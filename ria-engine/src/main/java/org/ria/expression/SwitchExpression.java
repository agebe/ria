/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.expression;

import java.util.ArrayList;
import java.util.List;

import org.ria.run.ScriptContext;
import org.ria.statement.Breakable;
import org.ria.value.Value;
import org.ria.value.VoidValue;

//https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
//https://docs.oracle.com/en/java/javase/16/language/switch-expressions.html
public class SwitchExpression implements Expression, Breakable {

  private Expression switchExpression;

  private List<SwitchColonCase> colonCases = new ArrayList<>();

  private List<SwitchArrowCase> arrowCases = new ArrayList<>();

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

  public void addArrowCase(SwitchArrowCase c) {
    arrowCases.add(c);
  }

  @Override
  public Value eval(ScriptContext ctx) {
    ctx.setLastResult(VoidValue.VOID);
    Value v = switchExpression.eval(ctx);
    try {
      ctx.getCurrentFrame().pushBreakable(this);
      if(!colonCases.isEmpty()) {
        return evalColonCases(ctx, v);
      } else if(!arrowCases.isEmpty()) {
        return evalArrowCases(ctx, v);
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

  private Value evalArrowCases(ScriptContext ctx, Value v) {
    for(SwitchArrowCase c : arrowCases) {
      if(c.isCase(ctx, v)) {
        c.execute(ctx);
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
