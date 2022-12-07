package org.rescript.statement;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;

public class ForStatementBuilder extends AbstractStatement implements ContainerStatement {

  private ForInitStatement forInit;

  private Expression forTerm;

  private List<Expression> forInc;

  private Statement statement;

  public ForStatementBuilder(int lineNumber) {
    super(lineNumber);
  }

  public ForInitStatement getForInit() {
    return forInit;
  }

  public void setForInit(ForInitStatement forInit) {
    this.forInit = forInit;
  }

  public Expression getForTerm() {
    return forTerm;
  }

  public void setForTerm(Expression forTerm) {
    this.forTerm = forTerm;
  }

  public List<Expression> getForInc() {
    return forInc;
  }

  public void setForInc(List<Expression> forInc) {
    this.forInc = forInc;
  }

  public Statement getStatement() {
    return statement;
  }

  public ForStatement create() {
    return new ForStatement(this.getLineNumber(), forInit, forTerm, forInc, statement);
  }

  @Override
  public void execute(ScriptContext ctx) {
    throw new ScriptException("not supported on ForStatementBuilder");
    
  }

  @Override
  public void addStatement(Statement statement) {
    if(this.statement == null) {
      this.statement = statement;
    } else {
      throw new ScriptException("statement already set");
    }
  }

}
