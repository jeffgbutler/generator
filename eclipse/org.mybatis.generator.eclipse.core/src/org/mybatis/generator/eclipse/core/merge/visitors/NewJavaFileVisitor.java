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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * @author Jeff Butler
 *
 */
public class NewJavaFileVisitor extends ASTVisitor {
    private CompilationUnitType compilationUnitType;
    private List<ASTNode> newNodes = new ArrayList<ASTNode>();
    private List<ImportDeclaration> imports;
    private Type superclass;
    private List<Type> superInterfaceTypes;
    private List<EnumConstantDeclaration> enumConstantDeclarations = new ArrayList<EnumConstantDeclaration>();

    /**
     * 
     */
    public NewJavaFileVisitor() {
        super();
    }

    public boolean visit(FieldDeclaration node) {
        newNodes.add(node);

        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean visit(EnumDeclaration node) {
        // if this is the top level class/interface then we want to descend into
        // it to find new methods and fields.  If not, then this is an inner
        // class/interface that we will merge into the existing file.
        if (node.getParent().getNodeType() == ASTNode.COMPILATION_UNIT) {
            onlyOneTopLevelAllowed();
            compilationUnitType = CompilationUnitType.ENUM;
            
            superInterfaceTypes = node.superInterfaceTypes();
            
            return true;
        } else {
            newNodes.add(node);
            return false;
        }
    }

    public boolean visit(MethodDeclaration node) {
        newNodes.add(node);

        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean visit(TypeDeclaration node) {
        // if this is the top level class/interface then we want to descend into
        // it to find new methods and fields.  If not, then this is an inner
        // class/interface that we will merge into the existing file.
        if (node.getParent().getNodeType() == ASTNode.COMPILATION_UNIT) {
            onlyOneTopLevelAllowed();
            if (node.isInterface()) {
                compilationUnitType = CompilationUnitType.INTERFACE;
            } else {
                compilationUnitType = CompilationUnitType.CLASS;
            }
            
            superclass = node.getSuperclassType();

            superInterfaceTypes = node.superInterfaceTypes();
            
            return true;
        } else {
            newNodes.add(node);
            return false;
        }
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        if (node.getParent().getNodeType() == ASTNode.COMPILATION_UNIT) {
            throw new UnsupportedOperationException("The Java file merger does not support top level Annotation Types");
        } else {
            newNodes.add(node);
            return false;
        }
    }
    
    public List<ASTNode> getNewNodes() {
        return newNodes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean visit(CompilationUnit node) {
        imports = node.imports();

        return true;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        enumConstantDeclarations.add(node);
        return false;
    }
    
    public List<ImportDeclaration> getImports() {
        return imports;
    }

    public Type getSuperclass() {
        onlyInClasses();
        return superclass;
    }

    public List<Type> getSuperInterfaceTypes() {
        return superInterfaceTypes;
    }

    public CompilationUnitType getCompilationUnitType() {
        return compilationUnitType;
    }
    
    public List<EnumConstantDeclaration> getEnumConstantDeclarations() {
        onlyInEnums();
        return enumConstantDeclarations;
    }
    
    private void onlyInClasses() {
        if (compilationUnitType != CompilationUnitType.CLASS) {
            throw new UnsupportedOperationException("Not supported in compilation units of type " + compilationUnitType);
        }
    }
    
    private void onlyInEnums() {
        if (compilationUnitType != CompilationUnitType.ENUM) {
            throw new UnsupportedOperationException("Not supported in compilation units of type " + compilationUnitType);
        }
    }
    
    private void onlyOneTopLevelAllowed() {
        if (compilationUnitType != null) {
            throw new UnsupportedOperationException("The Java merger does not support a complation unit with more than one top level class/interface/enum");
        }
    }
}
