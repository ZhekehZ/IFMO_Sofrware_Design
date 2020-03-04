package ru.ifmo.mit.hw1

import java.io.InputStreamReader
import java.io.Reader

object Application {
//    var r: Reader = InputStreamReader(System.`in`)

    @JvmStatic
    fun main() {
        val pars = Parser()
        val cmndR = CommandRunner()
        while (true) {
            try {
                var line = readLine()
                if (line.isNullOrEmpty()) line = ""
                val pipes = line.let { pars.argGetter(it) }.let { pars.updateEnv(it) }.let { pars.pipeParser(it) }
                print(pipes.let { cmndR.commandParser(it) })
            } catch (e: ExitCommand) {
                return
            }
        }
    }
}

