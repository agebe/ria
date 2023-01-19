package org.ria.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.ria.ScriptException;

public class ResourceUtil {

  public static String resourceAsString(String name) {
    try {
      return IOUtils.toString(ResourceUtil.class.getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new ScriptException("failed to load resource '%s'".formatted(name), e);
    }
  }

}
