package lang

abstract class ParseRule<ParseContext> {
    @Throws(FatalErrorException::class)
    abstract fun parse(pcx: ParseContext)

    @Throws(FatalErrorException::class)
    abstract fun semanticCheck(pcx: ParseContext)

    @Throws(FatalErrorException::class)
    abstract fun codeGen(pcx: ParseContext)
}