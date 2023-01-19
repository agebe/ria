package org.ria.expression;

public abstract class TripleOp implements Expression {

  private Expression exp1;

  private Expression exp2;

  private String op;

  public TripleOp(Expression exp1, Expression exp2, String op) {
    super();
    this.exp1 = exp1;
    this.exp2 = exp2;
    this.op = op;
  }

  public Expression getExp1() {
    return exp1;
  }

  public Expression getExp2() {
    return exp2;
  }

  public String getOp() {
    return op;
  }

  @Override
  public String getText() {
    return exp1.getText() + op + exp2.getText();
  }

  @Override
  public String toString() {
    return "(" + getText() + ")";
  }

}

