#!/usr/bin/env groovy
import com.beust.jcommander.*
import com.beust.jcommander.converters.FileConverter

Closure cmd = evaluate(new File("../../utils/cmdHelper.groovy"))

@Parameters(commandDescription = 'My program')
class HelloProgram {
    @Parameter(names = ['-f', '--file'], description = 'File to load. Can be specified multiple times.', required = true, converter = FileConverter)
    List<File> files

    @Parameter(names = ['-d', '--debug'], description = 'debug', hidden = true)
    boolean debug

    @Parameter(names = ['-h', '--help'], description = 'Show this help.', help = true)
    boolean help
}

cmd(HelloProgram) { HelloProgram args ->
    args.files.each { println "file: ${it.name}" }
}
