package org.rescript.statement;

import java.util.Iterator;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

//https://docs.oracle.com/javase/8/docs/technotes/guides/language/foreach.html
public class ForEachStatement extends AbstractLoop implements ContainerStatement {

  private String identifier;

  private Expression iterable;

  private Statement statement;

  public ForEachStatement() {
    super();
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public Expression getIterable() {
    return iterable;
  }

  public void setIterable(Expression iterable) {
    this.iterable = iterable;
  }

  @Override
  public void addStatement(Statement statement) {
    if(this.statement == null) {
      this.statement = statement;
    } else {
      throw new ScriptException("forEach already has a statement");
    }
  }

  @Override
  protected void executeLoop(ScriptContext ctx) {
    Value it = iterable.eval(ctx);
    if(it == null) {
      throw new ScriptException("for each iterable '{}' did not resolve".formatted(iterable));
    }
    if(it.val() instanceof Iterable<?> iterable) {
      Iterator<?> iter = iterable.iterator();
      while(iter.hasNext()) {
        try {
          ctx.getSymbols().getScriptSymbols().enterScope();
          Object o = iter.next();
          ctx.getSymbols().getScriptSymbols().defineVar(identifier, Value.of(o));
          clearContinue();
          statement.execute(ctx);
          if(ctx.isReturnFlag()) {
            break;
          }
          if(isBreak()) {
            break;
          }
        } finally {
          ctx.getSymbols().getScriptSymbols().exitScope();
        }
      }
    } else {
      throw new ScriptException("for each can only iterate over an Iterable, but not '%s'"
          .formatted(it.type().getName()));
    }
  }

}
