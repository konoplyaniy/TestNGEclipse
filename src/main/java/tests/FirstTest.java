package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BasicTest;
import utils.LogForTest;

public class FirstTest extends BasicTest {
	
	@DataProvider
	public static Object[][] getData() {
		return new Object[][] { { "1" }, { "2" }, { "3" }, };
	}

	@Test(dataProvider = "getData")
	public void test0(String i) {
		LogForTest.info("test0. Iteration: " + i);
		goToPage(BASE_URL);
		LogForTest.info("# " + i + driver.getTitle());
	}

	@Test(dataProvider = "getData")
	public void test1(String i) {
		LogForTest.info("test1");
		goToPage(BASE_URL + "Anmelden/");
		LogForTest.info("# " + i + driver.getTitle());
		Assert.assertEquals("Domain Names Search - Domain Name Registration Australia | Crazy Domains AUq",
				driver.getTitle(), "Wrong page title");
	}
	

	@Test
	public void test2() {
		LogForTest.info("test2");
		goToPage("https://www.crazydomains.com.au/web-hosting/");
		LogForTest.info("# " + driver.getTitle());
		Assert.assertEquals("Web Hosting Australia Only $2.48 | Crazy Domains AU", driver.getTitle(),
				"Wrong page title");
	}

	@Test
	public void test3() {
		LogForTest.info("test3");
		goToPage("https://www.crazydomains.com.au/wordpress-hosting/");
		Assert.assertEquals("WordPress Hosting - Transfer WordPress Site | Crazy Domains AU", driver.getTitle(),
				"Wrong page title");
	}
}