package br.com.zupacademy.bruno.criarPix.enums

import java.util.*


enum class TipoDeChave {
    ALEATORIO{
        override fun valida(chave: String): Boolean {
            return chave.isNullOrBlank()
        }

        override fun toModeltoBcb(): String {
            return "RANDOM"
        }
        override fun seAleatorio(chave: String): String {
            return UUID.randomUUID().toString()
        }
    },
    CPF{
        override fun valida(chave: String): Boolean {
            return chave.matches("^[0-9]{11}\$".toRegex())
        }
        override fun toModeltoBcb(): String {
            return "CPF"
        }
        override fun seAleatorio(chave: String): String {
            return chave
        }
    },
    CELULAR{
        override fun valida(chave: String): Boolean {
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
        override fun toModeltoBcb(): String {
            return "PHONE"
        }
        override fun seAleatorio(chave: String): String {
            return chave
        }
    },
    EMAIL{
        override fun valida(chave: String): Boolean {
            return chave.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex())
        }

        override fun toModeltoBcb(): String {
            return "EMAIL"
        }

        override fun seAleatorio(chave: String): String {
            return chave
        }

    };

    abstract fun valida(chave: String): Boolean

    abstract fun seAleatorio(chave: String): String

    abstract fun toModeltoBcb(): String

//    fun criar(tipo: String): TipoDeChave {
//        val tipoDeChave: TipoDeChave = this.valueOf(tipo)
//
//        if(!tipoDeChave.valida(tipo)) throw InconsistenciaTipoChaveEChaveException("Apresentou erros de validacao")
//
//        return tipoDeChave
//    }
}