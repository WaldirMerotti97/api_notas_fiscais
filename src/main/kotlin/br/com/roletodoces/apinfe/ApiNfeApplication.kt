package br.com.roletodoces.apinfe

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class ApiNfeApplication: CommandLineRunner{
	override fun run(vararg args: String?) {
		Teste().facade()
	}

}

fun main(args: Array<String>) {
	runApplication<ApiNfeApplication>(*args)
}
