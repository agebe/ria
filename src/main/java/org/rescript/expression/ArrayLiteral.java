package org.rescript.expression;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.ArrayValue;
import org.rescript.value.IntArrayValue;
import org.rescript.value.LongArrayValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayLiteral implements Expression {

  private static final Logger log = LoggerFactory.getLogger(ArrayLiteral.class);

  private List<Expression> expressions;

  public ArrayLiteral(List<Expression> expressions) {
    super();
    this.expressions = expressions;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    List<Value> vals = expressions.stream().map(expr -> expr.eval(ctx)).toList();
    Class<?> cls = commonSuperClass(vals.stream()
        .map(v -> v.type())
        .toArray(Class<?>[]::new));
    log.debug("common super class '{}'", cls.getName());
    if(cls.equals(double.class)) {
      throw new ScriptException("double array literal not implemented yet");
    } else if(cls.equals(float.class)) {
      throw new ScriptException("float array literal not implemented yet");
    } else if(cls.equals(int.class)) {
      int[] arr = new int[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toInt();
      }
      return new IntArrayValue(arr);
    } else if(cls.equals(long.class)) {
      long[] arr = new long[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toLong();
      }
      return new LongArrayValue(arr);
    } else if(cls.equals(byte.class)) {
      throw new ScriptException("byte array literal not implemented yet");
    } else if(cls.equals(short.class)) {
      throw new ScriptException("short array literal not implemented yet");
    } else if(cls.equals(char.class)) {
      throw new ScriptException("char array literal not implemented yet");
    } else if(cls.equals(boolean.class)) {
      throw new ScriptException("boolean array literal not implemented yet");
    } else {
      Object[] arr = (Object[])Array.newInstance(cls, vals.size());
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).val();
      }
      log.debug("array type '{}'", arr.getClass().getName());
      return new ArrayValue(arr, arr.getClass());
    }
  }

  private static Class<?> commonSuperClass(Class<?>... classes) {
    if(classes.length > 0) {
      if(allPrimitive(classes)) {
        return accommodatesAllPrimitives(classes);
      } else {
        List<Class<?>> l = commonSuperClasses(classes);
        if(!l.isEmpty()) {
          return l.get(0);
        }
      }
    }
    return Object.class;
  }

  private static boolean allPrimitive(Class<?>... classes) {
    return Arrays.stream(classes).allMatch(cls -> cls.isPrimitive());
  }

  private static Class<?> accommodatesAllPrimitives(Class<?>... classes) {
    Class<?> acc = classes[0];
    for(int i = 1;i<classes.length;i++) {
      if(acc.equals(double.class)) {
        break;
      }
      Class<?> c = classes[i];
      if(acc.equals(c)) {
        continue;
      }
      if(isWiderThan(c, acc)) {
        acc = c;
      }
    }
    return acc;
  }

  private static Map<Class<?>, Integer> pmap = Map.of(
      double.class, 0,
      float.class, 1,
      long.class, 2,
      int.class, 3,
      short.class, 4,
      char.class, 5,
      byte.class, 6,
      boolean.class, 7);

  private static boolean isWiderThan(Class<?> c1, Class<?> c2) {
    int p1 = pmap.get(c1);
    int p2 = pmap.get(c2);
    return p1 < p2;
  }

  // from https://stackoverflow.com/a/9797689
  private static List<Class<?>> commonSuperClasses(Class<?>... classes) {
    // start off with set from first hierarchy
    Set<Class<?>> rollingIntersect = new LinkedHashSet<Class<?>>(
        getClassesBfs(classes[0]));
    // intersect with next
    for (int i = 1; i < classes.length; i++) {
      rollingIntersect.retainAll(getClassesBfs(classes[i]));
    }
    return new LinkedList<Class<?>>(rollingIntersect);
  }

  private static Set<Class<?>> getClassesBfs(Class<?> clazz) {
    Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
    Set<Class<?>> nextLevel = new LinkedHashSet<Class<?>>();
    nextLevel.add(clazz);
    do {
      classes.addAll(nextLevel);
      Set<Class<?>> thisLevel = new LinkedHashSet<Class<?>>(nextLevel);
      nextLevel.clear();
      for (Class<?> each : thisLevel) {
        Class<?> superClass = each.getSuperclass();
        if (superClass != null && superClass != Object.class) {
          nextLevel.add(superClass);
        }
        for (Class<?> eachInt : each.getInterfaces()) {
          nextLevel.add(eachInt);
        }
      }
    } while (!nextLevel.isEmpty());
    return classes;
  }

}
