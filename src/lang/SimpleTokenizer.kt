package lang

import java.io.IOException
import java.io.InputStream
import java.io.PrintStream

class SimpleTokenizer : Tokenizer<SimpleToken, SimpleParseContext>() {
    private var lineNo = 1
    private var colNo = 0
    private var backCh = 0.toChar()
    private var backChExist = false
    private var caseSensitive = false

    private val CHAR_SPACE = 0
    private val CHAR_ALPHA = 1
    private val CHAR_NUM = 2
    private val CHAR_PUNCT = 3
    private val CHAR_COMMENT = 4
    private val charSet = IntArray(255)
    private var useHexNumber = false
    private var useOctalNumber = false

    //	private boolean useDirective = false;
    private fun setChar(s: String, type: Int) {
        for (element in s) {
            charSet[element.toInt()] = type
        }
    }

    private fun setupCharSet() {
        for (i in charSet.indices) {
            charSet[i] = CHAR_PUNCT
        }
        for (c in 'A'..'Z') {
            charSet[c.toInt()] = CHAR_ALPHA
        }
        for (c in 'a'..'z') {
            charSet[c.toInt()] = CHAR_ALPHA
        }
        for (c in '0'..'9') {
            charSet[c.toInt()] = CHAR_NUM
        }
    }

    fun setSpaceChars(s: String) = setChar(s, CHAR_SPACE)


    fun setCommentChar(c: Char) {
        charSet[c.toInt()] = CHAR_COMMENT
    }

    fun setAlphaChar(c: Char) {
        charSet[c.toInt()] = CHAR_ALPHA
    }

    fun setAlphaChars(s: String) = setChar(s, CHAR_ALPHA)

    fun useHexNumber(b: Boolean) {
        useHexNumber = b
    }

    fun useOctalNumber(b: Boolean) {
        useOctalNumber = b
    }

    fun caseSensitive(b: Boolean) {
        caseSensitive = b
    }

    private var inputStream: InputStream? = null
    private var err: PrintStream? = null
    private fun readChar(): Char {
        var ch: Char
        if (backChExist) {
            ch = backCh
            backChExist = false
        } else {
            ch = try {
                inputStream!!.read().toChar()
            } catch (e: IOException) {
                e.printStackTrace(err)
                (-1).toChar()
            }
        }
        ++colNo
        ch = if (caseSensitive) ch else Character.toLowerCase(ch)
        return ch
    }

    private fun backChar(c: Char) {
        backCh = c
        backChExist = true
        --colNo
        if (c == '\n') {
            --lineNo
        }
    }

    fun skipToNL(pctx: SimpleParseContext) {
        inputStream = pctx.ioContext.inStream
        err = pctx.ioContext.errStream
        // 構文エラー時に、行末まで読み飛ばして復帰
        var ch: Char
        do {
            ch = readChar()
        } while (ch != '\n')
        ++lineNo
        colNo = 0
    }

    private lateinit var currentTk: SimpleToken

    // 現在読み込まれているトークンを返す
    override fun getCurrentToken(pcx: SimpleParseContext): SimpleToken = currentTk

    // 次のトークンを読んで返す
    override fun getNextToken(pcx: SimpleParseContext): SimpleToken {
        inputStream = pcx.ioContext.inStream
        err = pcx.ioContext.errStream
        currentTk = readToken()
        //System.out.println("#readToken()='" + currentTk.toExplainString() + currentTk.getType());
        return currentTk
    }

    private fun readToken(): SimpleToken {
        lateinit var tk: SimpleToken
        var ch: Char
        val startCol: Int
        val text = StringBuffer()

        // 空白文字の読み飛ばし
        do {
            ch = readChar()
            if (ch == (-1).toChar()) {
                break
            } // EOF
            if (charSet[ch.toInt()] == CHAR_COMMENT) {    // コメントは行末まで読み飛ばし
                ch = readChar()
                while (ch != '\n') {
                    ch = readChar()
                }
            }
        } while (charSet[ch.toInt()] == CHAR_SPACE)
        startCol = colNo // この桁からトークンが始まる
        if (ch == (-1).toChar()) { // EOF
            tk = SimpleToken(SimpleToken.TK_EOF, lineNo, startCol, "end_of_file")
        } else {
            val s: String
            when (charSet[ch.toInt()]) {
                CHAR_ALPHA -> {
                    do {
                        text.append(ch)
                        ch = readChar()
                    } while (charSet[ch.toInt()] == CHAR_ALPHA || charSet[ch.toInt()] == CHAR_NUM)
                    backChar(ch)
                    s = text.toString()
                    tk = SimpleToken(SimpleToken.TK_IDENT, lineNo, startCol, s)
                }
                CHAR_NUM -> {
                    if (ch == '0') {
                        text.append('0')
                        ch = readChar()
                        if (useHexNumber && (ch == 'x' || ch == 'X')) {    // 16進数
                            text.append(ch)
                            ch = readChar()
                            if (charSet[ch.toInt()] == CHAR_NUM || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {
                                do {
                                    text.append(ch)
                                    ch = readChar()
                                } while (charSet[ch.toInt()] == CHAR_NUM || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f')
                                backChar(ch)
                            } else {                                        // 中途半端な16進数
                                backChar(ch)
                                tk = SimpleToken(SimpleToken.TK_ILL, lineNo, startCol, text.toString())
                            }
                        } else if (useOctalNumber) {                    // 8進数
                            while (ch in '0'..'7') {
                                text.append(ch)
                                ch = readChar()
                            }
                            backChar(ch)
                        } else {                                        // 10進数
                            while (charSet[ch.toInt()] == CHAR_NUM) {
                                text.append(ch)
                                ch = readChar()
                            }
                            backChar(ch)
                        }
                    } else {                                            // 10進数
                        do {
                            text.append(ch)
                            ch = readChar()
                        } while (charSet[ch.toInt()] == CHAR_NUM)
                        backChar(ch)
                    }
                    tk = SimpleToken(SimpleToken.TK_NUM, lineNo, startCol, text.toString())
                }
                CHAR_PUNCT -> {
                    text.append(ch)
                    s = text.toString()
                    tk = SimpleToken(SimpleToken.TK_ILL, lineNo, startCol, s)
                }
            }
        }
        if (ch == '\n') {
            ++lineNo
            colNo = 0
        }
        return tk
    }

    init {
        setupCharSet()
    }
}