package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb


import br.com.zupacademy.bruno.compartilhados.erros.AlreadyExistsErrorException
import br.com.zupacademy.bruno.compartilhados.erros.BadRequestErrorException
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandlerBcbClient::class)
class ErrorHandlerBcbClientInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        try {
            return context.proceed()
        } catch (ex: HttpClientResponseException) {

            when (context.methodName) {

                "criarChavePix" -> {
                    when (ex.status) {
                         HttpStatus.UNPROCESSABLE_ENTITY -> throw AlreadyExistsErrorException("Chave Pix já existe no BCB")
                        else -> throw BadRequestErrorException("Erro com ${context.name} do tipo ${ex.status}")
                    }
                }

                else -> throw BadRequestErrorException("FOi daqui")

            }
        }
    }
}