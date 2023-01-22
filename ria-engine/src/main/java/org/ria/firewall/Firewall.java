package org.ria.firewall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.ria.ScriptException;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Firewall {

  private static final Logger log = LoggerFactory.getLogger(Firewall.class);

  private List<FieldRule> fieldRules = new ArrayList<>();

  private RuleAction defaultFieldAction = RuleAction.ACCEPT;

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
      } catch(IllegalArgumentException | IllegalAccessException e) {
        throw new ScriptException("failed to set field '%s'".formatted(f.getName()), e);
      }
      return null;
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

}
