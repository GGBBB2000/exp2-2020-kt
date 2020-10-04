package lang.c;

import lang.SimpleToken;
import lang.TokenType;

public class CToken extends SimpleToken {     // +

    public CToken(TokenType type, int lineNo, int colNo, String s) {
        super(type, lineNo, colNo, s);
    }
}
