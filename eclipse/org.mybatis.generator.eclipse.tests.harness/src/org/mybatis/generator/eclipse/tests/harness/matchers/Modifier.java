/**
 *    Copyright 2006-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.eclipse.tests.harness.matchers;

public enum Modifier {
    ABSTRACT(org.eclipse.jdt.core.dom.Modifier.ABSTRACT),
    DEFAULT(org.eclipse.jdt.core.dom.Modifier.DEFAULT),
    FINAL(org.eclipse.jdt.core.dom.Modifier.FINAL),
    NATIVE(org.eclipse.jdt.core.dom.Modifier.NATIVE),
    NONE(org.eclipse.jdt.core.dom.Modifier.NONE),
    PRIVATE(org.eclipse.jdt.core.dom.Modifier.PRIVATE),
    PROTECTED(org.eclipse.jdt.core.dom.Modifier.PROTECTED),
    PUBLIC(org.eclipse.jdt.core.dom.Modifier.PUBLIC),
    STATIC(org.eclipse.jdt.core.dom.Modifier.STATIC),
    STRICTFP(org.eclipse.jdt.core.dom.Modifier.STRICTFP),
    SYNCHRONIZED(org.eclipse.jdt.core.dom.Modifier.SYNCHRONIZED),
    TRANSIENT(org.eclipse.jdt.core.dom.Modifier.TRANSIENT),
    VOLATILE(org.eclipse.jdt.core.dom.Modifier.VOLATILE);
    
    private int mask;
    
    private Modifier(int mask) {
        this.mask = mask;
    }
    
    public boolean matches(int flags) {
        if (this == NONE) {
            return flags == 0;
        }
        return (flags & mask) != 0;
    }
}
