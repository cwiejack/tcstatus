package de.swp

import de.swp.model.TCServerData
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.net.URL

@SpringBootApplication
open class DemoApplication {

    @Bean
    open fun commandLineRunner(tcServerData: TCServerData): CommandLineRunner {
        return object : CommandLineRunner {
            override fun run(args: Array<String>) {
                val options = getOptionParams()
                val parser = DefaultParser()
                val cmd = parser.parse(options,args)

                if (cmd.hasOption("tcserver")) {
                    tcServerData.serverUrl = URL(cmd.getOptionValue("tcserver"))
                }
                if (cmd.hasOption("tcuser")) {
                    tcServerData.userName = cmd.getOptionValue("tcuser")
                }
                if (cmd.hasOption("tcpwd")) {
                    tcServerData.password = cmd.getOptionValue("tcpwd")
                }
            }

        }
    }

    private fun getOptionParams() : Options {
        val options = Options()
        options.addOption(Option.builder("tcserver").argName("tcserver").hasArg().desc("Url des Teamcity Servers").build())
        options.addOption(Option.builder("tcuser").argName("tcuser").hasArg().desc("Benutzername für die Anmeldung am Server").build())
        options.addOption(Option.builder("tcpwd").argName("tcpwd").hasArg().desc("Passwort für die Anmeldung am Server").build())
        return options
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}


