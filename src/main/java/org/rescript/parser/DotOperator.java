package org.rescript.parser;

import org.rescript.ScriptException;
import org.rescript.run.Expressions;
import org.rescript.run.PackageValue;
import org.rescript.run.Value;
import org.rescript.symbol.SymbolNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotOperator implements ParseItem, Expression {

  private static final Logger log = LoggerFactory.getLogger(DotOperator.class);

  private Expression expr1;

  private TargetExpression expr2;

  public DotOperator(Expression expr1, TargetExpression expr2) {
    super();
    this.expr1 = expr1;
    this.expr2 = expr2;
  }

  @Override
  public Value eval(Expressions expressions) {
    Value val = expr1(expressions);
    log.debug("eval expr1 '{}' to '{}', isNull '{}'", expr1, val, val!=null?val.isNull():true);
    if((val != null) && (val.isNotNull())) {
      return expr2.eval(expressions, val);
    } else {
      throw new ScriptException("left expression '%s' evaluated to null".formatted(expr1));
    }
  }

  private Value expr1(Expressions expressions) {
    try {
      return expr1.eval(expressions);
    } catch(SymbolNotFoundException e) {
      return new PackageValue(expr1.getText());
    }
  }

  @Override
  public String getText() {
    return expr1 + "." + expr2;
  }

  @Override
  public String toString() {
    return getText();
  }

}
