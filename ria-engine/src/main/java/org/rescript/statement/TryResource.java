package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.expression.Identifier;
import org.rescript.parser.ParseItem;
import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class TryResource implements ParseItem {

  private Type type;

  private Identifier identifier;

  private Expression expression;

  private AutoCloseable closeable;

  public TryResource(Type type, Identifier identifier, Expression expression) {
    super();
    this.type = type;
    this.identifier = identifier;
    this.expression = expression;
  }

  public void execute(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    Object o = v.val();
    if(o == null) {
      closeable = null;
    } else if(o instanceof AutoCloseable c) {
      closeable = c;
    } else {
      throw new ScriptException("try-with-resouce requires AutoCloseable but got '%s'".formatted(v.type()));
    }
    ctx.getSymbols().getScriptSymbols().defineVar(identifier.getIdent(), v, type);
  }

  public void close() throws Exception {
    if(closeable != null) {
      closeable.close();
    }
  }

}
