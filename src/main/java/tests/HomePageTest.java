package tests;

import org.testng.annotations.Test;

import base.BasicTest;
import pages.HomePage;
import utils.LogForTest;

public class HomePageTest extends BasicTest {	
	
	@Test
	public void checkHeaderMenuUrl() {
		goToPage(BASE_URL);
		HomePage homePage = new HomePage(driver);
		homePage.getMenuLinksText().forEach(LogForTest::info);
	}
}
