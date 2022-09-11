package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.value.Value;

public interface Expression {
  Value eval(Expressions expressions);
  String getText();
}
