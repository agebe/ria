package org.ria.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.ria.SyntaxException;

public class SyntaxExceptionErrorListener extends BaseErrorListener {

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
      String msg, RecognitionException e) {
    throw new SyntaxException("line " + line + ":" + charPositionInLine + " " + msg);
  }

}
