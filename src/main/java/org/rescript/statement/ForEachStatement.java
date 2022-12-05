package org.rescript.statement;

import java.util.Iterator;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Array;
import org.rescript.value.Value;
import org.rescript.parser.Type;

//https://docs.oracle.com/javase/8/docs/technotes/guides/language/foreach.html
public class ForEachStatement extends AbstractLoop implements ContainerStatement {

  private String identifier;

  private Type type;

  private Expression iterable;

  private Statement statement;

  public ForEachStatement() {
    super();
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void setType(Type type) {
    this.type = type;
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
    Value v = iterable.eval(ctx);
    if(v == null) {
      throw new ScriptException("for each iterable '{}' did not resolve".formatted(iterable));
    }
    if(v.val() instanceof Iterable<?> iterable) {
      forEach(iterable.iterator(), ctx);
    } else if(v instanceof Array a) {
      forEach(a, ctx);
    } else {
      throw new ScriptException("for each can only iterate over an Iterable or Array, but not '%s'"
          .formatted(v.type().getName()));
    }
  }

  private void forEach(Iterator<?> iter, ScriptContext ctx) {
    while(iter.hasNext()) {
      try {
        ctx.getSymbols().getScriptSymbols().enterScope();
        Object o = iter.next();
        ctx.getSymbols().getScriptSymbols().defineVar(identifier, Value.of(o), type);
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
  }

  private void forEach(Array a, ScriptContext ctx) {
    for(int i=0;i<a.length();i++) {
      try {
        ctx.getSymbols().getScriptSymbols().enterScope();
        Object o = a.get(i).val();
        ctx.getSymbols().getScriptSymbols().defineVar(identifier, Value.of(o), type);
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
  }

}
