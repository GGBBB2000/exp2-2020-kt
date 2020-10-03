package lang

abstract class Token {
    abstract val type: Int
    abstract val text: String
    abstract val lineNo: Int
    abstract val columnNo: Int
    fun toExplainString(): String =
            "[" + lineNo + "行目," + columnNo + "文字目の'" + text + "']"
}