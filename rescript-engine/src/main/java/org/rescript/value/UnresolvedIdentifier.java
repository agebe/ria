package org.rescript.value;

import org.rescript.ScriptException;

public class UnresolvedIdentifier implements Value {

  private String identifier;

  public UnresolvedIdentifier(String identifier) {
    super();
    this.identifier = identifier;
  }

  @Override
  public Class<?> type() {
    throw new ScriptException("unresolved identifier '%s'".formatted(identifier));
  }

  @Override
  public String typeOf() {
    return "undefined";
  }

  @Override
  public Object val() {
    throw new ScriptException("unresolved identifier '%s'".formatted(identifier));
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    return false;
  }

  public String getIdentifier() {
    return identifier;
  }

}
