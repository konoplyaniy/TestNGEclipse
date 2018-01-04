package utils;

import org.apache.commons.io.FileUtils;
import org.deepsymmetry.GifSequenceWriter;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import utils.LogForTest;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import static base.BasicTest.*;
import static utils.DriverListener.*;
import static utils.LogForTest.*;

public class TestListener extends TestListenerAdapter {
	public static String PC_NAME = "";
	public static String TEST_CLASS = "";
	public static String TEST_LOCALE = "";
	public static String OS = "";
	public static String AVERAGE_TEST_DURATION;	
	private static String TEST_NAME = "";
	private static String TEST_PARAMETERS = "";
	private static ArrayList<File> screenNamesList = new ArrayList<>();
	private static String date = "";
	private static String failedTestsForEmail = "";
	private String currentURL;
	
	public static String refactorForWords(String words) {
		StringBuilder wordsForRefactor = new StringBuilder();
		ArrayList<Character> testNameArr = new ArrayList<>();
		char[] testNameCharArray = words.toCharArray();
		for (char chars : testNameCharArray) {
			if (Character.isUpperCase(chars)) {
				testNameArr.add(' ');
				testNameArr.add(chars);
			} else
				testNameArr.add(chars);
		}
		for (Character ch : testNameArr) {
			wordsForRefactor.append(ch);
		}
		return wordsForRefactor.toString().toUpperCase().trim();
	}

	private static void screenForTestFailure() {
		String filepath;
		try {
			if (driver != null) {
				File scrFile = driver.getScreenshotAs(OutputType.FILE);
				date = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
				filepath = SCREEN_FOLDER + TEST_CLASS + File.separator + date + "_" + TEST_NAME + "_LOCALE_"
						+ TEST_LOCALE + ".png";
				FileUtils.copyFile(scrFile, new File(filepath));
				screenNamesList.add(new File(filepath));
			}
		} catch (IOException e) {
			LogForTest.LOGGER.error("Can't create screen for test failure, occur error", e);
		}
	}	

	public static String getFailedTestsForEmail() {
		return failedTestsForEmail;
	}

	private static String trimString(String stringToTrim) {
		return stringToTrim.length() > 20 ? stringToTrim.substring(0, 20) : stringToTrim;
	}

	private void writeErrorTraceToFile() {
		try {
			if (errorTrace.size() != 0) {
				File path = new File(TMP_FOLDER);
				if (!path.exists()) {
					path.mkdir();
				}
				File file = new File(TMP_FOLDER + date + TEST_NAME + ".txt");
				PrintWriter writer = new PrintWriter(file);
				for (String line : errorTrace) {
					writer.print(System.lineSeparator());
					writer.print(line);
				}
				screenNamesList.add(file);
				writer.close();
			}
		} catch (IOException e) {
			LogForTest.LOGGER.error("Can't create error stack trace file, occur error", e);
		}
	}

	@Override
	public void onTestStart(ITestResult tr) {
		resetLogLists();		
		stepsCount = 1;

		/* INIT TEST HEADER VARIABLES */
		TEST_PARAMETERS = Arrays.toString(tr.getParameters());
		TEST_CLASS = tr.getTestClass().getRealClass().getSimpleName();
		TEST_NAME = "[" + tr.getName() + "]";
		OS = getOS();
		PC_NAME = getPcName();

		/* PRINT TEST HEADER */
		LogForTest.header("Date: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
		LogForTest.header("Browser: " + BROWSER_VERSION);
		LogForTest.header("PC name: " + PC_NAME);
		LogForTest.header("OS: " + OS);
		LogForTest.header("Class: " + TEST_CLASS);
		LogForTest.header("Test: " + TEST_NAME);
		LogForTest.header("Parameters: " + TEST_PARAMETERS);
		super.onTestStart(tr);
	}

	private void saveImage() throws IOException {
		date = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
		String fileDirectory = SCREEN_FOLDER + TEST_CLASS + File.separator;
		FileUtils.forceMkdir(new File(fileDirectory));
		String filePath = fileDirectory + date + "_" + TEST_NAME + "_LOCALE_" + TEST_LOCALE + ".gif";
		BufferedImage[] bufferedImages = new BufferedImage[animationImage.size()];
		for (int x = 0; x < animationImage.size(); x++) {
			bufferedImages[x] = ImageIO.read(animationImage.get(x));
		}
		ImageOutputStream output = new FileImageOutputStream(new File(filePath));
		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 1000, true);
		for (BufferedImage img : bufferedImages) {
			writer.writeToSequence(img);
		}
		writer.close();
		screenNamesList.add(new File(filePath));
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		LogForTest.info("Test \"" + tr.getName() + "\" successfully passed");		
		super.onTestSuccess(tr);
	}

	@Override
	public void onTestSkipped(ITestResult tr) {		
		super.onTestSkipped(tr);
		LogForTest.info("Test \"" + tr.getName() + "\" skipped");
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		currentURL = driver == null ? "Empty value" : driver.getCurrentUrl();

		errorTrace.addAll(getBrowserConsoleLog());
		animationImage.add(driver.getScreenshotAs(OutputType.FILE));
		try {
			saveImage();
		} catch (IOException e) {
			LogForTest.LOGGER.error("Occur error while saving image", e);
		} catch (OutOfMemoryError e) {
			LogForTest.info("Unavailable to save too big .gif animation. OutOfMemoryError!");
		}
		LogForTest.error(String.valueOf(tr.getThrowable().getMessage()));
		if (!TEST_CLASS.equals("WebcloudCheckTest")) {
			screenForTestFailure();
		}		

		writeErrorTraceToFile();
		super.onTestFailure(tr);

		screenNamesList = new ArrayList<>();
		errorTrace = new ArrayList<>();
		for (File anim : animationImage) {
			try {
				FileUtils.forceDelete(anim);
			} catch (IOException e) {
				LogForTest.LOGGER.error("Can't delete temp files (used for creating animation), occur error", e);
			}
		}
	}

	private void sendDevEmail() {

	}

	@Override
	public void onFinish(ITestContext testContext) {
		List<ITestResult> failedTestList = getFailedTests();
		Map<String, ArrayList<String>> failedTestMap = new HashMap<>();

		failedTestList.forEach(failedTest -> {
			String[] temp = failedTest.getMethod().getTestClass().getName().split("\\.");
			String testClass = temp[temp.length - 1];
			String testName = "<br>&nbsp;&nbsp;&nbsp;&nbsp;[" + failedTest.getName();
			String parameters = "";
			if (failedTest.getParameters() != null && failedTest.getParameters().length >= 1) {
				parameters = " - " + trimString(failedTest.getParameters()[0].toString());
			}

			if (failedTestMap.containsKey(testClass)) {
				failedTestMap.get(testClass).add(testName + parameters + "]");
			} else {
				String finalParameters = parameters;
				failedTestMap.put(testClass, new ArrayList<String>() {
					private static final long serialVersionUID = 1L;
					{
						add(testName + finalParameters + "]");
					}
				});
			}
		});

		StringBuilder failedTestStringBuilder = new StringBuilder();
		failedTestMap.forEach((key, value) -> {
			failedTestStringBuilder.append("<strong>").append(key).append("</strong>");
			value.forEach(failedTestStringBuilder::append);
		});
		if (!failedTestStringBuilder.toString().isEmpty() && !failedTestStringBuilder.toString().equals("")) {
			failedTestsForEmail = failedTestsForEmail + "<br>" + failedTestStringBuilder.toString();
		}
		failedTestList.addAll(getPassedTests());
		AVERAGE_TEST_DURATION = getLongAsTime((long) failedTestList.stream()
				.mapToLong(test -> (test.getEndMillis() - test.getStartMillis())).average().orElse(0.0));

		super.onFinish(testContext);
	}

	private String getLongAsTime(long timeDiff) {
		long diffSeconds = timeDiff / 1000 % 60;
		long diffMinutes = timeDiff / (60 * 1000) % 60;
		long diffHours = timeDiff / (60 * 60 * 1000) % 24;
		return (diffHours == 0 ? "" : diffHours + "h ") + (diffMinutes == 0 ? "" : diffMinutes + "m ")
				+ (diffSeconds == 0 ? "" : diffSeconds + "s ");
	}
}
