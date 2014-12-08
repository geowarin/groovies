#!/usr/bin/env groovy
@Grapes([
  @Grab("org.gebish:geb-core:0.9.0"),
  @Grab("org.seleniumhq.selenium:selenium-support:2.44.0"),
  @Grab("org.seleniumhq.selenium:selenium-firefox-driver:2.44.0")
])
import geb.Browser
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement

import static java.text.NumberFormat.getPercentInstance

def url = 'http://iconmonstr.com/collection/'
def icons = 0
def nbIcons = 2651
def downloadDir = new File(System.getProperty("user.home"), '/icons')
def type = 'svg'

Browser.drive() {
  go url
  def links = []
  links.addAll($('a.thumbnail-grid-link').collect { it.attr('href') })
  links.addAll($('a.thumbnail-link').collect { it.attr('href') })

  while (!links.isEmpty()) {
    String link = links.pop()
    go link

    if ($('body').classes().contains('single')) {

      try {
        def filesBefore = downloadDir.listFiles()
        clickOn(driver, "li.tab-$type")
        clickOn(driver, "#checkbox-$type")
        sleep 20
        clickOn(driver, "input#btn-$type")
        if (type == 'png') {
          waitFor { $('input#btng') }
          clickOn(driver, 'input#btng')
        }
//        println "dowloaded icon $link"
        waitForDownload(filesBefore, downloadDir)
        showProgress(++icons, nbIcons, 50)
      } catch (Exception ignored) {
        System.err.println("Could not download $link, we'll retry later")
        links.push(link)
      }

    } else {
      links.addAll($('a.thumbnail-grid-link').collect { it.attr('href') })
      links.addAll($('a.thumbnail-link').collect { it.attr('href') })
//      println "crawled $link, ${links.size()} left to browse"
    }
  }

  sleep 5000 // just to be sure downloads are finished
  println "Done ! $icons icons downloaded"

}.quit()

void waitForDownload(File[] filesBefore, File downloadDir) {
  def difFiles = []
  while (difFiles.size() == 0) {
    difFiles = downloadDir.listFiles() - filesBefore
    sleep(100)
  }
}

// Hack to circumvent error element not being displayed
private void clickOn(driver, String selector) {
  JavascriptExecutor js = driver as JavascriptExecutor;
  WebElement element = driver.findElement(By.cssSelector(selector));
  js.executeScript("arguments[0].click();", element);
}

private void showProgress(int current, int max, int barSize = 20) {
  def percent = getPercentInstance().format(current / max)
  def bars = (current * barSize).intdiv(max)
  print '|' + ('=' * bars) + (' ' * (barSize - bars)) + "| $percent ($current/$max)\r"
}
