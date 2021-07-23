package br.com.zupacademy.bruno.criarPix

enum class TipoDeChave {
    ALEATORIO{
        override fun valida(chave: String): Boolean {
            return true
        }
             },
    CPF{
        override fun valida(chave: String): Boolean {
            return chave.matches("^[0-9]{11}\$".toRegex())
        }
       },
    CELULAR{
        override fun valida(chave: String): Boolean {
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
           },
    EMAIL{
        override fun valida(chave: String): Boolean {
            return true
        }
    };

    abstract fun valida(chave: String): Boolean
}