package br.com.zupacademy.bruno.compartilhados.erros.exceptions

import java.lang.RuntimeException

class NotAuthorizedException(override val message: String?) : RuntimeException() {
}