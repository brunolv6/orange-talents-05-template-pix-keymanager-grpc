//package br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau.errorHandler
//
//
//import br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau.errorHandler.ErrorHandlerErpItauClient
//import br.com.zupacademy.bruno.compartilhados.erros.exceptions.AlreadyExistsErrorException
//import br.com.zupacademy.bruno.compartilhados.erros.exceptions.BadRequestErrorException
//import io.micronaut.aop.InterceptorBean
//import io.micronaut.aop.MethodInterceptor
//import io.micronaut.aop.MethodInvocationContext
//import io.micronaut.http.HttpStatus
//import io.micronaut.http.client.exceptions.HttpClientResponseException
//import javax.inject.Singleton
//
//@Singleton
//@InterceptorBean(ErrorHandlerErpItauClient::class)
//class ErrorHandlerErpItauClientInterceptor : MethodInterceptor<Any, Any> {
//    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
//        try {
//            return context.proceed()
//        } catch (ex: HttpClientResponseException) {
//
//            when (context.methodName) {
//
//                "consultarConta" -> {
//                    when (ex.status) {
//                         HttpStatus.UNPROCESSABLE_ENTITY -> throw BadRequestErrorException("Conta não encontrada!")
//                        else -> throw BadRequestErrorException("Erro com servidor externo")
//                    }
//                }
//
//                else -> throw BadRequestErrorException("Erro com servidor externo")
//
//            }
//        }
//    }
//}