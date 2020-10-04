package lang.c

class CType private constructor(val type: Int, private val string: String) {
    fun isCType(t: Int): Boolean = t == type

    override fun toString(): String = string

    companion object {
        const val T_err = 0 // 型エラー
        const val T_int = 1 // int
        const val T_pint = 2 // int*
        private val typeArray = arrayOf(
                CType(T_err, "error"),
                CType(T_int, "int"),
                CType(T_pint, "int*"))

        @JvmStatic
        fun getCType(type: Int): CType {
            return typeArray[type]
        }
    }

}