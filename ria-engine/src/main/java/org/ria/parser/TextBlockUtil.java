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
package org.ria.parser;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.startsWith;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;

// https://docs.oracle.com/en/java/javase/15/text-blocks/index.html
public class TextBlockUtil {

  private static final String DOUBLE_QUOTE_TEXT_BLOCK_START = "\"\"\"\n";
  private static final String DOUBLE_QUOTE_TEXT_BLOCK_END = "\"\"\"";
  private static final String SINGLE_QUOTE_TEXT_BLOCK_START = "'''\n";
  private static final String SINGLE_QUOTE_TEXT_BLOCK_END = "'''";

  public static boolean isTextBlock(String s) {
    return
        (startsWith(s, DOUBLE_QUOTE_TEXT_BLOCK_START) && endsWith(s, DOUBLE_QUOTE_TEXT_BLOCK_END)) ||
        (startsWith(s, SINGLE_QUOTE_TEXT_BLOCK_START) && endsWith(s, SINGLE_QUOTE_TEXT_BLOCK_END));
  }

  private static String removeStartEndMarker(String s) {
    if(startsWith(s, DOUBLE_QUOTE_TEXT_BLOCK_START) && endsWith(s, DOUBLE_QUOTE_TEXT_BLOCK_END)) {
      return StringUtils.removeStart(
          StringUtils.removeEnd(s, DOUBLE_QUOTE_TEXT_BLOCK_END), DOUBLE_QUOTE_TEXT_BLOCK_START);
    } else if(startsWith(s, SINGLE_QUOTE_TEXT_BLOCK_START) && endsWith(s, SINGLE_QUOTE_TEXT_BLOCK_END)) {
      return StringUtils.removeStart(
          StringUtils.removeEnd(s, SINGLE_QUOTE_TEXT_BLOCK_END), SINGLE_QUOTE_TEXT_BLOCK_START);
    } else {
      throw new ScriptException("not a text block: " + s);
    }
  }

  private static int leadingWhiteSpace(String s) {
    for(int i=0;i<s.length();i++) {
      char c = s.charAt(i);
      if(!Character.isWhitespace(c)) {
        return i;
      }
    }
    return s.length();
  }

  // String.split("\\R"), StringUtils.split(...) and String.lines() all
  // swallow the last line if it is empty (trailing newline) but
  // it is required to calculate incidental white space and
  // for putting the string back together (with the trailing newline)
  private static Stream<String> splitPreserveAllLines(String org) {
    if(StringUtils.endsWith(org, "\n")) {
      return Stream.concat(org.lines(), Stream.of(""));
    } else {
      return org.lines();
    }
  }

  private static int lastNonSpaceChar(String s) {
    for(int i=s.length();i>0;i--) {
      int cp = s.codePointAt(i-1);
      if(!(Character.isSpaceChar(cp) || Character.isWhitespace(cp))) {
        return i;
      }
    }
    return 0;
  }

  private static String stripSpaceEnd(String s) {
    return s!=null?StringUtils.substring(s, 0, lastNonSpaceChar(s)):s;
  }

  private static String removeIncidentalWhiteSpace(String org, int len) {
    return splitPreserveAllLines(org)
        .map(s -> StringUtils.substring(s, len))
        .map(s -> stripSpaceEnd(s))
        .collect(Collectors.joining("\n"));
  }

  private static String removeIncidentalWhiteSpace(String org) {
    int minLeadingWhiteSpace = splitPreserveAllLines(org)
        .mapToInt(TextBlockUtil::leadingWhiteSpace)
        .min()
        .orElse(0);
    return removeIncidentalWhiteSpace(org, minLeadingWhiteSpace);
  }

  public static String toString(String org) {
    return removeIncidentalWhiteSpace(removeStartEndMarker(org));
  }

}
