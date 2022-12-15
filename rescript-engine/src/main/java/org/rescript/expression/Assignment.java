package org.rescript.expression;

import java.util.List;

public interface Assignment extends Expression {
  List<Identifier> identifiers();
}
