package org.rescript.expression;

import java.util.ArrayList;
import java.util.List;

import org.rescript.run.ScriptContext;
import org.rescript.statement.Breakable;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class SwitchExpression implements Expression, Breakable {

  private Expression switchExpression;

  private List<SwitchColonCase> colonCases = new ArrayList<>();

  private boolean breakFlag;

  public Expression getSwitchExpression() {
    return switchExpression;
  }

  public void setSwitchExpression(Expression switchExpression) {
    this.switchExpression = switchExpression;
  }

  public void addColonCase(SwitchColonCase c) {
    colonCases.add(c);
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = switchExpression.eval(ctx);
    try {
      ctx.getCurrentFrame().pushBreakable(this);
      if(!colonCases.isEmpty()) {
        return evalColonCases(ctx, v);
      } else {
        return VoidValue.VOID;
      }
    } finally {
      ctx.getCurrentFrame().popBreakable(this);
    }
  }

  private Value evalColonCases(ScriptContext ctx, Value v) {
    boolean fallThrough = false;
    for(SwitchColonCase c : colonCases) {
      if(fallThrough) {
        c.execute(ctx);
      } else if(c.isCase(ctx, v)) {
        fallThrough = true;
        c.execute(ctx);
      }
      if(isBreak()) {
        break;
      }
    }
    return ctx.getLastResult();
  }

  @Override
  public void setBreak() {
    breakFlag = true;
  }

  @Override
  public boolean isBreak() {
    return breakFlag;
  }

}
