package org.rescript.expression;

import org.rescript.parser.ParseItem;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public interface Expression extends ParseItem {
  Value eval(ScriptContext ctx);
  String getText();
}
