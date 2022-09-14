package org.rescript.statement;

import java.util.ArrayList;
import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfStatement extends AbstractStatement {

  private static final Logger log = LoggerFactory.getLogger(IfStatement.class);

  private Expression expression;

  private List<Statement> statements = new ArrayList<>();

  public IfStatement() {
    super();
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.toBoolean()) {
      statements.get(0).execute(ctx);
    } else {
      if(statements.size() == 2) {
        statements.get(1).execute(ctx);
      }
    }
  }

  @Override
  public void addStatement(Statement statement) {
    if(statements.isEmpty()) {
      log.debug("adding true branch to if statement, '{}'", statement);
      statements.add(statement);
    } else if(statements.size() == 1) {
      log.debug("adding false branch to if statement '{}'", statement);
      statements.add(statement);
    } else {
      throw new ScriptException("if statement already has 2 branches");
    }
  }

}
