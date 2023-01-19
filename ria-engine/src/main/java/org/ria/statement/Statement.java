package org.ria.statement;

import org.ria.parser.ParseItem;
import org.ria.run.ScriptContext;

public interface Statement extends ParseItem {
  void execute(ScriptContext ctx);
  int getLineNumber();
  void setLineNumber(int lineNumber);
}
