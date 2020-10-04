package lang

import java.io.*

class IOContext constructor(var inputFileName: String, var outStream: PrintStream?, var errStream: PrintStream?) {

    //	private String outputFileName;
    //	private String errFileName;
    var inStream: InputStream? = null

    private fun openInput(inputFileName: String) {
        // inputFileNameをオープンしてinにつなぐ
        try {
            inStream = FileInputStream(inputFileName)
        } catch (e: FileNotFoundException) {
            e.printStackTrace(errStream)
        }
    }

    fun allClose() = try {
        inStream = inStream?.let {
            it.close()
            null
        }
        outStream = outStream?.let {
            it.close()
            null
        }
        errStream = errStream?.let {
            it.close()
            null
        }
    } catch (e: IOException) {
        e.printStackTrace(errStream)
    }

    init {
        openInput(inputFileName)
    }
}