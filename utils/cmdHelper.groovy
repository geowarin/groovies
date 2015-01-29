import com.beust.jcommander.*

void cmdLine(Class argsClass, Closure c) {
    def arguments = argsClass.newInstance()
    try {
        def commander = new JCommander(arguments, args)
        commander.setProgramName(argsClass.simpleName)
        if (arguments.hasProperty('help') && arguments.help) {
            StringBuilder sb = new StringBuilder()
            commander.usage(sb)
            println sb.readLines().head()
            println argsClass.getAnnotation(Parameters)?.commandDescription() ?: ''
            println sb.readLines().tail().join('\n')
        } else {
            c.call(arguments)
        }
    } catch (e) {
        System.err.println e.message
    }
}

return this.&cmdLine