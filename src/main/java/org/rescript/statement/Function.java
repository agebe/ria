package org.rescript.statement;

import java.util.List;

import org.rescript.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Function implements Statement {
  
  private static final Logger log = LoggerFactory.getLogger(Function.class);

  private String name;

  private List<String> parameterNames;

  private BlockStatement statements;

  public Function() {
    super();
  }

//  public Function(String name, List<String> parameterNames, BlockStatement statements) {
//    super();
//    this.name = name;
//    this.parameterNames = parameterNames;
//    this.statements = statements;
//  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute function '{}'", name);
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

}
