package lang.c

import lang.FatalErrorException
import lang.IOContext
import lang.TokenType

object TestCToken {
    @JvmStatic
    fun main(args: Array<String>) {
        //val inFile = args[0] // 適切なファイルを絶対パスで与えること
        val inFile = "/home/cs17075/classes/2020情報科学実験II/TestCases（10月1日版）/実験01/CodeTest.c"
        val ioCtx = IOContext(inFile, System.out, System.err)
        val tokenizer = CTokenizer(CTokenRule())
        val pcx = CParseContext(ioCtx, tokenizer)
        try {
            val ct = pcx.tokenizer
            val tk = ct.getNextToken(pcx)
            if (TestTokenizer.isFirst(tk)) {
                TestTokenizer(pcx).apply {
                    parse(pcx)
                    codeGen(pcx)
                }
            }
        } catch (e: FatalErrorException) {
            e.printStackTrace()
        }
    }

    private class TestTokenizer  //		program  ::= { token } EOF
    (pcx: CParseContext) : CParseRule() {
        override fun parse(ctx: CParseContext) {
            var tk = ctx.tokenizer.getCurrentToken(ctx)
            while (tk.type != TokenType.EOF) {
                if (tk.type == TokenType.NUM) {
                    ctx.ioContext.outStream?.println("Token=" + tk.toExplainString() + "type=" + tk.type + " valule=" + tk.intValue)
                } else {
                    ctx.ioContext.outStream?.println("Token=" + tk.toExplainString() + "type=" + tk.type)
                }
                tk = ctx.tokenizer.getNextToken(ctx)
            }
        }

        @Throws(FatalErrorException::class)
        override fun semanticCheck(pcx: CParseContext) {
            // do nothing
        }

        @Throws(FatalErrorException::class)
        override fun codeGen(pcx: CParseContext) {
            // do nothing
        }

        companion object {
            fun isFirst(tk: CToken): Boolean = true
        }
    }
}