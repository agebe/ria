package org.ria.statement;

import java.util.List;

import org.ria.ScriptException;
import org.ria.expression.Assignment;
import org.ria.run.ScriptContext;

public class ForInitStatement extends AbstractStatement implements Statement {

  private VardefStatement vardef;

  private List<Assignment> assigments;

  public ForInitStatement(int lineNumber) {
    super(lineNumber);
  }

  public ForInitStatement(int lineNumber, VardefStatement vardef) {
    super(lineNumber);
    this.vardef = vardef;
  }

  public ForInitStatement(int lineNumber, List<Assignment> assigments) {
    super(lineNumber);
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