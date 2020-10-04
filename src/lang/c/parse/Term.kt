package lang.c.parse

import lang.FatalErrorException
import lang.c.CParseContext
import lang.c.CParseRule
import lang.c.CToken

class Term(pcx: CParseContext?) : CParseRule() {
    // term ::= factor
    private lateinit var factor: CParseRule

    @Throws(FatalErrorException::class)
    override fun parse(pcx: CParseContext) {
        // ここにやってくるときは、必ずisFirst()が満たされている
        factor = Factor(pcx).apply {
            parse(pcx)
        }
    }

    @Throws(FatalErrorException::class)
    override fun semanticCheck(pcx: CParseContext) {
        factor.apply {
            semanticCheck(pcx)
            this@Term.cType = cType // factor の型をそのままコピー
            this@Term.isConstant = isConstant
        }
    }

    @Throws(FatalErrorException::class)
    override fun codeGen(pcx: CParseContext) {
        pcx.ioContext.outStream?.run {
            println(";;; term starts")
            factor.codeGen(pcx)
            println(";;; term completes")
        }
    }

    companion object {
        fun isFirst(tk: CToken): Boolean = Factor.isFirst(tk)
    }
}