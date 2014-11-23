import static java.text.NumberFormat.percentInstance

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
