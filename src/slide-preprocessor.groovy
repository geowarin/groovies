#!/usr/bin/env groovy

import java.nio.file.Files

import static SlideCompiler.basename
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

/**
 *
 * Date: 22/09/2014
 * Time: 13:19
 * @author Geoffroy Warin (http://geowarin.github.io)
 */

class SlidePreprocessor {
  static private cli
  static private File inputFile

  static void execute(args) {
    def options = parseOptions(args)
    String[] types = options.t == 'both' ? ['deck', 'pdf'] : [options.t]

    for (String type : types) {
      def newText = type == 'deck' ? handleDeck(inputFile.text) : handlePdf(inputFile.text)

      def temp = File.createTempFile("tmp-slide", ".adoc", new File('.'))
      temp.deleteOnExit()
      temp << newText

      type == 'deck' ? SlideCompiler.compileDeck(temp, new File(basename(inputFile) + '.html'))
        : SlideCompiler.compilePdf(temp, new File(basename(inputFile) + '.pdf'))
    }
  }

  static def parseOptions(args) {
    cli = new CliBuilder(usage: 'slide')
    cli.t(args: 1, argName: 'type', "type ('pdf', 'deck' or 'both')", required: true)
    cli.f(args: 1, argName: 'file', 'file to convert', required: true)
    def options = cli.parse(args)
    options || System.exit(1)

    inputFile = new File(options.f)
    ['deck', 'pdf', 'both'].contains(options.t) || exitWithMessage('Unknown type ' + options.t)
    inputFile.exists() || exitWithMessage("File not found ${inputFile} ")
    options
  }

  static void exitWithMessage(String message) {
    System.err.println(message)
    cli.usage()
    System.exit(1)
  }


  static String handlePdf(String text) {
    def newText = hideTaggedBlocks(text, /hidePdf/)
    hideTaggedTitles(newText, /hidePdf/)
  }

  static String handleDeck(String text) {
    def newText = hideTaggedBlocks(text, /hideDeck/)
    newText = hideTaggedTitles(newText, /hideDeck/)
    setLevelTwoToTaggedTitles(newText, /newSlide/)
  }

  static String hideTaggedBlocks(String text, String tag) {
    def regexp = /(?sm)(\[/ + tag + /\])\s-+(.*)-+\s?/
    text.replaceAll(regexp, '')
  }

  static String hideTaggedTitles(String text, String tag) {
    def regexp = /\[/ + tag + /\]\n=+.*\n/
    text.replaceAll(regexp, '')
  }

  static String setLevelTwoToTaggedTitles(text, String tag) {
    def regexp = /(?sm)(\[/ + tag + /\])\s(={2,})\s*(\w+)/
//        def removeOneEqualToTitle = { "${it[2][1..-1]} ${it[3]}" }
    text.replaceAll(regexp, { "== ${it[3]}" })
  }

}

class SlideCompiler {

  static void compilePdf(File input, File output) {
    execute("asciidoctor-pdf $input.absolutePath")

    def inputFolder = input.getParentFile()
    def compiled = new File(inputFolder, basename(input) + '.pdf')

    def moved = Files.move(compiled.toPath(), output.toPath(), REPLACE_EXISTING)
    new File(compiled.getParentFile(), basename(input) + '.pdfmarks').delete()

    println "successfully compile ${moved.toFile().absolutePath}"
  }

  static void compileDeck(File input, File output) {
    execute("asciidoctor -T /Users/aziphael/dev/tools/asciidoctor/asciidoctor-backends/haml $input.absolutePath")
    def compiled = new File(input.getParentFile(), basename(input) + '.html')
    def moved = Files.move(compiled.toPath(), output.toPath(), REPLACE_EXISTING)
    println "successfully compiled ${moved.toFile().absolutePath}"
  }

  static int execute(String command) {
    def proc = command.execute()
    def resultValue = proc.waitFor()
    if (resultValue != 0) {
      throw new Error(proc.err.text)
    }
    println proc.in.text
    return resultValue
  }

  static String basename(File file) {
    file.name.lastIndexOf('.').with { it != -1 ? file.name[0..<it] : file.name }
  }
}

SlidePreprocessor.execute(args)
