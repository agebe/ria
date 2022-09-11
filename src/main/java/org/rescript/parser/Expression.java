package org.rescript.parser;

import org.rescript.run.Expressions;
import org.rescript.run.Value;

public interface Expression {
  Value eval(Expressions expressions);
  String getText();
}
