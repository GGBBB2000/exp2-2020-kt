package lang.c.parse

import lang.FatalErrorException
import lang.c.CParseContext
import lang.c.CParseRule
import lang.c.CToken
import lang.c.CType
import lang.c.CType.Companion.getCType

class Number(pcx: CParseContext?) : CParseRule() {
    // number ::= NUM
    private lateinit var num: CToken

    @Throws(FatalErrorException::class)
    override fun parse(pcx: CParseContext) {
        val ct = pcx.tokenizer
        val tk = ct.getCurrentToken(pcx)
        num = tk
        ct.getNextToken(pcx)
    }

    @Throws(FatalErrorException::class)
    override fun semanticCheck(pcx: CParseContext) {
        cType = getCType(CType.T_int)
        isConstant = true
    }

    @Throws(FatalErrorException::class)
    override fun codeGen(pcx: CParseContext) {
        val o = pcx.ioContext.outStream
        o!!.println(";;; number starts")
        o.println("\tMOV\t#" + num.text + ", (R6)+\t; Number: 数を積む<" + num.toExplainString() + ">")
        o.println(";;; number completes")
    }

    companion object {
        fun isFirst(tk: CToken): Boolean {
            return tk.type == CToken.TK_NUM
        }
    }
}