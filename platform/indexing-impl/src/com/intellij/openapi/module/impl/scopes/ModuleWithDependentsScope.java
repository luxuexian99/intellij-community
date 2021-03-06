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
package com.intellij.openapi.module.impl.scopes;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.containers.Queue;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author max
 */
class ModuleWithDependentsScope extends GlobalSearchScope {
  private final Module myModule;

  private final ProjectFileIndex myProjectFileIndex;
  private final Set<Module> myModules;
  private final GlobalSearchScope myProjectScope;

  ModuleWithDependentsScope(@NotNull Module module) {
    super(module.getProject());
    myModule = module;

    myProjectFileIndex = ProjectRootManager.getInstance(module.getProject()).getFileIndex();
    myProjectScope = ProjectScope.getProjectScope(module.getProject());

    myModules = new THashSet<Module>();
    myModules.add(module);

    fillModules();
  }

  private void fillModules() {
    Queue<Module> walkingQueue = new Queue<Module>(10);
    walkingQueue.addLast(myModule);

    Module[] allModules = ModuleManager.getInstance(myModule.getProject()).getModules();
    Set<Module> processed = new THashSet<Module>();

    while (!walkingQueue.isEmpty()) {
      Module current = walkingQueue.pullFirst();
      processed.add(current);
      for (Module dependent : allModules) {
        for (OrderEntry orderEntry : ModuleRootManager.getInstance(dependent).getOrderEntries()) {
          if (orderEntry instanceof ModuleOrderEntry && current.equals(((ModuleOrderEntry)orderEntry).getModule())) {
            myModules.add(dependent);
            if (!processed.contains(dependent) && ((ModuleOrderEntry)orderEntry).isExported()) {
              walkingQueue.addLast(dependent);
            }
          }
        }
      }
    }
  }

  @Override
  public boolean contains(@NotNull VirtualFile file) {
    return contains(file, false);
  }

  boolean contains(@NotNull VirtualFile file, boolean myOnlyTests) {
    Module moduleOfFile = myProjectFileIndex.getModuleForFile(file);
    if (moduleOfFile == null || !myModules.contains(moduleOfFile)) return false;
    if (myOnlyTests && !myProjectFileIndex.isInTestSourceContent(file)) return false;
    return myProjectScope.contains(file);
  }

  @Override
  public int compare(@NotNull VirtualFile file1, @NotNull VirtualFile file2) {
    return 0;
  }

  @Override
  public boolean isSearchInModuleContent(@NotNull Module aModule) {
    return myModules.contains(aModule);
  }

  @Override
  public boolean isSearchInLibraries() {
    return false;
  }

  @NonNls
  public String toString() {
    return "Module with dependents:" + myModule.getName();
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ModuleWithDependentsScope)) return false;

    final ModuleWithDependentsScope moduleWithDependentsScope = (ModuleWithDependentsScope)o;

    return myModule.equals(moduleWithDependentsScope.myModule);
  }

  public int hashCode() {
    return myModule.hashCode();
  }
}
