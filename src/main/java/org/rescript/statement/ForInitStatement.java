package org.rescript.statement;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.Assignment;
import org.rescript.run.ScriptContext;

public class ForInitStatement implements Statement {

  private VardefStatement vardef;

  private List<Assignment> assigments;

  public ForInitStatement(VardefStatement vardef) {
    super();
    this.vardef = vardef;
  }

  public ForInitStatement(List<Assignment> assigments) {
    super();
    this.assigments = assigments;
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(vardef != null) {
      vardef.execute(ctx);
    } else if(assigments != null) {
      assigments.forEach(a -> a.eval(ctx));
    } else {
      throw new ScriptException("invalid state");
    }
  }

}
