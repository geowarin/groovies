#!/usr/bin/env groovy
@Grapes(
  @Grab('log4j:log4j:1.2.17')
)
import org.apache.log4j.Level
import org.apache.log4j.Logger

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

class LogAppendable extends DefaultAppendable {
  Level level

  @Override
  void write(String s) {
    if (s.trim())
      Logger.rootLogger.log(level, s)
  }
}

Logger.rootLogger.level = Level.DEBUG
// Optional : configure appenders
//Logger.rootLogger.removeAllAppenders()
//Logger.rootLogger.addAppender new ConsoleAppender([layout: new PatternLayout('%m%n'), writer: System.out.newWriter()])
//Logger.rootLogger.addAppender new FileAppender(new TTCCLayout(), 'myscript.log')

Process proc = ['ls', '-l'].execute()
proc.waitForProcessOutput(new LogAppendable([level: Level.DEBUG]), new LogAppendable([level: Level.ERROR]))
