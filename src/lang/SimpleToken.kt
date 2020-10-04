package lang

open class SimpleToken(// 上のどのトークンか
        override val type: TokenType, // このトークンがあった行
        override val lineNo: Int, // このトークンがあった桁
        override val columnNo: Int, // 切り出したトークンの綴り
        override val text: String) : Token() {

    val intValue: Int
        get() = Integer.decode(text).toInt()

}