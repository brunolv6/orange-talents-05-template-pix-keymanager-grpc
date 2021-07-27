package br.com.zupacademy.bruno.compartilhados.clientesExternos.bcb

data class BcbDeleteRequest(
    val key: String,
    val participant: String = "60701190"
)
