package io.github.agebe.script.lang;

public class FunctionParameter extends CachedLangItem {

  private LangItem parameter;

  public FunctionParameter(LangItem parameter) {
    super();
    this.parameter = parameter;
  }

  public LangItem getParameter() {
    return parameter;
  }

  @Override
  public String toString() {
    return "FunctionParameter [parameter=" + parameter + "]";
  }

  @Override
  protected Result resolveOnce() {
    return parameter.resolve();
  }

}
