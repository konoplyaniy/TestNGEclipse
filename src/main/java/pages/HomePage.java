package pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import base.BasePage;

public class HomePage extends BasePage {

	public HomePage(EventFiringWebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//div[contains(@class, 'widget-NavigationContainer')]/ul[@widget-child='navigation']//a[@target='_self']")
	private List<WebElement> LINKS;

	public List<String> getMenuLinksText() {
		return LINKS.stream().map(webElement -> (webElement.getText() + ": " + webElement.getCssValue("display")))
				.collect(Collectors.toList());
	}

}
