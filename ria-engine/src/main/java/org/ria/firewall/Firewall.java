package org.ria.firewall;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.ria.ScriptException;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Firewall {

  private static final Logger log = LoggerFactory.getLogger(Firewall.class);

  private List<FieldRule> fieldRules = new ArrayList<>();

  private List<ConstructorRule> constructorRules = new ArrayList<>();

  private List<MethodRule> methodRules = new ArrayList<>();

  private RuleAction defaultFieldAction = RuleAction.ACCEPT;

  private RuleAction defaultConstructorAction = RuleAction.ACCEPT;

  private RuleAction defaultMethodAction = RuleAction.ACCEPT;

  public Firewall() {
    super();
  }

  private FieldRule match(Field f, FieldAccess access) {
    if(fieldRules == null) {
      return null;
    }
    return fieldRules.stream()
        .filter(fr -> fr.matches(f, access))
        .findFirst()
        .orElse(null);
  }

  private Object checkAccess(Field f, FieldAccess access, Supplier<Object> supplier) {
    FieldRule rule = match(f, access);
    RuleAction action = rule!=null?rule.getAction():defaultFieldAction;
    if(RuleAction.ACCEPT.equals(action)) {
      return supplier.get();
    } else if(RuleAction.DENY.equals(action)) {
      throw new AccessDeniedException("field '%s', access denied".formatted(f));
    } else if(RuleAction.DROP.equals(action)) {
      log.debug("firewall drop action on field access '%s'".formatted(f));
      return null;
    } else if(RuleAction.INTERCEPT.equals(action)) {
      // TODO
      throw new ScriptException("firewall action '%s' not implemeneted".formatted(action));
    } else {
      throw new ScriptException("firewall action '%s' not implemeneted".formatted(action));
    }
  }

  private void setField(Field field, Object owner, Value v) throws IllegalArgumentException, IllegalAccessException {
    if(v.isPrimitive()) {
      if(v.isDouble()) {
        field.setDouble(owner, v.toDouble());
      } else if(v.isFloat()) {
        field.setFloat(owner, v.toFloat());
      } else if(v.isLong()) {
        field.setLong(owner, v.toLong());
      } else if(v.isInteger()) {
        field.setInt(owner, v.toInt());
      } else if(v.isChar()) {
        field.setChar(owner, v.toChar());
      } else if(v.isByte()) {
        field.setByte(owner, v.toByte());
      } else if(v.isShort()) {
        field.setShort(owner, v.toShort());
      } else {
        throw new ScriptException("unsupported primitive type, " + v.type());
      }
    } else {
      field.set(owner, v.val());
    }
  }

  public void checkAccessAndSet(Field f, Object target, Value v) {
    checkAccess(f, FieldAccess.SET, () -> {
      try {
        setField(f, target, v);
        return null;
      } catch(IllegalArgumentException | IllegalAccessException e) {
        throw new ScriptException("failed to set field '%s'".formatted(f.getName()), e);
      }
    });
  }

  public Object checkAccessAndGet(Field f, Object target) {
    return checkAccess(f, FieldAccess.GET, () -> {
      try {
        return f.get(target);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        throw new ScriptException("failed to access field '%s'".formatted(f.getName()), e);
      }
    });
  }

  private ConstructorRule match(Constructor<?> c) {
    if(constructorRules == null) {
      return null;
    }
    return constructorRules.stream()
        .filter(rule -> rule.matches(c))
        .findFirst()
        .orElse(null);
  }

  public Object checkAndInvoke(Constructor<?> c, Object[] parameters) {
    ConstructorRule rule = match(c);
    RuleAction action = rule!=null?rule.getAction():defaultConstructorAction;
    if(RuleAction.ACCEPT.equals(action)) {
      try {
        return c.newInstance(parameters);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        throw new ScriptException("constructor '%s' invocation failed".formatted(c), e);
      }
    } else if(RuleAction.DENY.equals(action)) {
      throw new AccessDeniedException("constructor '%s', access denied".formatted(c));
    } else if(RuleAction.DROP.equals(action)) {
      throw new ScriptException("firewall action drop not supported on constructor".formatted(action));
    } else if(RuleAction.INTERCEPT.equals(action)) {
      // TODO
      throw new ScriptException("firewall action '%s' not implemeneted".formatted(action));
    } else {
      throw new ScriptException("firewall action '%s' not supported".formatted(action));
    }
  }

  private MethodRule match(Method m) {
    if(methodRules == null) {
      return null;
    }
    return methodRules.stream()
        .filter(rule -> rule.matches(m))
        .findFirst()
        .orElse(null);
  }

  public Object checkAndInvoke(Method m, Object target, Object[] parameters) {
    MethodRule rule = match(m);
    RuleAction action = rule!=null?rule.getAction():defaultMethodAction;
    if(RuleAction.ACCEPT.equals(action)) {
      try {
        return m.invoke(target, parameters);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new ScriptException("method '%s' invocation failed".formatted(m), e);
      }
    } else if(RuleAction.DENY.equals(action)) {
      throw new AccessDeniedException("method '%s', access denied".formatted(m));
    } else if(RuleAction.DROP.equals(action)) {
      return null;
    } else if(RuleAction.INTERCEPT.equals(action)) {
      // TODO
      throw new ScriptException("firewall action '%s' not implemeneted".formatted(action));
    } else {
      throw new ScriptException("firewall action '%s' not supported".formatted(action));
    }
  }

  public List<FieldRule> getFieldRules() {
    return fieldRules;
  }

  public Firewall setFieldRules(List<FieldRule> fieldRules) {
    if(fieldRules == null) {
      throw new ScriptException("fieldRules is null");
    }
    this.fieldRules = fieldRules;
    return this;
  }

  public RuleAction getDefaultFieldAction() {
    return defaultFieldAction;
  }

  public Firewall setDefaultFieldAction(RuleAction defaultFieldAction) {
    if(defaultFieldAction == null) {
      throw new ScriptException("defaultFieldAction is null");
    }
    this.defaultFieldAction = defaultFieldAction;
    return this;
  }

  public RuleAction getDefaultConstructorAction() {
    return defaultConstructorAction;
  }

  public Firewall setDefaultConstructorAction(RuleAction defaultConstructorAction) {
    if(defaultConstructorAction == null) {
      throw new ScriptException("defaultConstructorAction is null");
    }
    this.defaultConstructorAction = defaultConstructorAction;
    return this;
  }

  public List<ConstructorRule> getConstructorRules() {
    return constructorRules;
  }

  public Firewall setConstructorRules(List<ConstructorRule> constructorRules) {
    this.constructorRules = constructorRules;
    return this;
  }

  public List<MethodRule> getMethodRules() {
    return methodRules;
  }

  public Firewall setMethodRules(List<MethodRule> methodRules) {
    this.methodRules = methodRules;
    return this;
  }

  public RuleAction getDefaultMethodAction() {
    return defaultMethodAction;
  }

  public Firewall setDefaultMethodAction(RuleAction defaultMethodAction) {
    this.defaultMethodAction = defaultMethodAction;
    return this;
  }

  public Firewall addFieldRule(Set<FieldAccess> fieldAccess, String packageName, String className, String fieldName,
      RuleAction action) {
    this.fieldRules.add(new SimpleFieldRule(fieldAccess, packageName, className, fieldName, action));
    return this;
  }

  public Firewall addConstructorRule(String packageName, String className, RuleAction action) {
    this.constructorRules.add(new SimpleConstructorRule(packageName, className, action));
    return this;
  }

  public Firewall addMethodRule(String packageName, String className, String name, RuleAction action) {
    this.methodRules.add(new SimpleMethodRule(packageName, className, name, action));
    return this;
  }

}