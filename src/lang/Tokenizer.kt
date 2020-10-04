package lang

abstract class Tokenizer<TKN : Token, PCTX : ParseContext> {
    abstract fun getCurrentToken(pcx: PCTX): TKN // 既に読み込まれている最新の字句を返す
    abstract fun getNextToken(pcx: PCTX): TKN // 新たに字句をひとつ読み込んで返す
}