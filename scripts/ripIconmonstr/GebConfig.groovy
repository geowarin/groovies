import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile

driver = {
  FirefoxProfile firefoxProfile = new FirefoxProfile();
  firefoxProfile.setPreference("browser.download.folderList", 2);
  firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
  firefoxProfile.setPreference("browser.download.dir", System.getProperty("user.home") + '/icons');
  firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "image/png");
  firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "image/svg+xml");
  def firefoxDriver = new FirefoxDriver(firefoxProfile)
//  SharedResources.instance.browser = firefoxDriver
//  firefoxDriver.manage().window().maximize()
  firefoxDriver
}
