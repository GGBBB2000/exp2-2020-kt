package lang

interface Compiler<Pctx> {
    @Throws(FatalErrorException::class)
    fun semanticCheck(pcx: Pctx)

    @Throws(FatalErrorException::class)
    fun codeGen(pcx: Pctx)
}