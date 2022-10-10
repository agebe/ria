package org.rescript.statement;

public interface ContainerStatement extends Statement {
  void addStatement(Statement statement);
}
