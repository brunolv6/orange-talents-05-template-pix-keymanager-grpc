package br.com.zupacademy.bruno.compartilhados.erros.exceptions

import br.com.zupacademy.bruno.criarPix.RequestChavePixExterna
import javax.validation.ConstraintViolation

class ConstraintViolationExceptionSpecial(
    override val message: String?,
    val erros: MutableSet<ConstraintViolation<RequestChavePixExterna>>
) : RuntimeException() {
}
