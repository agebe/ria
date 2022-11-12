package org.rescript.expression;

import java.lang.reflect.Array;
import java.util.List;

import org.rescript.ScriptException;
import org.rescript.value.ArrayValue;
import org.rescript.value.BooleanArrayValue;
import org.rescript.value.CharArrayValue;
import org.rescript.value.DoubleArrayValue;
import org.rescript.value.FloatArrayValue;
import org.rescript.value.IntArrayValue;
import org.rescript.value.LongArrayValue;
import org.rescript.value.Value;

public class ArrayUtil {

  public static Value newArray(Class<?> cls, List<Value> vals) {
    if(cls.equals(double.class)) {
      double[] arr = new double[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toDouble();
      }
      return new DoubleArrayValue(arr);
    } else if(cls.equals(float.class)) {
      float[] arr = new float[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toFloat();
      }
      return new FloatArrayValue(arr);
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
      char[] arr = new char[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toChar();
      }
      return new CharArrayValue(arr);
    } else if(cls.equals(boolean.class)) {
      boolean[] arr = new boolean[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toBoolean();
      }
      return new BooleanArrayValue(arr);
    } else {
      Object[] arr = (Object[])Array.newInstance(cls, vals.size());
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).val();
      }
      return new ArrayValue(arr, arr.getClass());
    }
  }

  public static Value newArray(Class<?> cls, int size) {
    if(cls.equals(double.class)) {
      double[] arr = new double[size];
      return new DoubleArrayValue(arr);
    } else if(cls.equals(float.class)) {
      float[] arr = new float[size];
      return new FloatArrayValue(arr);
    } else if(cls.equals(int.class)) {
      int[] arr = new int[size];
      return new IntArrayValue(arr);
    } else if(cls.equals(long.class)) {
      long[] arr = new long[size];
      return new LongArrayValue(arr);
    } else if(cls.equals(byte.class)) {
      throw new ScriptException("byte array literal not implemented yet");
    } else if(cls.equals(short.class)) {
      throw new ScriptException("short array literal not implemented yet");
    } else if(cls.equals(char.class)) {
      char[] arr = new char[size];
      return new CharArrayValue(arr);
    } else if(cls.equals(boolean.class)) {
      boolean[] arr = new boolean[size];
      return new BooleanArrayValue(arr);
    } else {
      Object[] arr = (Object[])Array.newInstance(cls, size);
      return new ArrayValue(arr, arr.getClass());
    }
  }

}
