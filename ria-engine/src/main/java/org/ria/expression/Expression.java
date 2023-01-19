package org.ria.expression;

import org.ria.parser.ParseItem;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public interface Expression extends ParseItem {
  Value eval(ScriptContext ctx);
}
