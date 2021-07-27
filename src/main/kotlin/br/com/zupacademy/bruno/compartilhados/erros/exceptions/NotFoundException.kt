package br.com.zupacademy.bruno.compartilhados.erros.exceptions

import java.lang.RuntimeException

class NotFoundException(override val message: String?) : RuntimeException() {
}