package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.LogForTest;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import static base.BasicTest.BASE_URL;
import static base.BasicTest.driver;
import static utils.TestListener.TEST_CLASS;

public class DriverListener implements WebDriverEventListener {
	public static String TEST_SYSWEB = "";
	public static String TRACE = "";
	public static List<File> animationImage = new ArrayList<>();
	static List<String> errorTrace = new ArrayList<>();
	private final int count;
	private final String color;
	private long interval;
	private String currentUrl;
	private String goToUrl;

	public DriverListener(String color, int count, long interval, TimeUnit unit) {
		this.color = color;
		this.count = count;
		this.interval = TimeUnit.MILLISECONDS.convert(Math.max(0, interval), unit);
	}

	public static String getOS() {
		return System.getProperty("os.name");
	}

	public static String getPcName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Method for getting class from StackTrace
	 *
	 * @return - Class
	 */
	private Class getPageObjectRunningClass() {
		Class pageClass = null;
		Throwable t = new Throwable();
		StackTraceElement trace[] = t.getStackTrace();
		String list[];
		if (System.getProperty("os.name").equals("Linux")) {
			list = new File("src/test/java").list();
		} else {
			list = new File("src\\test\\java").list();
		}

		try {
			for (StackTraceElement tr : trace) {
				String packageName = String.valueOf(tr).split("\\.")[0];
				if (Arrays.toString(list).contains(packageName)) {
					pageClass = Class.forName(tr.getClassName());
					if (!pageClass.equals(this.getClass()) && pageClass.getDeclaredAnnotation(Test.class) == null) {
						return pageClass;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageClass;
	}

	/**
	 * Method find WebElement Name by xpath
	 *
	 * @param webElement
	 *            - get WebElement
	 * @return - String WebElement Name
	 */
	private String getWebElementName(WebElement webElement) {
		String[] webElementXPATH = String.valueOf(webElement).split("] -> ");
		String element = webElementXPATH[1]
				.substring(webElementXPATH[1].indexOf(":") + 2, webElementXPATH[1].length() - 1).replace("]]", "]");
		Field[] lastPublicFields = getPageObjectRunningClass().getDeclaredFields();
		for (Field field : lastPublicFields) {
			Annotation[] annotations = field.getDeclaredAnnotations();
			if (Arrays.toString(annotations).contains(element)) {
				return "[" + field.getName().replace("_", " ") + "]";
			}
		}
		return "[" + webElement.getText() + "]";
	}

	/**
	 * Method for paint element
	 *
	 * @param color
	 *            String color
	 * @param element
	 *            Current WebElement
	 * @param js
	 *            JS executor
	 */
	private void changeColor(String color, WebElement element, JavascriptExecutor js) {
		js.executeScript("arguments[0].style.backgroundColor = '" + color + "'", element);
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for light click
	 *
	 * @param element
	 *            get WebElement for light up
	 */
	private void flash(WebElement element) {
		JavascriptExecutor js = driver;
		js.executeScript("scrollTo(0, "
				+ (element.getLocation().getY() - driver.manage().window().getSize().getHeight() / 2) + ")"); // Super
																												// scroll
		String bgColor = element.getCssValue("backgroundColor");
		for (int i = 0; i < count; i++) {
			changeColor(color, element, js);
			animationImage.add((driver.getScreenshotAs(OutputType.FILE)));
			changeColor(bgColor, element, js);
		}
	}

	public void beforeNavigateTo(String s, WebDriver webDriver) {
		goToUrl = s;
	}

	public void afterNavigateTo(String s, WebDriver webDriver) {
		LogForTest.info("Go to " + goToUrl);
		if (!driver.getCurrentUrl().split("#")[0].equals(goToUrl)) {
			LogForTest.info("But get: " + driver.getCurrentUrl());
		}
	}

	public void beforeNavigateBack(WebDriver webDriver) {
	}

	public void afterNavigateBack(WebDriver webDriver) {
	}

	public void beforeNavigateForward(WebDriver webDriver) {
	}

	public void afterNavigateForward(WebDriver webDriver) {
	}

	public void beforeNavigateRefresh(WebDriver webDriver) {
	}

	public void afterNavigateRefresh(WebDriver webDriver) {
	}

	public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {
	}

	public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
	}

	public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
		currentUrl = driver.getCurrentUrl().split("#")[0];
		flash(webElement);
		if (webElement.getText().length() == 0) {
			LogForTest.info("Click on " + getWebElementName(webElement));
		} else if (webElement.getText().length() != 0) {
			LogForTest.info("Click on " + getWebElementName(webElement) + " with text \""
					+ webElement.getText().replaceAll("\\n", " ") + "\"");
		}
	}

	/**
	 *
	 * @param webElement
	 *            current web element used by driver in real time
	 * @param webDriver
	 *            current driver instance
	 */
	public void afterClickOn(WebElement webElement, WebDriver webDriver) {
		if (!driver.getCurrentUrl().split("#")[0].equals(currentUrl)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				LogForTest.LOGGER.info("Error while waiting after click on web element", e);
			}
			LogForTest.info("REDIRECT TO " + driver.getCurrentUrl());
		}
	}

	@Override
	public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
		if (webElement.getAttribute("value") != null) {
			flash(webElement);
		}
	}

	@Override
	public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
		if (webElement.getAttribute("value") == null) {
			LogForTest.LOGGER.debug("Web element haven't value");
		} else if (webElement.getAttribute("value").length() != 0) {
			LogForTest.info("Input \"" + webElement.getAttribute("value") + "\" in " + getWebElementName(webElement));
		} else if (webElement.getAttribute("value").length() == 0) {
			LogForTest.info("Clear " + getWebElementName(webElement));
		}
	}

	public void beforeScript(String s, WebDriver webDriver) {
	}

	public void afterScript(String s, WebDriver webDriver) {
	}

	public void onException(Throwable throwable, WebDriver webDriver) {
		errorTrace.add(throwable.getMessage());
		for (StackTraceElement str : throwable.getStackTrace()) {
			errorTrace.add(String.valueOf(str));
		}
	}

	@Override
	public void beforeAlertAccept(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterAlertAccept(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterAlertDismiss(WebDriver driver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeAlertDismiss(WebDriver driver) {
		// TODO Auto-generated method stub

	}

}
