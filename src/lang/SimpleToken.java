package lang;

public class SimpleToken extends Token {
    private TokenType type;                // 上のどのトークンか
    private String text;                // 切り出したトークンの綴り
    private int lineNo;                // このトークンがあった行
    private int colNo;                // このトークンがあった桁

    @Override
    public TokenType getType() {
        return type;
    }

    @Override
    public String getText() {
        return text == null ? "(null)" : text;
    }

    @Override
    public int getLineNo() {
        return lineNo;
    }

    @Override
    public int getColumnNo() {
        return colNo;
    }

    public int getIntValue() {
        return Integer.decode(text).intValue();
    }

    public SimpleToken(TokenType type, int lineNo, int colNo, String s) {
        this.type = type;
        this.lineNo = lineNo;
        this.colNo = colNo;
        this.text = s;
    }
}
