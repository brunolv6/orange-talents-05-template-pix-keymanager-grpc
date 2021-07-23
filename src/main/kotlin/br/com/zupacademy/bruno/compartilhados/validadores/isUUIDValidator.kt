package br.com.zupacademy.bruno.compartilhados.validadores

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton

@Singleton
class isUUIDValidator: ConstraintValidator<isUUID, String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<isUUID>,
        context: ConstraintValidatorContext
    ): Boolean {

        if(value == null) return true

        // confere se o valor corresponde ao Regex esperado
        return value.matches("[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}".toRegex())
    }

}
