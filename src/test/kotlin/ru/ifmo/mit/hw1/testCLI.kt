package ru.ifmo.mit.hw1

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.TextFromStandardInputStream
import java.time.Clock.system
import org.junit.contrib.java.lang.system.TextFromStandardInputStream.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class testCLI {
    @Rule
    @JvmField
    var systemInMock: TextFromStandardInputStream = emptyStandardInputStream()

    @Test
    fun BAT() {
        val app = Application
        val outputStream = ByteArrayOutputStream()
        val stream = PrintStream(outputStream)
        System.setOut(stream)
        systemInMock.provideLines("a=10", "echo \$a", "echo 123 | wc ", "exit")
        app.main()
        val res = """
        
        10
        1 1 3
        
        """.trimIndent()
        assertEquals(res, outputStream.toString())
    }

    @Test
    fun TestCat() {
        val app = Application
        val outputStream = ByteArrayOutputStream()
        val stream = PrintStream(outputStream)
        System.setOut(stream)
        systemInMock.provideLines(
            "FILE=build.gradle",
            "cat -n \$FILE",
            "cat -b \$FILE",
            "cat -b \$FILE gradlew.bat",
            "exit"
        )
        app.main()
        val res = """
        
        0 plugins {
        1     id 'java'
        2     id 'org.jetbrains.kotlin.jvm' version '1.3.61'
          3 }
           4 
        5 version '1.0-SNAPSHOT'
           6 
        7 sourceCompatibility = 1.8
           8 
        9 repositories {
        10     mavenCentral()
         11 }
          12 
        13 dependencies {
        14     compile 'org.jline:jline-terminal:3.10.0'
        15     compile 'net.sf.jopt-simple:jopt-simple:5.0.4'
        16     implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        17     testCompile group: 'junit', name: 'junit', version: '4.12'
        18     testCompile 'com.github.stefanbirkner:system-rules:1.19.0'
         19 }
          20 
        21 compileKotlin {
        22     kotlinOptions.jvmTarget = "1.8"
         23 }
        24 compileTestKotlin {
        25     kotlinOptions.jvmTarget = "1.8"
         26 }
            


        0 plugins {
        1     id 'java'
        2     id 'org.jetbrains.kotlin.jvm' version '1.3.61'
          3 }
             
        4 version '1.0-SNAPSHOT'
             
        5 sourceCompatibility = 1.8
             
        6 repositories {
        7     mavenCentral()
          8 }
             
        9 dependencies {
        10     compile 'org.jline:jline-terminal:3.10.0'
        11     compile 'net.sf.jopt-simple:jopt-simple:5.0.4'
        12     implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        13     testCompile group: 'junit', name: 'junit', version: '4.12'
        14     testCompile 'com.github.stefanbirkner:system-rules:1.19.0'
         15 }
             
        16 compileKotlin {
        17     kotlinOptions.jvmTarget = "1.8"
         18 }
        19 compileTestKotlin {
        20     kotlinOptions.jvmTarget = "1.8"
         21 }
            


        0 plugins {
        1     id 'java'
        2     id 'org.jetbrains.kotlin.jvm' version '1.3.61'
          3 }
             
        4 version '1.0-SNAPSHOT'
             
        5 sourceCompatibility = 1.8
             
        6 repositories {
        7     mavenCentral()
          8 }
             
        9 dependencies {
        10     compile 'org.jline:jline-terminal:3.10.0'
        11     compile 'net.sf.jopt-simple:jopt-simple:5.0.4'
        12     implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        13     testCompile group: 'junit', name: 'junit', version: '4.12'
        14     testCompile 'com.github.stefanbirkner:system-rules:1.19.0'
         15 }
             
        16 compileKotlin {
        17     kotlinOptions.jvmTarget = "1.8"
         18 }
        19 compileTestKotlin {
        20     kotlinOptions.jvmTarget = "1.8"
         21 }
            

        22 @if "%DEBUG%" == "" @echo off
        23 @rem ##########################################################################
        24 @rem
        25 @rem  Gradle startup script for Windows
        26 @rem
        27 @rem ##########################################################################
             
        28 @rem Set local scope for the variables with windows NT shell
        29 if "%OS%"=="Windows_NT" setlocal
             
        30 set DIRNAME=%~dp0
        31 if "%DIRNAME%" == "" set DIRNAME=.
        32 set APP_BASE_NAME=%~n0
        33 set APP_HOME=%DIRNAME%
             
        34 @rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
        35 set DEFAULT_JVM_OPTS="-Xmx64m"
             
        36 @rem Find java.exe
        37 if defined JAVA_HOME goto findJavaFromJavaHome
             
        38 set JAVA_EXE=java.exe
        39 %JAVA_EXE% -version >NUL 2>&1
        40 if "%ERRORLEVEL%" == "0" goto init
             
        41 echo.
        42 echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
        43 echo.
        44 echo Please set the JAVA_HOME variable in your environment to match the
        45 echo location of your Java installation.
             
        46 goto fail
             
        47 :findJavaFromJavaHome
        48 set JAVA_HOME=%JAVA_HOME:"=%
        49 set JAVA_EXE=%JAVA_HOME%/bin/java.exe
             
        50 if exist "%JAVA_EXE%" goto init
             
        51 echo.
        52 echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
        53 echo.
        54 echo Please set the JAVA_HOME variable in your environment to match the
        55 echo location of your Java installation.
             
        56 goto fail
             
        57 :init
        58 @rem Get command-line arguments, handling Windows variants
             
        59 if not "%OS%" == "Windows_NT" goto win9xME_args
             
        60 :win9xME_args
        61 @rem Slurp the command line arguments.
        62 set CMD_LINE_ARGS=
        63 set _SKIP=2
             
        64 :win9xME_args_slurp
        65 if "x%~1" == "x" goto execute
             
        66 set CMD_LINE_ARGS=%*
             
        67 :execute
        68 @rem Setup the command line
             
        69 set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
             
        70 @rem Execute Gradle
        71 "%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%
             
        72 :end
        73 @rem End local scope for the variables with windows NT shell
        74 if "%ERRORLEVEL%"=="0" goto mainEnd
             
        75 :fail
        76 rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
        77 rem the _cmd.exe /c_ return code!
        78 if  not "" == "%GRADLE_EXIT_CONSOLE%" exit 1
        79 exit /b 1
             
        80 :mainEnd
        81 if "%OS%"=="Windows_NT" endlocal
             
        82 :omega
            



        """.trimIndent()
        assertEquals(res, outputStream.toString())

    }

    @Test
    fun TestPwd() {
        val app = Application
        val outputStream = ByteArrayOutputStream()
        val stream = PrintStream(outputStream)
        System.setOut(stream)
        systemInMock.provideLines("pwd -L ", "pwd --physical ", "exit")
        app.main()
        val res = """
            /home/karl-crl/Desktop/IFMO/Software_des
            /home/karl-crl/Desktop/IFMO/Software_des
            
        """.trimIndent()
        assertEquals(res, outputStream.toString())

    }

    @Test
    fun TestEcho() {
        val app = Application
        val outputStream = ByteArrayOutputStream()
        val stream = PrintStream(outputStream)
        System.setOut(stream)
        systemInMock.provideLines("a=10", "echo \$a", "x=exit", "\$x")
        app.main()
        val res = """
            
            10
            
            
        """.trimIndent()
        assertEquals(res, outputStream.toString())
    }

    @Test
    fun TestWC() {
        val app = Application
        val outputStream = ByteArrayOutputStream()
        val stream = PrintStream(outputStream)
        System.setOut(stream)
        systemInMock.provideLines("echo 123 | wc ", "echo {12 3        4} | wc ", "exit")
        app.main()
        val res = """
        1 1 3
        1 3 15
        
        """.trimIndent()
        assertEquals(res, outputStream.toString())
    }

    @Test
    fun TestCommandRunner() {
        val cmdr = CommandRunner()
        val res = cmdr.commandParser(mutableListOf<String>("echo build.gradle", "cat -b"))
        val expected = """
            0 plugins {
            1     id 'java'
            2     id 'org.jetbrains.kotlin.jvm' version '1.3.61'
              3 }
                 
            4 version '1.0-SNAPSHOT'
                 
            5 sourceCompatibility = 1.8
                 
            6 repositories {
            7     mavenCentral()
              8 }
                 
            9 dependencies {
            10     compile 'org.jline:jline-terminal:3.10.0'
            11     compile 'net.sf.jopt-simple:jopt-simple:5.0.4'
            12     implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
            13     testCompile group: 'junit', name: 'junit', version: '4.12'
            14     testCompile 'com.github.stefanbirkner:system-rules:1.19.0'
             15 }
                 
            16 compileKotlin {
            17     kotlinOptions.jvmTarget = "1.8"
             18 }
            19 compileTestKotlin {
            20     kotlinOptions.jvmTarget = "1.8"
             21 }
                


        """.trimIndent()
        assertEquals(expected, res)
    }

    @Test
    fun TestParser() {
        val parcer = Parser()
        parcer.updateEnv("a=build.gradle")
        assertEquals("build.gradle", parcer.env["a"])
        val result = "echo \$a | cat -b "
            .let { parcer.argGetter(it) }
            .let { parcer.pipeParser(it) }

        val expected = mutableListOf<String>("echo build.gradle", "cat -b")
        assertEquals(expected[0], result[0])
        assertEquals(expected[1], result[1])
    }

    @Test
    fun TestGrep() {
        var expected = """
            hello
            Hello, 
            hellllo, 
            Hello, 
            hellllo, 
              omg
            hellllo, 
              omg
                

            hello
            Hello, 
            hellllo, 
            hellllo, 
              omg
                

            hello
            hellllo, 


            """.trimIndent()
        val app = Application
        val outputStream = ByteArrayOutputStream()
        val stream = PrintStream(outputStream)
        System.setOut(stream)

        systemInMock.provideLines("grep -i -A 2 hel+o hello.txt", "grep -A 2 hel+o hello.txt", "cat hello.txt | grep hel+o", "exit")
        app.main()
        assertEquals(expected, outputStream.toString())

    }
}