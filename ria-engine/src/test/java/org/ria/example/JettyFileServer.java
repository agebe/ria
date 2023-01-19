package org.ria.example;

import org.ria.Script;

public class JettyFileServer {

  public static void main(String[] args) {
    new Script().run(ResourceUtil.resourceAsString("jettyFileServer.ria"));
  }

}
