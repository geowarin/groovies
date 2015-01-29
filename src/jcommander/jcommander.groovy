#!/usr/bin/env groovy
import com.beust.jcommander.*
import com.beust.jcommander.converters.FileConverter

@Parameters(commandDescription = 'My program')
class HelloProgram {
    @Parameter(names = ['-f', '--file'], description = 'File to load. Can be specified multiple times.', required = true, converter = FileConverter)
    List<File> files

    @Parameter(names = ['-d', '--debug'], description = 'debug', hidden = true)
    boolean debug

    @Parameter(names = ['-h', '--help'], description = 'Show this help.', help = true)
    boolean help
}

void cmdLine(Class argsClass, Closure c) {
    def arguments = argsClass.newInstance()
    try {
        def commander = new JCommander(arguments, args)
        commander.setProgramName(argsClass.simpleName)
        if (arguments.help) {
            StringBuilder sb = new StringBuilder()
            commander.usage(sb)
            println sb.readLines().head()
            println argsClass.getAnnotation(Parameters)?.commandDescription()?: ''
            println sb.readLines().tail().join('\n')
        } else {
            c.call(arguments)
        }
    } catch (e) {
        System.err.println e.message
    }
}

cmdLine(HelloProgram) { HelloProgram args ->
    args.files.each { println "file: ${it.name}" }
}
