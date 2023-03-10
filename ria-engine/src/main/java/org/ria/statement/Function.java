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
package org.ria.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.expression.FunctionCall;
import org.ria.run.ScriptContext;
import org.ria.value.FunctionValue;
import org.ria.value.Value;
import org.ria.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Function extends AbstractStatement implements Statement, Expression {
  
  private static final Logger log = LoggerFactory.getLogger(Function.class);

  private String name;

  private List<String> parameterNames;

  private BlockStatement statements;

  private List<Function> nestedFunctions = new ArrayList<>();

  private Function parent;


  public Function(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return new FunctionValue(List.of(this));
  }

  @Override
  public void execute(ScriptContext ctx) {
    // functions can only be executed via function call.
    // if we do encounter function definitions in the program flow just ignore and move on...
  }

  public void executeFunction(ScriptContext ctx) {
    try {
      ctx.enterFunction(this);
      ctx.setLastResult(VoidValue.VOID);
      // do not create a variable scope for the implicit root (main) function
      // the variables declared outside any function in the script have
      // global scope and can also be accessed from outside the script (via Script API)
      // and therefore need to survive the run (local variables are garbage collected on exitScope)
      // script functions have min 2 scopes, 1 for the function parameters created here, the 2nd scope is created
      // when the function block is entered
      if(parent != null) {
        ctx.getSymbols().getScriptSymbols().enterScope();
      }
      log.debug("execute function '{}'", name);
      ListIterator<String> listIterator = parameterNames.listIterator(parameterNames.size());
      while (listIterator.hasPrevious()) {
        String paramName = listIterator.previous();
        Value val = ctx.getStack().pop();
        log.debug("define local variable '{}' with value '{}'", paramName, val);
        ctx.getSymbols().getScriptSymbols().defineVar(paramName, val);
      }
      statements.execute(ctx);
    } finally {
      ctx.getStack().push(ctx.getLastResult());
      ctx.setReturnFlag(false);
      if(parent != null) {
        ctx.getSymbols().getScriptSymbols().exitScope();
      }
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

  public boolean matchesName(String name) {
    return StringUtils.equals(name, this.name);
  }

  public boolean matchesParameters(FunctionCall call) {
    return call.getParameters().size() == this.parameterNames.size();
  }

  public boolean matches(FunctionCall call) {
    return matchesName(call.getName().getName()) && matchesParameters(call);
  }

  public static Function main() {
    Function f = new Function(0);
    f.name = "__main";
    f.parameterNames = new ArrayList<>();
    f.statements = new BlockStatement(0, true);
    return f;
  }

  @Override
  public String toString() {
    return "Function [name=" + name + "]";
  }

}
