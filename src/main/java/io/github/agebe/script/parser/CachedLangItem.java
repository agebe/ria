//package io.github.agebe.script.parser;
//
//import io.github.agebe.script.ScriptException;
//
//public abstract class CachedLangItem implements ParseItem {
//
//  private Result cached;
//
//  @Override
//  public Result resolve() {
//    if(cached == null) {
//      cached = resolveOnce();
//    }
//    return cached;
//  }
//
//  protected Result resolveOnce() {
//    throw new ScriptException("%s can't be resolved, not implemented".formatted(this.getClass().getName()));
//  }
//
//}
