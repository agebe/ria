package org.rescript.statement;

import org.rescript.run.ScriptContext;

public interface Statement {
  void execute(ScriptContext ctx);
}
