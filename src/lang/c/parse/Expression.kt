package lang.c.parse

import lang.FatalErrorException
import lang.TokenType
import lang.c.CParseContext
import lang.c.CParseRule
import lang.c.CToken
import lang.c.CType
import lang.c.CType.Companion.getCType

class Expression(pcx: CParseContext) : CParseRule() {
    // expression ::= term { expressionAdd }
    private lateinit var expression: CParseRule

    @Throws(FatalErrorException::class)
    override fun parse(pcx: CParseContext) {
        // ここにやってくるときは、必ずisFirst()が満たされている
        lateinit var list: CParseRule
        var term: CParseRule = Term(pcx)
        term.parse(pcx)
        val ct = pcx.tokenizer
        var tk = ct.getCurrentToken(pcx)

        while (ExpressionAdd.isFirst(tk) or ExpressionSub.isFirst(tk)) {
            list = when (tk.type) {
                CToken.TK_PLUS -> ExpressionAdd(pcx, term)
                CToken.TK_MINUS -> ExpressionSub(pcx, term)
                else -> {
                    TODO("CTokenをenumにする")
                }
            }.apply { parse(pcx) }
            term = list
            tk = ct.getCurrentToken(pcx)
        }
        expression = term
    }

    @Throws(FatalErrorException::class)
    override fun semanticCheck(pcx: CParseContext) {
        expression.apply {
            semanticCheck(pcx)
            this@Expression.cType = cType // expression の型をそのままコピー
            this@Expression.isConstant = isConstant
        }
    }

    @Throws(FatalErrorException::class)
    override fun codeGen(pcx: CParseContext) {
        pcx.ioContext.outStream?.run {
            println(";;; expression starts")
            expression.codeGen(pcx)
            println(";;; expression completes")
        }
    }

    companion object {
        fun isFirst(tk: CToken): Boolean {
            return Term.isFirst(tk)
        }
    }
}

internal class ExpressionAdd(pcx: CParseContext, private val left: CParseRule) : CParseRule() {
    // expressionAdd ::= '+' term
    private lateinit var op: CToken
    private lateinit var right: CParseRule

    @Throws(FatalErrorException::class)
    override fun parse(pcx: CParseContext) {
        // ここにやってくるときは、必ずisFirst()が満たされている
        val ct = pcx.tokenizer
        op = ct.getCurrentToken(pcx)
        // +の次の字句を読む
        val tk = ct.getNextToken(pcx)
        if (Term.isFirst(tk)) {
            right = Term(pcx).apply {
                parse(pcx)
            }
        } else {
            pcx.fatalError(tk.toExplainString() + "+の後ろはtermです")
        }
    }

    @Throws(FatalErrorException::class)
    override fun semanticCheck(pcx: CParseContext) {
        // 足し算の型計算規則
        val s = arrayOf(intArrayOf(CType.T_err, CType.T_err), intArrayOf(CType.T_err, CType.T_int))
        left.semanticCheck(pcx)
        right.semanticCheck(pcx)
        val lt = left.cType.type // +の左辺の型
        val rt = right.cType.type // +の右辺の型
        val nt = s[lt][rt] // 規則による型計算
        if (nt == CType.T_err) {
            pcx.fatalError(op.toExplainString() + "左辺の型[" + left.cType.toString() + "]と右辺の型[" + right!!.cType.toString() + "]は足せません")
        }
        cType = getCType(nt)
        isConstant = left.isConstant && right.isConstant // +の左右両方が定数のときだけ定数
    }

    @Throws(FatalErrorException::class)
    override fun codeGen(pcx: CParseContext) {
        pcx.ioContext.outStream?.run {
            left.codeGen(pcx) // 左部分木のコード生成を頼む
            right.codeGen(pcx) // 右部分木のコード生成を頼む
            println("\tMOV\t-(R6), R0\t; ExpressionAdd: ２数を取り出して、足し、積む<$op>")
            println("\tMOV\t-(R6), R1\t; ExpressionAdd:")
            println("\tADD\tR1, R0\t; ExpressionAdd:")
            println("\tMOV\tR0, (R6)+\t; ExpressionAdd:")
        }
    }

    companion object {
        fun isFirst(tk: CToken): Boolean {
            return tk.type == TokenType.PLUS
        }
    }
}

internal class ExpressionSub(pcx: CParseContext, private val left: CParseRule) : CParseRule() {
    // expressionAdd ::= '-' term
    private lateinit var op: CToken
    private lateinit var right: CParseRule

    @Throws(FatalErrorException::class)
    override fun parse(pcx: CParseContext) {
        // ここにやってくるときは、必ずisFirst()が満たされている
        val ct = pcx.tokenizer
        op = ct.getCurrentToken(pcx)
        // +の次の字句を読む
        val tk = ct.getNextToken(pcx)
        if (Term.isFirst(tk)) {
            right = Term(pcx).apply {
                parse(pcx)
            }
        } else {
            pcx.fatalError(tk.toExplainString() + "-の後ろはtermです")
        }
    }

    @Throws(FatalErrorException::class)
    override fun semanticCheck(pcx: CParseContext) {
        // 減算の型計算規則
        val s = arrayOf(intArrayOf(CType.T_err, CType.T_err), intArrayOf(CType.T_err, CType.T_int))
        left.semanticCheck(pcx)
        right.semanticCheck(pcx)
        val lt = left.cType.type // -の左辺の型
        val rt = right.cType.type // -の右辺の型
        val nt = s[lt][rt] // 規則による型計算
        if (nt == CType.T_err) {
            pcx.fatalError(op.toExplainString() + "左辺の型[" + left.cType.toString() + "]と右辺の型[" + right.cType.toString() + "]は引けません")
        }
        cType = getCType(nt)
        isConstant = left.isConstant && right.isConstant // +の左右両方が定数のときだけ定数
    }

    @Throws(FatalErrorException::class)
    override fun codeGen(pcx: CParseContext) {
        pcx.ioContext.outStream?.run {
            left.codeGen(pcx) // 左部分木のコード生成を頼む 左
            right.codeGen(pcx) // 右部分木のコード生成を頼む　右
            println("\tMOV\t-(R6), R1\t; ExpressionSub: ２数を取り出して、引き、積む<$op>")
            println("\tMOV\t-(R6), R0\t; ExpressionSub:")
            println("\tSUB\tR1, R0\t; ExpressionSub:")
            println("\tMOV\tR0, (R6)+\t; ExpressionSub:")
        }
    }

    companion object {
        fun isFirst(tk: CToken): Boolean {
            return tk.type == CToken.TK_MINUS
        }
    }

}