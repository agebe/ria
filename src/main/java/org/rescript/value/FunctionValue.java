package org.rescript.value;

import java.util.List;
import java.util.Objects;

import org.rescript.statement.Function;

public class FunctionValue implements Value {

  private List<Function> functions;

  public FunctionValue(List<Function> functions) {
    super();
    this.functions = functions;
  }

  @Override
  public Class<?> type() {
    return Function.class;
  }

  @Override
  public String typeOf() {
    return "function";
  }

  @Override
  public Object val() {
    return functions;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean equalsOp(Value other) {
    if(other == null) {
      return false;
    } else if(other.isFunction()) {
     return Objects.equals(val(), other.val());
    } else {
      return false;
    }
  }

  public List<Function> getFunctions() {
    return functions;
  }

  @Override
  public boolean isFunction() {
    return true;
  }

  @Override
  public FunctionValue toFunctionValue() {
    return this;
  }

}
