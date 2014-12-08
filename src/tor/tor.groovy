#!/usr/bin/env groovy
@Grapes([
        @Grab("org.gebish:geb-core:0.9.0"),
        @Grab("org.seleniumhq.selenium:selenium-support:2.44.0"),
        @Grab("org.seleniumhq.selenium:selenium-firefox-driver:2.44.0")
])
import geb.Browser

Browser.drive() {
    go 'https://check.torproject.org/'
    waitFor { $('.content h1').displayed }
    assert $('.content h1').text().contains('Congratulations')

}.quit()