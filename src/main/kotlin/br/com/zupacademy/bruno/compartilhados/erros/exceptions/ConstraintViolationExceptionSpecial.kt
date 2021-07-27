package br.com.zupacademy.bruno.compartilhados.erros.exceptions

import br.com.zupacademy.bruno.criarPix.RequestChavePixExterna
import br.com.zupacademy.bruno.deletarPix.RequestDeletarChave
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

class ConstraintViolationExceptionSpecial(
    override val message: String?,
    val erros: Iterable<ConstraintViolation<*>>
) : RuntimeException() {
}
