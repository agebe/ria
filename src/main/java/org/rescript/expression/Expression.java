package org.rescript.expression;

import org.rescript.run.Expressions;
import org.rescript.value.Value;

// this should extend from ParseItem
public interface Expression {
  Value eval(Expressions expressions);
  String getText();
}
