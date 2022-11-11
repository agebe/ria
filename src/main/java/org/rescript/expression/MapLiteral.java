package org.rescript.expression;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class MapLiteral implements Expression {

  private List<Map.Entry<Expression, Expression>> entries;

  public MapLiteral(List<Entry<Expression, Expression>> entries) {
    super();
    this.entries = entries;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Map<Object, Object> map = new LinkedHashMap<>();
    entries.forEach(me -> map.put(me.getKey().eval(ctx).val(), me.getValue().eval(ctx).val()));
    return Value.of(Map.class, map);
  }

}
