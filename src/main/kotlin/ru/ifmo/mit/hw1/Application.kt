package ru.ifmo.mit.hw1

object Application {
//    var r: Reader = InputStreamReader(System.`in`)

    const val CURR_ROOT: String = "__curr_dir__"

    @JvmStatic
    fun main(args: Array<String>) {
        val pars = Parser()
        pars.env[CURR_ROOT] = System.getProperty("user.dir");

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


