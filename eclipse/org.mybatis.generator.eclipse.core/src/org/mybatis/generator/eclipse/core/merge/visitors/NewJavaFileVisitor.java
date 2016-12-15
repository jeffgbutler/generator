/**
 *  Copyright 2006-2016 Copyright 2006-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mybatis.generator.eclipse.core.merge.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * This visitor gathers new nodes that will be written into an existing Java file.
 * 
 * FOr the public item, the visitor will descend and capture methods, fields, enum declarations,
 * inner classes/enums/interfaces.
 * 
 * @author Jeff Butler
 *
 */
public class NewJavaFileVisitor extends ASTVisitor {
    private CompilationUnitType compilationUnitType;
    private List<BodyDeclaration> newNodes = new ArrayList<BodyDeclaration>();
    private List<BodyDeclaration> newTopLevelNodes = new ArrayList<BodyDeclaration>();
    private List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
    private Type superclass;
    private List<Type> superInterfaceTypes = new ArrayList<Type>();

    /**
     * 
     */
    public NewJavaFileVisitor() {
        super();
    }
    
    @Override
    public boolean visit(ImportDeclaration node) {
        imports.add(node);
        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        newNodes.add(node);

        return false;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        newNodes.add(node);

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(EnumDeclaration node) {
        if (isTopLevel(node)) {
            if (isPublic(node)) {
                superInterfaceTypes.addAll(node.superInterfaceTypes());
                compilationUnitType = CompilationUnitType.ENUM;
                return true;
            } else {
                newTopLevelNodes.add(node);
                return false;
            }
        } else {
            newNodes.add(node);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(TypeDeclaration node) {
        if (isTopLevel(node)) {
            if (isPublic(node)) {
                superInterfaceTypes.addAll(node.superInterfaceTypes());
                superclass = node.getSuperclassType();
                compilationUnitType = node.isInterface() ? CompilationUnitType.INTERFACE : CompilationUnitType.CLASS;
                return true;
            } else {
                newTopLevelNodes.add(node);
                return false;
            }
        } else {
            newNodes.add(node);
            return false;
        }
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        if (isTopLevel(node)) {
            if (isPublic(node)) {
                compilationUnitType = CompilationUnitType.ANNOTATION;
                return true;
            } else {
                newTopLevelNodes.add(node);
                return false;
            }
        } else {
            newNodes.add(node);
            return false;
        }
    }
    
    @Override
    public boolean visit(EnumConstantDeclaration node) {
        newNodes.add(node);
        return false;
    }
    
    public List<BodyDeclaration> getNewNodes() {
        return newNodes;
    }

    public List<ImportDeclaration> getImports() {
        return imports;
    }

    public Type getSuperclass() {
        return superclass;
    }

    public List<Type> getSuperInterfaceTypes() {
        return superInterfaceTypes;
    }

    public CompilationUnitType getCompilationUnitType() {
        return compilationUnitType;
    }
    
    private boolean isPublic(BodyDeclaration node) {
        return Modifier.isPublic(node.getModifiers());
    }

    private boolean isTopLevel(ASTNode node) {
        ASTNode parent = node.getParent();
        return parent != null && parent.getNodeType() == ASTNode.COMPILATION_UNIT;
    }
}
