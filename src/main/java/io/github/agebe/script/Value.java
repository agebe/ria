package io.github.agebe.script;

public class Value {

  private Object obj;

  private Class<?> cls;

  private boolean b;

  private double d;

  private float f;

  private long l;

  private int i;

  private char c;

  // void
  private boolean v;

  public Object getObj() {
    return obj;
  }

  public Class<?> getCls() {
    return cls;
  }

  public double getD() {
    return d;
  }

  public float getF() {
    return f;
  }

  public long getL() {
    return l;
  }

  public int getI() {
    return i;
  }

  public char getC() {
    return c;
  }

  public boolean isV() {
    return v;
  }

  public Class<?> paramType() {
    return cls;
  }

  public Object getParamObj() {
    // TODO
    if(cls == boolean.class) {
      return b;
    } else {
      return obj;
    }
  }

  public boolean getB() {
    return b;
  }

  public static Value cls(Class<?> cls) {
    Value v = new Value();
    v.cls = cls;
    return v;
  }

  public static Value vd() {
    Value v = new Value();
    v.cls = Void.class;
    v.v = true;
    return v;
  }

  public static Value ofObj(Object o, Class<?> type) {
    Value v = new Value();
    v.obj = o;
    v.cls = type;
    return v;
  }

  public static Value ofBoolean(Object o) {
    Value v = new Value();
    v.b = (Boolean)o;
    v.cls = boolean.class;
    return v;
  }

}
