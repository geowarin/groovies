#!/usr/bin/env groovy

@Grapes(
  @Grab('log4j:log4j:1.2.17')
)
import groovy.util.logging.Log4j
import org.apache.log4j.Level

class Output {
  static Level level = Level.DEBUG;
}

abstract class DefaultAppendable implements Appendable {

  @Override
  Appendable append(CharSequence csq) throws IOException {
    write(csq)
    return this
  }

  @Override
  Appendable append(CharSequence csq, int start, int end) throws IOException {
    write(csq.subSequence(start, end))
    return this
  }

  @Override
  Appendable append(char c) throws IOException {
    write(c.toString())
    return this
  }

  abstract void write(String s)
}

@Log4j
class LogAppendable extends DefaultAppendable {
  public Level level
  LogAppendable() {
    log.level = Output.level
//    log.addAppender(new FileAppender(new TTCCLayout(), 'myscript.log'));
  }

  @Override
  void write(String s) {
    if (s.trim())
      log.log(level, s)
  }
}

Output.level = Level.INFO
Process proc = ['ls', '-l'].execute()
proc.waitForProcessOutput(new LogAppendable([level: Level.DEBUG]), new LogAppendable([level: Level.ERROR]))
