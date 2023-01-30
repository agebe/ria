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

import java.util.List;

import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.run.ScriptContext;

public class ForStatementBuilder extends AbstractStatement implements ContainerStatement {

  private ForInitStatement forInit;

  private Expression forTerm;

  private List<Expression> forInc;

  private Statement statement;

  public ForStatementBuilder(int lineNumber) {
    super(lineNumber);
  }

  public ForInitStatement getForInit() {
    return forInit;
  }

  public void setForInit(ForInitStatement forInit) {
    this.forInit = forInit;
  }

  public Expression getForTerm() {
    return forTerm;
  }

  public void setForTerm(Expression forTerm) {
    this.forTerm = forTerm;
  }

  public List<Expression> getForInc() {
    return forInc;
  }

  public void setForInc(List<Expression> forInc) {
    this.forInc = forInc;
  }

  public Statement getStatement() {
    return statement;
  }

  public ForStatement create() {
    return new ForStatement(this.getLineNumber(), forInit, forTerm, forInc, statement);
  }

  @Override
  public void execute(ScriptContext ctx) {
    throw new ScriptException("not supported on ForStatementBuilder");
    
  }

  @Override
  public void addStatement(Statement statement) {
    if(this.statement == null) {
      this.statement = statement;
    } else {
      throw new ScriptException("statement already set");
    }
  }

}
