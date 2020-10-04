package lang

enum class TokenType {
    IDENT,  // 識別子（ラベル）
    NUM,    // 数値
    EOF,    // （ファイルの終端記号）
    ILL,    // 未定義トークン
    PLUS,   // +
    MINUS
}