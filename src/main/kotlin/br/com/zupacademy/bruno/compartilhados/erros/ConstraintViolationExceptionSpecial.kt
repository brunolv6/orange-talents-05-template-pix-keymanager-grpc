package br.com.zupacademy.bruno.compartilhados.erros

import br.com.zupacademy.bruno.ErrorDetails
import br.com.zupacademy.bruno.criarPix.RequestChave
import javax.validation.ConstraintViolation

class ConstraintViolationExceptionSpecial(
    override val message: String?,
    val erros: MutableSet<ConstraintViolation<RequestChave>>
) : RuntimeException() {
}
