package ru.ifmo.mit.hw1

import joptsimple.*
import kotlin.system.exitProcess

import java.util.*
import org.jline.terminal.TerminalBuilder
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.Integer.min
import java.lang.StringBuilder
import java.nio.file.Paths
import kotlin.math.max

const val JOPTSIMPLE_SUPPRESS_EXIT = "joptsimple.suppressExit"


/**
 * This class allows to perform command execution, which introduced by functions
 */
class CommandRunner {

    /**
     * @param pipes which you can get from Parser.
     */
    fun commandParser(pipes: MutableList<String>): String {
        var res: String? = null
        pipes.forEachIndexed { idx, it ->
            res = execCommand(it.split(" ")[0], it.split(" ").drop(1), res)
        }
        return res!!
    }

    /**
     * Call needed function
     */
    private fun execCommand(command: String, opt: List<String>, arg: String?): String {
        when (command) {
            "cat" -> return cat(opt, arg)
            "wc" -> return wc(opt, arg)
            "echo" -> return echo(opt, arg)
            "pwd" -> return pwd(opt, arg)
            "exit" -> return exit(opt, arg)
            "grep" -> return grep(opt, arg)
        }
        return ""
    }

    private fun cat(opt: List<String>, arg: String?): String {
        var drop: Int? = null
        opt.reversed().forEachIndexed { index, s ->
            if (s[0] == '-' && drop == null) {
                drop = index

            }
        }
        if (drop == null) drop = opt.size

        val files = opt.subList(opt.size - drop!!, opt.size).toMutableList()
        if (!arg.isNullOrBlank()) files.add(arg)
        val optC = opt.subList(0, opt.size - drop!!)
        var ifShowTab = false
        var ifDel2Empty = false
        var ifShowNum = false
        var ifPrintDol = false
        var ifShowNumNonBlank = false

        with(getOptionParser()) {
            acceptsAll(listOf("b", "number-nonblank"), "number nonempty output lines, overrides -n")
            acceptsAll(listOf("E", "show-ends"), "display \\$ at end of each line")
            acceptsAll(listOf("n", "number"), "number all output lines")
            acceptsAll(listOf("s", "squeeze-blank"), "suppress repeated empty output lines")
            acceptsAll(listOf("T", "show-tabs"), "display TAB characters as ^I")
            parse(optC.toTypedArray()) { options ->
                if (files.isEmpty()) {
                    val sb = StringBuffer()
                    var currLine: String? = ""
                    while (true) {
                        currLine = readLine()
                        if (currLine.isNullOrBlank()) sb.append(currLine) else sb.append("")
                        if (currLine == "^C") break
                        sb.append("\n")
                    }
                    files.add(sb.toString())
                }
                if (options.has("T")) ifShowTab = true
                if (options.has("s")) ifDel2Empty = true
                if (options.has("n")) ifShowNum = true
                if (options.has("E")) ifPrintDol = true
                if (options.has("b")) {
                    ifShowNumNonBlank = true; ifShowNum = false
                }
            }
        }

        var curLine: Int = 0
        var is2Empty = false
        var sb = StringBuilder()
        val printLines = mutableListOf<String>()
        files.forEach { file ->
            File(Paths.get(file).toRealPath().toString()).forEachLine {
                val line = it
                if (ifDel2Empty) {
                    if (line.isBlank() || line.isNullOrEmpty()) if (is2Empty) return@forEachLine else is2Empty = true
                }
                if (ifShowNum) {
                    sb.append(curLine.toString())
                    sb.append(" ")
                    curLine++
                } else if (ifShowNumNonBlank) {
                    if (!line.isNullOrBlank()) {
                        sb.append(curLine.toString())
                        sb.append(" ")
                        curLine++
                    }
                }
                if (ifShowTab) {
                    line.replace("\t", "^I")
                }
                sb.append(line)
                if (ifPrintDol) {
                    sb.append("\$")
                }
                printLines.add(sb.toString())
                sb = StringBuilder()
            }
            printLines.add("\n")
        }

        printLines.forEach { sb.append(it.padStart(5, ' ')); sb.append("\n") }
        return sb.toString()
    }

    private fun wc(opt: List<String>, arg: String?): String {
        var argV = arg
        var optC = listOf<String>()
        if (argV == null) {
            if (opt.size == 0) {
                return ""
            } else {
                argV = opt.last()
                optC = opt.subList(0, opt.size - 1)
            }
        }

        val txt = arg!!
        val numOfLines = txt.lines().size
        val numOfBits = txt.length
        val numOfWords = txt.split(" ").filter { it.length > 0 }.size

        if (optC.isEmpty()) {
            return numOfLines.toString() + " " + numOfWords + " " + numOfBits
        }

        val sb = StringBuilder()

        with(getOptionParser()) {
            acceptsAll(kotlin.collections.listOf("c", "bytes"), "print the byte counts")
            acceptsAll(kotlin.collections.listOf("m", "chars"), "print the character counts")
            acceptsAll(kotlin.collections.listOf("l", "lines"), "print the newline counts")

            parse(optC.toTypedArray()) { options ->
                if (options.has("l")) {
                    sb.append(numOfLines)
                    sb.append(" ")
                }
                if (options.has("m")) {
                    sb.append(numOfWords)
                    sb.append(" ")
                }
                if (options.has("c")) {
                    sb.append(numOfBits)
                    sb.append(" ")
                }
            }
        }
        return sb.toString()
    }

    private fun echo(opt: List<String>, arg: String?): String {
        var optC = listOf<String>()
        var drop: Int? = null
        opt.reversed().forEachIndexed { index, s ->
            if (s.length > 0 && s[0] == '-' && drop == null) {
                drop = index

            }
        }
        if (drop == null) drop = opt.size

        val sb = StringBuilder()
        opt.subList(opt.size - drop!!, opt.size).forEach { sb.append(it); sb.append(" ") }
        val argV = sb.toString().dropLast(1)

        var parseEcraned = false
        var printNewLine = false
        with(getOptionParser()) {
            acceptsAll(listOf("n"), "do not output the trailing newline")
            acceptsAll(listOf("e"), "enable interpretation of backslash escapes")
            acceptsAll(listOf("E"), "disable interpretation of backslash escapes (default)")

            parse(optC.toTypedArray()) { options ->
                if (options.has("n")) {
                    printNewLine = true
                }
                if (options.has("E")) {
                    parseEcraned = false
                }
                if (options.has("e")) {
                    parseEcraned = true
                }
            }
        }
        if (parseEcraned) {
            // Kotlin allows only following to escape
            argV.replace("\\n", "\n")
            argV.replace("\\t", "\t")
            argV.replace("\\r", "\r")
        }
        if (printNewLine) {
            return argV + "\n"
        }
        return argV
    }

    private fun pwd(opt: List<String>, arg: String?): String {
        var argV = arg
        var printLogical = true

        with(getOptionParser()) {
            acceptsAll(listOf("L", "logical"), "use PWD from environment, even if it contains symlinks")
            acceptsAll(listOf("P", "physical"), "avoid all symlinks")
            parse(opt.toTypedArray()) { options ->
                if (options.has("L")) {
                    printLogical = true
                }
                if (options.has("P")) {
                    printLogical = false
                }
            }
        }
        if (printLogical) {
            return Paths.get("").toAbsolutePath().toString()
        }
        return Paths.get("").toRealPath().toString()
    }

    private fun exit(opt: List<String>, arg: String?): String {
        throw ExitCommand()
        return ""
    }

    fun grep(opt: List<String>, arg: String?): String {
        var argV = arg
        var ignoreCase = false
        var N: Int? = null
        var wordRegex = false
        var res = ""
        var optC = mutableListOf<String>()
        var flag = false
        opt.forEachIndexed() {  index, it ->
            if (it == "-A" || it == "--after-context") {
                try {
                    val tmp = it + opt[index + 1]
                    optC.add(tmp)
                    flag = true
                } catch (e: IndexOutOfBoundsException) {
                    throw IllegalArgumentException("Wrong value for -A")
                }
            } else if (flag) {
                flag = false
            } else {
                optC.add(it)
            }
        }
        //res = arg!!
        if (arg != null) {
            res = arg
        } else {
            res = cat(listOf(),optC.last())
            optC = optC.dropLast(1).toMutableList()
        }

        var regex = optC.last().toRegex()
        optC = optC.dropLast(1).toMutableList();

        with(getOptionParser()) {
            acceptsAll(
                kotlin.collections.listOf("i", "ignore-case"),
                "ignore case distinctions, so that characters that differ only in case match each other."
            )
            acceptsAll(
                kotlin.collections.listOf("w", "word-regexp"),
                "Select only those lines containing matches that form whole words.  The test is that the matching substring must either be at the beginning of the line, or preceded by a  non-word  constituent\n" +
                        "              character.   Similarly,  it  must be either at the end of the line or followed by a non-word constituent character.  Word-constituent characters are letters, digits, and the underscore.  This\n" +
                        "              option has no effect if -x is also specified.\n"
            )
            acceptsAll(
                kotlin.collections.listOf("A", "after-context"),
                "Print  NUM  lines of trailing context after matching lines.  Places a line containing a group separator (--) between contiguous groups of matches.  With the -o or --only-matching option, this\n" +
                        "              has no effect and a warning is given"
            ).withRequiredArg().ofType(Int::class.java)

            parse(optC.toTypedArray()) { options ->
                if (options.has("A")) {
                    N = options.valueOf("A") as Int
                }
                if (options.has("i")) {
                    ignoreCase = true
                }
                if (options.has("w")) {
                    wordRegex = true
                }
            }
        }

        if (wordRegex) {
            regex = ("\b" + regex.pattern + "\b").toRegex()
        }
        val sb = StringBuilder()

        var lines = res.split("\n")
        val linesLen = lines.size

        if (ignoreCase) {
            lines.forEachIndexed { index, it ->
                if (regex.find(it.toLowerCase()) != null) {
                    sb.append(it)
                    sb.append('\n')
                    if (N != null) {
                        for (i in 1..min(linesLen, N!!)) {
                            sb.append(lines[index + i])
                            sb.append('\n')
                        }
                    }
                }
            }
        } else {
            lines.forEachIndexed { index, it ->
                if (regex.find(it) != null) {
                    sb.append(it)
                    sb.append('\n')
                    if (N != null) {
                        for (i in 1..min(linesLen-index-1, N!!)) {
                            sb.append(lines[index + i])
                            sb.append('\n')
                        }
                    }
                }
            }
        }

        return sb.toString()
    }

    /**
     * Register for common arguments for all funciton
     * @return OptionParser
     */
    private fun getOptionParser(
    ): OptionParser = object : OptionParser() {
    }

    /**
     * More suitable parse() method
     */
    private fun OptionParser.parse(
        args: Array<String>,
        description: String? = null,
        acceptNonOptionArguments: Boolean = false,
        block: (OptionSet) -> Unit
    ) {
        formatHelpWith(object : HelpFormatter {
            // here we need to calculate terminal width lazy only if is needed to show help
            // so in terminal-less mode we wont show warnings during normal usage
            val formatter: BuiltinHelpFormatter by lazy {
                BuiltinHelpFormatter(
                    maxOf(
                        80, try {
                            TerminalBuilder.terminal().width
                        } catch (e: Exception) {
                            System.err.println("Warning: Cannot defined terminal width: ${e.message}")
                            0
                        }
                    ),
                    2
                )
            }

            override fun format(options: MutableMap<String, out OptionDescriptor>?) = formatter.format(options)
        })

        acceptsAll(listOf("h", "?", "help"), "Show help").forHelp()

        val options = parse(*args)
        if ("help" in options) {
            if (description != null) {
                System.err.println(description)
                System.err.println("")
                System.err.println("")
            }
            System.err.print("Arguments: ")
            System.err.println(Arrays.toString(args))
            printHelpOn(System.err)
            val suppressExit = System.getProperty(JOPTSIMPLE_SUPPRESS_EXIT)
            if (suppressExit == null || !suppressExit.toBoolean()) {
                System.exit(0)
            }
        }

        if (!acceptNonOptionArguments && options.nonOptionArguments().isNotEmpty()) {
            throw IllegalArgumentException("Unrecognized options: ${options.nonOptionArguments()}")
        }

        block(options)
    }
}

operator fun OptionSet.contains(option: String): Boolean = has(option)

fun main() {
    print(CommandRunner().grep(listOf("-A", "2", "hel+o"), "hello.txt"))
}