package base;

import org.openqa.selenium.*;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utils.LogForTest;

@SuppressWarnings("unused")
public class BasePage {

	protected EventFiringWebDriver driver;

	public BasePage(EventFiringWebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	/**
	 * Delete token, when check domain Search fields
	 */
	public static String deleteTokenFromUrl(String url) {
		String newUrl = url.substring(url.indexOf("?token") + 1, url.lastIndexOf("&") + 1);
		return url.replace(newUrl, "");
	}

	/**
	 * Using, when check Bulk domain Search Fields
	 */
	public static void pressEnter(WebElement webElement) {
		webElement.sendKeys(Keys.ENTER);
	}

	protected void scrollToWebElement(WebElement element) {
		try {
			JavascriptExecutor js = driver;
			js.executeScript("scrollTo(0, "
					+ (element.getLocation().getY() - driver.manage().window().getSize().getHeight() / 2) + ")"); // Super
																													// scroll
		} catch (Exception e) {
			LogForTest.LOGGER.warn("Can't scroll to WebElement", e);
		}
	}
	
	private void highlightElement(WebElement webElement) {
		driver.executeScript("arguments[0].style.backgroundColor = '#FFFF00 '", webElement);
	}

	public String getPageDescription() {
		return "Base page";
	}

	public void waitForElement(WebElement element, String elementDescription) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			Assert.fail("Timeout Exception: Can't wait for " + elementDescription + "\n" + e.getMessage());
		}
	}

	public void waitForElementIsInvisible(WebElement element) {
		try {
			new WebDriverWait(driver, 10).until(ExpectedConditions.stalenessOf(element));
		} catch (TimeoutException e) {
			Assert.fail("WebDriver Wait for element is invisible timeout Exception\n" + e.getMessage());
		}
	}

	public void waitForElementIsHide(WebElement element) {
		new WebDriverWait(driver, 10).until(ExpectedConditions.attributeContains(element, "overflow", "hidden"));
	}

	public void waitForElementIsClickable(WebElement element) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.elementToBeClickable(element));
		} catch (TimeoutException e) {
			Assert.fail("WebDriver Wait for element is clickable timeout Exception\n" + e.getMessage());
		}
	}

	public void waitUntilTextPresentInElementValue(WebElement SomeLocatorByXpath, String text) {
		try {
			new WebDriverWait(driver, 15)
					.until(ExpectedConditions.textToBePresentInElementValue(SomeLocatorByXpath, text));
		} catch (TimeoutException e) {
			Assert.fail("Timeout Exception to wait " + text + " value");
		}
	}

	public void waitUntilTextPresentInElement(WebElement webElement, String text) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.textToBePresentInElement(webElement, text));
		} catch (TimeoutException e) {
			Assert.fail("Timeout Exception to wait " + text + " text");
		}
	}

	public String getCurrentUrl() {
		return driver.getCurrentUrl().split("#")[0];
	}

	public void waitURLContainsText(String text) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.urlContains(text));
		} catch (TimeoutException e) {
			Assert.fail("Exception while wait text \"" + text + "\" in url\n" + e.getMessage());
		}
	}

	/**
	 * Wait for URL contains text1 or text2 for 15 second, if texts will be not
	 * present in URl test will be failed
	 *
	 * @param text1
	 *            some text to be present in URL
	 * @param text2
	 *            some text to be present in URL
	 */
	public void waitURLContainsText(String text1, String text2) {
		try {
			new WebDriverWait(driver, 15).until(ExpectedConditions.or(ExpectedConditions.urlContains(text1),
					ExpectedConditions.urlContains(text2)));
		} catch (TimeoutException e) {
			Assert.fail("Exception while wait text \"" + text1 + " and " + text2 + "\" in url\n" + e.getMessage());
		}
	}

	public String getHiddenTextByWebElement(WebElement element) {
		try {
			return (String) driver.executeScript("return arguments[0].innerHTML", element);
		} catch (TimeoutException e) {
			Assert.fail("Can't return hidden text, occur error: \n" + e.getMessage());
			return "";
		}
	}

}
