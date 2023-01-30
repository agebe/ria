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

import org.ria.ScriptException;
import org.ria.expression.Expression;
import org.ria.run.ScriptContext;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfStatement extends AbstractStatement implements ContainerStatement {

  private static final Logger log = LoggerFactory.getLogger(IfStatement.class);

  private Expression expression;

  private List<Statement> statements = new ArrayList<>();

  public IfStatement(int lineNumber) {
    super(lineNumber);
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    if(v.toBoolean()) {
      statements.get(0).execute(ctx);
    } else {
      if(statements.size() == 2) {
        statements.get(1).execute(ctx);
      }
    }
  }

  @Override
  public void addStatement(Statement statement) {
    if(statements.isEmpty()) {
      log.debug("adding true branch to if statement, '{}'", statement);
      statements.add(statement);
    } else if(statements.size() == 1) {
      log.debug("adding false branch to if statement '{}'", statement);
      statements.add(statement);
    } else {
      throw new ScriptException("if statement already has 2 branches");
    }
  }

}
