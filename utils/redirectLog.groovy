
/**
 * 
 * Date: 29/01/15
 * Time: 12:08
 * @author Geoffroy Warin (http://geowarin.github.io)
 */

@Grapes(
        @Grab('log4j:log4j:1.2.17')
)
import org.apache.log4j.Level
import org.apache.log4j.Logger

def redirectLog = { Level level ->
    return { String s ->
        if (s.trim())
            Logger.rootLogger.log(level, s)
    } as Appendable
}

Logger.rootLogger.level = Level.DEBUG
// Optional : configure appenders
//Logger.rootLogger.removeAllAppenders()
//Logger.rootLogger.addAppender new ConsoleAppender([layout: new PatternLayout('%m%n'), writer: System.out.newWriter()])
//Logger.rootLogger.addAppender new FileAppender(new TTCCLayout(), 'myscript.log')

Process proc = ['ls', '-l'].execute()
proc.waitForProcessOutput(redirectLog(Level.INFO), redirectLog(Level.ERROR))
