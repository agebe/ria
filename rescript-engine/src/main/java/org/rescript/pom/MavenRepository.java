package org.rescript.pom;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class MavenRepository {

  private String repositoryUrl;

  public MavenRepository(String repositoryUrl) {
    super();
    this.repositoryUrl = repositoryUrl;
  }

  public String getFile(MavenCoordinates coord, String suffix) throws Exception {
    return new String(getFileBytes(coord, suffix).getRight(), StandardCharsets.UTF_8);
  }

  public Pair<File, byte[]> getFileBytes(MavenCoordinates coord, String suffix) throws Exception {
    Pair<File, byte[]> p = fromCache(coord, suffix);
    if(p != null) {
      return p;
    }
    byte[] buf = fromRemote(coord, suffix);
    File f = cache(coord, suffix);
    f.getParentFile();
    if(!f.getParentFile().exists()) {
      f.getParentFile().mkdirs();
    }
    FileUtils.writeByteArrayToFile(f, buf);
    return Pair.of(f, buf);
  }

  public File fetchFile(MavenCoordinates coord, String suffix) throws Exception {
    return getFileBytes(coord, suffix).getLeft();
  }

  private Pair<File, byte[]> fromCache(MavenCoordinates coord, String suffix) throws IOException {
    File f = cache(coord, suffix);
    return f.exists()?Pair.of(f, FileUtils.readFileToByteArray(f)):null;
  }

  private byte[] fromRemote(MavenCoordinates coord, String suffix) throws Exception {
    String url = url(coord, suffix);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI(url))
        .GET()
        .build();
    HttpClient client = HttpClient.newHttpClient();
    System.out.println("get " + url);
    HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
    return response.body();
  }

  private File cache(MavenCoordinates coord, String suffix) {
    File userHome = new File(System.getProperty("user.home"));
    // FIXME version needs to be read from the manifest
    File cacheBase = new File(userHome, ".bs/0.1.0-SNAPSHOT/cache");
    File group = new File(cacheBase, coord.group());
    File artifact = new File(group, coord.artifact());
    File version = new File(artifact, coord.version());
    return new File(version, coord.artifact() + "-" + coord.version() + suffix);
  }

  private String url(MavenCoordinates coord, String suffix) {
    return this.repositoryUrl +
        StringUtils.replaceChars(coord.group(), '.', '/') +
        "/" +
        coord.artifact() +
        "/" +
        coord.version() +
        "/" +
        coord.artifact() + "-" + coord.version() +
        suffix;
  }

}
