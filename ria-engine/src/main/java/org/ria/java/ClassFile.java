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
package org.ria.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.SimpleJavaFileObject;

public class ClassFile extends SimpleJavaFileObject {

  private ByteArrayOutputStream out;

  protected ClassFile(URI uri, Kind kind) {
    super(uri, kind);
  }

  @Override
  public URI toUri() {
    return super.toUri();
  }

  @Override
  public String getName() {
    return super.getName();
  }

  @Override
  public InputStream openInputStream() throws IOException {
    return super.openInputStream();
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    out = new ByteArrayOutputStream();
    return out;
  }

  @Override
  public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
    throw new UnsupportedOperationException();
//    return super.openReader(ignoreEncodingErrors);
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    throw new UnsupportedOperationException();
//    return super.getCharContent(ignoreEncodingErrors);
  }

  @Override
  public Writer openWriter() throws IOException {
    throw new UnsupportedOperationException();
//    return super.openWriter();
  }

  @Override
  public long getLastModified() {
    return super.getLastModified();
  }

  @Override
  public boolean delete() {
    return super.delete();
  }

  @Override
  public Kind getKind() {
    return super.getKind();
  }

  @Override
  public boolean isNameCompatible(String simpleName, Kind kind) {
    return super.isNameCompatible(simpleName, kind);
  }

  @Override
  public NestingKind getNestingKind() {
    return super.getNestingKind();
  }

  @Override
  public Modifier getAccessLevel() {
    return super.getAccessLevel();
  }

  @Override
  public String toString() {
    return super.toString();
  }

  public byte[] getBytes() {
    return out.toByteArray();
  }

}
