package org.rescript.expression;

import org.rescript.parser.ParseItem;
import org.rescript.run.Expressions;
import org.rescript.value.Value;

public interface Expression extends ParseItem {
  Value eval(Expressions expressions);
  String getText();
}
