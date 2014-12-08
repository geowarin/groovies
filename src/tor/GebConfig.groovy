import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile

driver = {
  FirefoxProfile profile = new FirefoxProfile()
  profile.setPreference('network.proxy.type', 1)
  profile.setPreference('network.proxy.socks', '127.0.0.1')
  profile.setPreference('network.proxy.socks_port', 9050)
  new FirefoxDriver(profile)
}