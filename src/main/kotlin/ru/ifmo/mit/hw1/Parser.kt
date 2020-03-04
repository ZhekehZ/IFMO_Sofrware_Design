package ru.ifmo.mit.hw1

import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Class to perform simple parse:
 * 1. Put varuables in $var_name
 * 2. Updates environment if we introduce new variables
 * 3. Separate pipeline by '|'
 *
 * @author Elena Kartysheva
 */

class Parser {

    val env = Environment()

    /**
     * Place argValue instead of "$a"
     */
    fun argGetter(s: String): String {
        var isUnaryQuote = false
        var parseAsVar = false
        var isEcrane = false
        var strB = StringBuilder()
        val valToSubstitute = mutableListOf<String>()
        val positions = mutableListOf<Int>()
        s.forEachIndexed { idx, it ->
            if (it == '\'' && !isEcrane) {
                isUnaryQuote = !isUnaryQuote
            }
            if (!isUnaryQuote) {
                if (it == '$' && !isEcrane) {
                    parseAsVar = true
                    positions.add(idx)
                }

                if (parseAsVar) {
                    if (it == ' ' || it == '}' || it == '"') {
                        parseAsVar = false
                        valToSubstitute.add(strB.toString().substring(1))
                        strB = StringBuilder()
                    } else {
                        strB.append(it)
                    }
                }
            }

            isEcrane = it == '\\'

        }

        if (parseAsVar && strB.length > 0) {
            valToSubstitute.add(strB.toString().substring(1))
        }

        var parsedStr = s
        var argVal = ""
        valToSubstitute.forEachIndexed { ind, name ->
            argVal = env.getValue(name)
            val sLeft = parsedStr.substring(0, positions[ind])
            val sRight = parsedStr.substring(positions[ind] + name.length + 1)
            parsedStr = sLeft + argVal + sRight
        }

        return parsedStr
    }

    /**
     * Parse String and find new parameters
     */
    fun updateEnv(s: String): String {
        var isBrace = false
        var parseVal = false
        var sb = StringBuilder()
        var argName = ""

        s.forEachIndexed { pos, it ->
            if (it == '{') {
                isBrace = true
            } else if (it == '}') {
                isBrace = false
            } else if (it == '=' && sb.length > 0) {
                argName = sb.toString()
                sb = java.lang.StringBuilder()
                parseVal = true
            } else if (it == ' ') {
                if (parseVal) {
                    if (env.containsKey(argName)) {
                        env[argName] = sb.toString()
                    } else {
                        env.put(argName, sb.toString())
                    }
                    parseVal = false
                }
                sb = java.lang.StringBuilder()
            } else {
                sb.append(it)
            }
        }

        if (parseVal && sb.length > 0) {
            if (env.containsKey(argName)) {
                env[argName] = sb.toString()
            } else {
                env.put(argName, sb.toString())
            }
        }
        return s
    }

    /**
     * Parse string to array of strings by pipe symbol "|"
     */
    fun pipeParser(s: String): MutableList<String> {
        val pipes = mutableListOf<Int>()
        var isQuote = false
        var isDoubleQuote = false
        var isEcrane = false
        s.forEachIndexed { pos, it ->
            if (it == '"') {
                isDoubleQuote = !isDoubleQuote
            } else if (it == '\'') {
                isQuote = !isQuote
            } else if (it == '\\' && !isDoubleQuote && !isQuote) {
                isEcrane = true
            } else if (it == '|' && !isDoubleQuote && !isEcrane && !isQuote) {
                pipes.add(pos)
            }
        }
        pipes.add(s.length)
        val subCommands = mutableListOf<String>()
        var lowerBound = 0
        pipes.forEach {
            subCommands.add(s.subSequence(lowerBound, it).toString().trim())
            lowerBound = it + 1
        }
        return subCommands
    }
}