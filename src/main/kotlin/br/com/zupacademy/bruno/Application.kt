package br.com.zupacademy.bruno

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zupacademy.bruno")
		.start()
}

