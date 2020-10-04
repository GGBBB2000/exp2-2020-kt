package lang.c.parse

import lang.FatalErrorException
import lang.c.CParseContext
import lang.c.CParseRule
import lang.c.CToken

class Factor(pcx: CParseContext?) : CParseRule() {
    // factor ::= number
    private lateinit var number: CParseRule

    @Throws(FatalErrorException::class)
    override fun parse(pcx: CParseContext) {
        // ここにやってくるときは、必ずisFirst()が満たされている
        number = Number(pcx).apply {
            parse(pcx)
        }
    }

    @Throws(FatalErrorException::class)
    override fun semanticCheck(pcx: CParseContext) {
        number.apply {
            semanticCheck(pcx)
            this@Factor.cType = cType // number の型をそのままコピー
            this@Factor.isConstant = isConstant // number は常に定数
        }
    }

    @Throws(FatalErrorException::class)
    override fun codeGen(pcx: CParseContext) {
        pcx.ioContext.outStream?.run {
            println(";;; factor starts")
            number.codeGen(pcx)
            println(";;; factor completes")
        }
    }

    companion object {
        fun isFirst(tk: CToken): Boolean {
            return Number.isFirst(tk)
        }
    }
}