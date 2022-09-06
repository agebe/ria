package io.github.agebe.script.parser;

import io.github.agebe.script.run.Expressions;
import io.github.agebe.script.run.Value;

public interface Expression {
  Value eval(Expressions expressions);
}
