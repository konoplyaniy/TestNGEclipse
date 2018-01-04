package base;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import utils.DriverListener;
import utils.LogForTest;

@Listeners(utils.TestListener.class)
public class BasicTest {
	public static int stepsCount = 1;
	public static EventFiringWebDriver driver;
	public static String BROWSER_VERSION;
	public final static String SCREEN_FOLDER = "C:\\Automation\\Screenshot\\";
	public final static String TMP_FOLDER = "C:\\\\Automation\\\\Tmp\\\\";
	public final static String BASE_URL = "https://expert-integration.bt.systems/";

	@BeforeSuite
	public void tearUp() {
		if (driver == null)
			initEnvironment();
	}

	@AfterSuite(alwaysRun = true)
	public void tearDown() {
		if (driver != null)
			driver.quit();
	}

	private static void initEnvironment() {
		System.out.println("SERVICE MESSAGE: Set driver folder property");
		System.setProperty("webdriver.chrome.driver", "C:\\Automation\\chromedriver\\chromedriver.exe");

		System.out.println("SERVICE MESSAGE: Set driver options");
		String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; Dreamscape/1.0;) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36";
		ChromeOptions co = new ChromeOptions();
		co.addArguments("--user-agent=" + userAgent);

		co.addArguments("--window-size=1920,1080");
		co.addArguments("--headless");

		System.out.println("SERVICE MESSAGE: Set driver Desired capabilities");
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability("pageLoadStrategy", "none");
		cap.setCapability(ChromeOptions.CAPABILITY, co);
		System.out.println("SERVICE MESSAGE: Switch on driver Logging, set levels");
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		System.out.println("SERVICE MESSAGE: Init driver");
		@SuppressWarnings("deprecation")
		WebDriver webDriver = new ChromeDriver(cap);
		Capabilities caps = ((RemoteWebDriver) webDriver).getCapabilities();
		BROWSER_VERSION = caps.getBrowserName() + ", version " + caps.getVersion();
		driver = new EventFiringWebDriver(webDriver);
		driver.register(new DriverListener("#FFFF00 ", 1, 1, TimeUnit.MILLISECONDS));
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
	}

	public void goToPage(String url) {
		if (driver == null) {
			initEnvironment();
		}
		try {
			String currentUrl = driver.getCurrentUrl().split("#")[0];
			if (!driver.getCurrentUrl().split("#")[0].equals(url)) {				
				driver.get(url);
				Thread.sleep(2000);
				if (driver.getCurrentUrl().split("#")[0].equals(currentUrl)) {
					driver.get(url);
				}
			} else {
				LogForTest.info("Already on " + url);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getBrowserConsoleLog() {
		ArrayList<String> consoleLog = new ArrayList<>();
		if (driver != null) {
			LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
			for (LogEntry entry : logEntries) {
				consoleLog.add("CONSOLE LOG : " + entry.getLevel() + "  " + entry.getMessage());
			}
		}
		return consoleLog;
	}

}
