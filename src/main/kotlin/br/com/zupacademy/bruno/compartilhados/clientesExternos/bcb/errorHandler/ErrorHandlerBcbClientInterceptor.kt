//package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb.errorHandler
//
//
//import br.com.zupacademy.bruno.compartilhados.erros.exceptions.AlreadyExistsErrorException
//import br.com.zupacademy.bruno.compartilhados.erros.exceptions.BadRequestErrorException
//import io.micronaut.aop.InterceptorBean
//import io.micronaut.aop.MethodInterceptor
//import io.micronaut.aop.MethodInvocationContext
//import io.micronaut.http.HttpStatus
//import io.micronaut.http.client.exceptions.HttpClientResponseException
//import org.slf4j.LoggerFactory
//import javax.inject.Singleton
//
//@Singleton
//@InterceptorBean(ErrorHandlerBcbClient::class)
//class ErrorHandlerBcbClientInterceptor : MethodInterceptor<Any, Any> {
//
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
//        try {
//            return context.proceed()
//        } catch (ex: HttpClientResponseException) {
//            logger.error("Algum erro ocorreu aqui - ${context.name} do tipo ${ex.status}")
//            when (context.methodName) {
//
//                "criarChavePix" -> {
//                    when (ex.status) {
//                         HttpStatus.UNPROCESSABLE_ENTITY -> throw AlreadyExistsErrorException("Chave Pix já existe no BCB")
//                        else -> throw BadRequestErrorException("Erro com ${context.name} do tipo ${ex.status}")
//                    }
//                }
//
//                else -> throw BadRequestErrorException("Erro inesperado no BCB")
//
//            }
//        }
//    }
//}