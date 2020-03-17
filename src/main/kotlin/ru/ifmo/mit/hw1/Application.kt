package ru.ifmo.mit.hw1

import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import java.nio.file.Path

object Application {
//    var r: Reader = InputStreamReader(System.`in`)

    @JvmStatic
    fun main(args: Array<String>) {
        val pars = Parser()
        pars.env["__curr_dir__"] = File("").absolutePath.toString()
        val cmndR = CommandRunner()
        while (true) {
            try {
                var line = readLine()
                if (line.isNullOrEmpty()) line = ""
                val pipes = line.let { pars.argGetter(it) }.let { pars.updateEnv(it) }.let { pars.pipeParser(it) }
                print(pipes.let { cmndR.commandParser(it, pars.env) })
            } catch (e: ExitCommand) {
                return
            }
        }
    }
}


