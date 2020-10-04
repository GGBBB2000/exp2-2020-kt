package lang.c

import lang.SimpleToken
import lang.TokenType

class CToken
(type: TokenType, lineNo: Int, colNo: Int, s: String) : SimpleToken(type, lineNo, colNo, s)