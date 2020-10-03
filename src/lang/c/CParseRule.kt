package lang.c

import lang.Compiler
import lang.LL1
import lang.ParseRule

abstract class CParseRule : ParseRule<CParseContext>(), Compiler<CParseContext>, LL1<CToken> {
    // この節点の（推測される）型
    lateinit var cType: CType

    // この節点は定数を表しているか？
    var isConstant = false

}