package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BasicTest;
import utils.LogForTest;

public class SecondTest extends BasicTest {

	@Test
	public void test3() {
		LogForTest.info("test3");
		goToPage("https://www.crazydomains.com.au/wordpress-hosting/");
		Assert.assertEquals("WordPress Hosting - Transfer WordPress Site | Crazy Domains AU", driver.getTitle(),
				"Wrong page title");
	}
}
