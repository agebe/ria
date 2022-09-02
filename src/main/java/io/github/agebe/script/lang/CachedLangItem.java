package io.github.agebe.script.lang;

import io.github.agebe.script.ScriptException;

public abstract class CachedLangItem implements LangItem {

  private LangItemResult cached;

  @Override
  public LangItemResult resolve() {
    if(cached == null) {
      cached = resolveOnce();
    }
    return cached;
  }

  protected LangItemResult resolveOnce() {
    throw new ScriptException("%s can't be resolved, not implemented".formatted(this.getClass().getName()));
  }

}
