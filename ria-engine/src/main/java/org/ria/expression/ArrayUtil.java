/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.expression;

import java.lang.reflect.Array;
import java.util.List;

import org.ria.ScriptException;
import org.ria.value.AbstractArrayValue;
import org.ria.value.ArrayValue;
import org.ria.value.BooleanArrayValue;
import org.ria.value.ByteArrayValue;
import org.ria.value.CharArrayValue;
import org.ria.value.DoubleArrayValue;
import org.ria.value.FloatArrayValue;
import org.ria.value.IntArrayValue;
import org.ria.value.LongArrayValue;
import org.ria.value.ShortArrayValue;
import org.ria.value.Value;

public class ArrayUtil {

  public static AbstractArrayValue newArray(Class<?> cls, List<Value> vals) {
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
      byte[] arr = new byte[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toByte();
      }
      return new ByteArrayValue(arr);
    } else if(cls.equals(short.class)) {
      short[] arr = new short[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toShort();
      }
      return new ShortArrayValue(arr);
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
    } else if(cls.equals(byte.class)) {
      byte[] arr = new byte[vals.size()];
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).toByte();
      }
      return new ByteArrayValue(arr);
    } else {
      Object[] arr = (Object[])Array.newInstance(cls, vals.size());
      for(int i=0;i<vals.size();i++) {
        arr[i] = vals.get(i).val();
      }
      return new ArrayValue(arr, arr.getClass());
    }
  }

  public static AbstractArrayValue newArray(Class<?> cls, int size) {
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
      byte[] arr = new byte[size];
      return new ByteArrayValue(arr);
    } else if(cls.equals(short.class)) {
      short[] arr = new short[size];
      return new ShortArrayValue(arr);
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

  public static AbstractArrayValue toArrayValue(Object array) {
    if(array instanceof double[] a) {
      return new DoubleArrayValue(a);
    } else if(array instanceof float[] a) {
      return new FloatArrayValue(a);
    } else if(array instanceof long[] a) {
      return new LongArrayValue(a);
    } else if(array instanceof int[] a) {
      return new IntArrayValue(a);
    } else if(array instanceof char[] a) {
      return new CharArrayValue(a);
    } else if(array instanceof short[] a) {
      return new ShortArrayValue(a);
    } else if(array instanceof byte[] a) {
      return new ByteArrayValue(a);
    } else if(array instanceof boolean[] a) {
      return new BooleanArrayValue(a);
    } else if(array instanceof Object[] a) {
      return new ArrayValue(a, a.getClass());
    } else {
      throw new ScriptException("unexpected array type '%s'".formatted(array));
    }
  }

}
