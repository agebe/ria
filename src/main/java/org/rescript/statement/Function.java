package org.rescript.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.expression.FunctionCall;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Function implements Statement {
  
  private static final Logger log = LoggerFactory.getLogger(Function.class);

  private String name;

  private List<String> parameterNames;

  private BlockStatement statements;

  private List<Function> nestedFunctions = new ArrayList<>();

  private Function parent;

  public Function() {
    super();
  }

  @Override
  public void execute(ScriptContext ctx) {
    try {
      ctx.enterFunction(this);
      ctx.setLastResult(VoidValue.VOID);
      ctx.getSymbols().getScriptSymbols().enterScope();
      ListIterator<String> listIterator = parameterNames.listIterator(parameterNames.size());
      while (listIterator.hasPrevious()) {
        String paramName = listIterator.previous();
        Value val = ctx.getStack().pop();
        ctx.getSymbols().getScriptSymbols().defineVar(paramName, val);
      }
      log.debug("execute function '{}'", name);
      statements.execute(ctx);
    } finally {
      ctx.getStack().push(ctx.getLastResult());
      ctx.setReturnFlag(false);
      ctx.getSymbols().getScriptSymbols().exitScope();
      ctx.exitFunction(this);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getParameterNames() {
    return parameterNames;
  }

  public void setParameterNames(List<String> parameterNames) {
    this.parameterNames = parameterNames;
  }

  public BlockStatement getStatements() {
    return statements;
  }

  public void setStatements(BlockStatement statements) {
    this.statements = statements;
  }

  public void addFunction(Function function) {
    if(function.getParent() != null) {
      throw new ScriptException("function '%s' already has parent".formatted(function));
    }
    function.setParent(this);
    nestedFunctions.add(function);
  }

  public List<Function> getNestedFunctions() {
    return nestedFunctions;
  }

  public Function getParent() {
    return parent;
  }

  public void setParent(Function parent) {
    this.parent = parent;
  }

  public boolean matches(FunctionCall call) {
    if(!StringUtils.equals(call.getName().getName(), name)) {
      return false;
    }
    return call.getParameters().size() == this.parameterNames.size();
  }

  public static Function main() {
    Function f = new Function();
    f.name = "main";
    f.parameterNames = new ArrayList<>();
    f.statements = new BlockStatement(true);
    return f;
  }

  @Override
  public String toString() {
    return "Function [name=" + name + "]";
  }

}
