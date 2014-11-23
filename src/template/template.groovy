#!/usr/bin/env groovy
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine
import org.apache.commons.cli.HelpFormatter

def template = new PowerTemplate()
template.parseOptions(args)
template.run()

class PowerTemplate {
  def cli
  File template
  def output
  File[] bindingFiles

  void run() {
    def binding = getBindings();
    binding += [
      env: System.getenv(),
      cmd: { s ->
        def proc = s.execute()
        proc.waitFor()
        return proc.in.text
      }
    ]
    def engine = new SimpleTemplateEngine()
    def template = engine.createTemplate(template.text).make(binding)
    output.class == File && output.exists() && output.delete()
    output << template.toString()
  }

  Map getBindings() {
    bindingFiles
      .collect { it.exists() ? new JsonSlurper().parse(it) : [:] }
      .inject([:]) { acc, value -> acc + value }
  }

  void parseOptions(args) {
    cli = new CliBuilder(
      formatter: new MyHelp(),
      header: 'Power Template® by geowarin.',
      usage: 'template -f [file] (-o outputFile) (-v values1.json values2.json)',
      footer: '''
Use the power of groovy to templatize a file.
It can contain values within ${}.
You can use any groovy expressions.

You can use json files to put values into the template.
Use ${env['someProperty']} to access environment variables.
Use ${cmd('command')} to include the output of a command.
''')
    cli.f(args: 1, argName: 'file', 'The file to templatize', required: true)
    cli.o(args: 1, argName: 'output', 'Optional. Redirect the result to a file')
    cli.v(args: -2, argName: 'vars', 'Optional. Point to one or several json files to put values into the template')
    OptionAccessor options = cli.parse(args)
    options || System.exit(1)

    template = new File(options.f)
    template.exists() || exitWithMessage("File not found ${template}")
    output = options.o ? new File(options.o) : System.out
    bindingFiles = options.vs.collect { new File(it) }
  }

  void exitWithMessage(String message) {
    System.err.println(message)
    cli.usage()
    System.exit(1)
  }
}

// Hack to preserve line endings in usage. Don't care about max width
class MyHelp extends HelpFormatter {
  protected int findWrapPos(String text, int width, int startPos) {
    return text.lastIndexOf('\n', startPos)
  }
}
