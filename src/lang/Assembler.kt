package lang

interface Assembler<Pctx> {
    @Throws(FatalErrorException::class)
    fun pass1(pcx: Pctx)

    @Throws(FatalErrorException::class)
    fun pass2(pcx: Pctx)
}