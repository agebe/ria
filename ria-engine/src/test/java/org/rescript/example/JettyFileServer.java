package org.rescript.example;

import org.rescript.Script;

public class JettyFileServer {

  public static void main(String[] args) {
    new Script().run(ResourceUtil.resourceAsString("jettyFileServer.ria"));
  }

}
