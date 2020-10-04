package lang.c.parse

import lang.FatalErrorException
import lang.TokenType
import lang.c.CParseContext
import lang.c.CParseRule
import lang.c.CToken

class Program(pcx: CParseContext) : CParseRule() {
    // program ::= expression EOF
    private lateinit var program: CParseRule

    @Throws(FatalErrorException::class)
    override fun parse(pcx: CParseContext) {
        // ここにやってくるときは、必ずisFirst()が満たされている
        program = Expression(pcx)
        program.parse(pcx)
        val ct = pcx.tokenizer
        val tk = ct.getCurrentToken(pcx)
        if (tk.type != TokenType.EOF) {
            pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります")
        }
    }

    @Throws(FatalErrorException::class)
    override fun semanticCheck(pcx: CParseContext) {
        program.semanticCheck(pcx)
    }

    @Throws(FatalErrorException::class)
    override fun codeGen(pcx: CParseContext) {
        pcx.ioContext.outStream?.run {
            println(";;; program starts")
            println("\t. = 0x100")
            println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ")
            // ここには将来、宣言に対するコード生成が必要
            println("__START:")
            println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化")
            program.codeGen(pcx)
            println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用")
            println("\tHLT\t\t\t; ProgramNode:")
            println("\t.END\t\t\t; ProgramNode:")
            println(";;; program completes")
        }
    }

    companion object {
        @JvmStatic
        fun isFirst(tk: CToken): Boolean =
                Expression.isFirst(tk)
    }
}