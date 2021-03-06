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
package com.jetbrains.python.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.PlatformIcons;
import com.jetbrains.python.sdk.PySdkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author traff
 */
public class PyRemoteLibrariesNode extends PsiDirectoryNode {
  private PyRemoteLibrariesNode(Project project, PsiDirectory value, ViewSettings viewSettings) {
    super(project, value, viewSettings);
  }

  @Override
  protected void updateImpl(PresentationData data) {
    data.setPresentableText("Remote Libraries");
    data.setIcon(PlatformIcons.LIBRARY_ICON);
  }

  @Nullable
  public static PyRemoteLibrariesNode create(@NotNull Project project, @NotNull Sdk sdk, ViewSettings settings) {
    final VirtualFile remoteLibraries = PySdkUtil.findRemoteLibrariesDir(sdk);
    if (remoteLibraries != null) {
      final PsiDirectory remoteLibrariesDirectory = PsiManager.getInstance(project).findDirectory(remoteLibraries);
      return new PyRemoteLibrariesNode(project, remoteLibrariesDirectory, settings);
    }
    return null;
  }
}
