/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package org.jetbrains.java.decompiler;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class IdeaDecompilerTest extends LightCodeInsightFixtureTestCase {
  public void testSimple() {
    String path = getRtJarPath() + "!/java/lang/String.class";
    VirtualFile file = StandardFileSystems.jar().findFileByPath(path);
    assertNotNull(path, file);

    CharSequence text = new IdeaDecompiler().getText(file);
    assertNotNull(text);

    String decompiled = text.toString();
    assertTrue(decompiled, decompiled.contains("public final class String"));
    assertTrue(decompiled, decompiled.contains("@deprecated"));
    assertTrue(decompiled, decompiled.contains("private static class CaseInsensitiveComparator"));
    assertFalse(decompiled, decompiled.contains("{ /* compiled code */ }"));
    assertFalse(decompiled, decompiled.contains("synthetic"));
  }

  public void testEnum() { doTestDecompiler(); }
  public void testDeprecations() { doTestDecompiler(); }
  public void testExtendsList() { doTestDecompiler(); }
  public void testParameters() { doTestDecompiler(); }
  public void testConstants() { doTestDecompiler(); }

  private void doTestDecompiler() {
    String name = PluginPathManager.getPluginHomePath("java-decompiler") + "/testData/" + getName().substring(4);
    String path = name + ".class";
    VirtualFile file = StandardFileSystems.local().findFileByPath(path);
    assertNotNull(path, file);
    file.getParent().getChildren();
    file.getParent().refresh(false, true);

    try {
      CharSequence text = new IdeaDecompiler().getText(file);
      assertNotNull(text);
      String expected = FileUtil.loadFile(new File(name + ".txt"));
      assertEquals(expected, text.toString());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void testStubCompatibilityRt() {
    String path = getRtJarPath() + "!/";
    VirtualFile dir = StandardFileSystems.jar().findFileByPath(path);
    assertNotNull(path, dir);
    doTestStubCompatibility(dir);
  }

  public void testStubCompatibilityIdea() {
    String path = PathManager.getHomePath() + "/out/classes/production";
    VirtualFile dir = StandardFileSystems.local().refreshAndFindFileByPath(path);
    assertNotNull(path, dir);
    doTestStubCompatibility(dir);
  }

  private void doTestStubCompatibility(VirtualFile root) {
    VfsUtilCore.visitChildrenRecursively(root, new VirtualFileVisitor() {
      @Override
      public boolean visitFile(@NotNull VirtualFile file) {
        if (!file.isDirectory() && file.getFileType() == StdFileTypes.CLASS && !file.getName().contains("$")) {
          PsiFile clsFile = getPsiManager().findFile(file);
          assertNotNull(file.getPath(), clsFile);
          String decompiled = ((ClsFileImpl)clsFile).getMirror().getText();
          assertTrue(file.getPath(), decompiled.contains(file.getNameWithoutExtension()));
        }
        return true;
      }
    });
  }

  private static String getRtJarPath() {
    String home = System.getProperty("java.home");
    return SystemInfo.isAppleJvm ? FileUtil.toCanonicalPath(home + "/../Classes/classes.jar") : home + "/lib/rt.jar";
  }
}
