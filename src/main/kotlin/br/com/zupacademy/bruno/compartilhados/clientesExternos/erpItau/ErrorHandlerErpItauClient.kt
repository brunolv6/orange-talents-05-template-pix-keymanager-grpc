package br.com.zupacademy.bruno.compartilhados.clientesExternos.erpItau

import io.micronaut.aop.Around

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Around
annotation class ErrorHandlerErpItauClient
