package br.com.roletodoces.apinfe

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiNfeApplication: CommandLineRunner{
	override fun run(vararg args: String?) {
		Teste().facade()
	}

}

fun main(args: Array<String>) {
	runApplication<ApiNfeApplication>(*args)
}
