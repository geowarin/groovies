import java.text.NumberFormat

import static java.text.NumberFormat.percentInstance

/**
 *
 * Date: 12/11/14
 * Time: 23:24
 * @author Geoffroy Warin (http://geowarin.github.io)
 */

def max = 100
for (int i = 1; i < max; i++) {
  showProgress(i, max)
  sleep 20
}

private void showProgress(int current, int max, int barSize = 20) {
  def percent = getPercentInstance().format(current / max)
  def bars = (current * barSize).intdiv(max)
  print '|' + ('=' * bars) + (' ' * (barSize - bars)) + "| $percent\r"
}
