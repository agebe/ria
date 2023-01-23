package org.ria.expression;

import java.util.List;

public interface Assignment extends TargetExpression {
  List<Identifier> identifiers();
}
