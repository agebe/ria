package org.rescript.statement;

import org.rescript.parser.ParseItem;
import org.rescript.run.ScriptContext;

public interface Statement extends ParseItem {
  void execute(ScriptContext ctx);
  int getLineNumber();
  void setLineNumber(int lineNumber);
}
