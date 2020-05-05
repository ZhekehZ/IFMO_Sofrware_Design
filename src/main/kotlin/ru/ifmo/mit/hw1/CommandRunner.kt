package ru.ifmo.mit.hw1

import joptsimple.*
import org.jetbrains.annotations.NotNull
import org.jline.terminal.TerminalBuilder
import ru.ifmo.mit.hw1.Application.CURR_ROOT
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

const val JOPTSIMPLE_SUPPRESS_EXIT = "joptsimple.suppressExit"


/**
 * This class allows to perform command execution, which introduced by functions
 */
class CommandRunner {

    /**
     * @param pipes which you can get from Parser.
     */
    fun commandParser(pipes: MutableList<String>, env: Environment): String {
        var res: String? = null
        pipes.forEachIndexed { idx, it ->
            res = execCommand(it.split(" ")[0], it.split(" ").drop(1), res, env)
        }
        return res!!
    }

    /**
     * Call needed function
     */
    private fun execCommand(command: String, opt: List<String>, arg: String?, env: Environment): String {
        when (command) {
            "cat" -> return cat(opt, arg, env)
            "wc" -> return wc(opt, arg, env)
            "echo" -> return echo(opt, arg, env)
            "pwd" -> return pwd(opt, arg, env)
            "exit" -> return exit(opt, arg, env)
            "ls" -> return ls(opt, arg, env)
            "cd" -> return cd(opt, arg, env)
        }
        return ""
    }

    private fun getRealPath(path: String, env: Environment): Path {
        return getCurrRootPath(env).resolve(path).toAbsolutePath().normalize()
    }

    private fun getCurrRootPath(env: Environment): Path {
        return Paths.get(env[CURR_ROOT].orEmpty())
    }

    private fun changeCurrRoot(root: Path, env: Environment) {
        env[CURR_ROOT] = root.toString()
    }

    private fun cat(opt: List<String>, arg: String?, env: Environment): String {
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
            File(getRealPath(file, env).toRealPath().toString()).forEachLine {
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

    private fun wc(opt: List<String>, arg: String?, env: Environment): String {
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

    private fun echo(opt: List<String>, arg: String?, env: Environment): String {
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


    /**
     * Prints list of files in directory or file name
     *
     * @param arg -- arguments (must be exactly one)
     * @param env -- current (local) environment
     *
     * @return list of files in directory -- when the argument is a directory
     *         file name -- when the argument is a file
     *         "Invalid arguments" -- when the number of arguments is not equal to 1
     *         "Invalid path <path>" -- when the argument is invalid
     */
    private fun ls(@NotNull opt: List<String>, arg: String?, @NotNull env: Environment): String {
        val root = getCurrRootPath(env)

        val dir = when (opt.size) {
            0 -> root
            1 -> getRealPath(opt[0], env)
            else -> return "Invalid arguments"
        }

        if (!Files.exists(dir)) return "Invalid path ($dir)"

        return if (Files.isDirectory(dir)) {
            Files.list(dir)
                .map(root::relativize)
                .map { e -> e.toString() }
                .reduce { a, b -> "$a\n$b" }
                .orElse("") + "\n"
        } else {
            dir.toString() + "\n"
        }
    }

    /**
     * Changes current working directory (see [CURR_ROOT] in [Environment] variable)
     *  Does nothing when called without arguments
     *
     * @param arg -- call arguments (no more than one)
     * @param env -- current (local) environment
     *
     * @return "" -- when the directory has successfully changed
     *         "Invalid directory" -- when the directory path is wrong
     *         "Invalid arguments" -- when more than one argument is passed
     */
    private fun cd(@NotNull opt: List<String>, arg: String?, @NotNull env: Environment): String {
        return when (opt.size) {
            0 -> ""
            1 -> {
                val path = getRealPath(opt[0], env)
                if (Files.isDirectory(path)) {
                    changeCurrRoot(path, env)
                    return ""
                }
                return "Invalid directory"
            }
            else -> "Invalid arguments"
        }
    }

    private fun pwd(opt: List<String>, arg: String?, env: Environment): String {
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
            return getCurrRootPath(env).toString()
        }
        return getCurrRootPath(env).toRealPath().toString()
    }

    private fun exit(opt: List<String>, arg: String?, env: Environment): String {
        throw ExitCommand()
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