package br.com.zupacademy.bruno.criarPix.enums

enum class TipoDeConta {
    CONTA_CORRENTE{
        override fun toModeltoBcb(): String {
            return "CACC"
        }
    },
    CONTA_POUPANCA{
        override fun toModeltoBcb(): String {
            return "SVGS"
        }
    };
    abstract fun toModeltoBcb(): String
}