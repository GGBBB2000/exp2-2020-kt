package lang.c

import lang.TokenType
import lang.Tokenizer
import java.io.IOException
import java.io.InputStream
import java.io.PrintStream

class CTokenizer constructor(private  val rule: CTokenRule)
    : Tokenizer<CToken, CParseContext>(){
    private var lineNo = 1
    private var colNo = 1
    private var backCh = 0.toChar()
    private var backChExist = false
    private var inputStream: InputStream? = null
    private var err: PrintStream? = null
    private fun readChar(): Char {
        val ch: Char
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
        if (ch == '\n') {
            colNo = 1
            ++lineNo
        }
        //		System.out.print("'"+ch+"'("+(int)ch+")");
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

    // 現在読み込まれているトークンを返す
    private lateinit var currentTk: CToken

    override fun getCurrentToken(pcx: CParseContext): CToken = currentTk

    override fun getNextToken(pcx: CParseContext): CToken {
        inputStream = pcx.ioContext.inStream
        err = pcx.ioContext.errStream
        currentTk = readToken()
        //System.out.println("#readToken()='" + currentTk.toExplainString() + currentTk.getType());
        return currentTk
    }

    private fun readToken(): CToken {
        lateinit var tk: CToken
        var ch: Char
        var startCol = colNo
        val text = StringBuffer()
        var state = 0
        var accept = false
        while (!accept) {
            when (state) {
                0 -> {
                    ch = readChar()
                    if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                    } else if (ch == (-1).toChar()) {    // EOF
                        startCol = colNo - 1
                        state = 1
                    } else if (ch in '0'..'9') {
                        startCol = colNo - 1
                        text.append(ch)
                        state = 3
                    } else if (ch == '+') {
                        startCol = colNo - 1
                        text.append(ch)
                        state = 4
                    } else {            // ヘンな文字を読んだ
                        startCol = colNo - 1
                        text.append(ch)
                        state = 2
                    }
                }
                1 -> {
                    tk = CToken(TokenType.EOF, lineNo, startCol, "end_of_file")
                    accept = true
                }
                2 -> {
                    tk = CToken(TokenType.ILL, lineNo, startCol, text.toString())
                    accept = true
                }
                3 -> {
                    ch = readChar()
                    if (Character.isDigit(ch)) {
                        text.append(ch)
                    } else {
                        // 数の終わり
                        backChar(ch) // 数を表さない文字は戻す（読まなかったことにする）
                        tk = CToken(TokenType.NUM, lineNo, startCol, text.toString())
                        accept = true
                    }
                }
                4 -> {
                    tk = CToken(TokenType.PLUS, lineNo, startCol, "+")
                    accept = true
                }
            }
        }
        return tk
    }
}