/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.intellij.javaee.web;

import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Can be implemented by {@link com.intellij.util.xml.Converter} instance
 *
 * @deprecated use {@link com.intellij.util.xml.CustomReferenceConverter} instead
 * @author Dmitry Avdeev
 */
public interface PsiReferenceConverter {

  /**
   *
   * @param psiElement underlying element for created references ({@link com.intellij.psi.PsiReference#getElement()})
   * @param soft true if created references should be soft ({@link com.intellij.psi.PsiReference#isSoft()})
   * @return empty array if the converter cannot get any references
   */
  @NotNull
  PsiReference[] createReferences(@NotNull PsiElement psiElement, final boolean soft);
}
