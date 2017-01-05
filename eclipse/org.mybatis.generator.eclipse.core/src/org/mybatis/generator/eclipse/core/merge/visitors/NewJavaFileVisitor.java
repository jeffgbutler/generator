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

import static org.mybatis.generator.eclipse.core.merge.visitors.VisitorUtils.isPublic;
import static org.mybatis.generator.eclipse.core.merge.visitors.VisitorUtils.isTopLevel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * This visitor gathers new nodes that will be written into an existing Java file.
 * 
 * For the public item, the visitor will descend and capture methods, fields, enum declarations,
 * inner classes/enums/interfaces.
 * 
 * @author Jeff Butler
 *
 */
public class NewJavaFileVisitor extends ASTVisitor {
    private List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
    
    // following are items contained in the public top level type
    private CompilationUnitType compilationUnitType;
    private Type superclass;
    private List<Type> superInterfaceTypes = new ArrayList<Type>();
    private List<EnumConstantDeclaration> enumConstants = new ArrayList<EnumConstantDeclaration>();
    private List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
    private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
    private List<AnnotationTypeDeclaration> annotations = new ArrayList<AnnotationTypeDeclaration>();
    private List<EnumDeclaration> enums = new ArrayList<EnumDeclaration>();
    private List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();

    // following are other top level types
    private List<AnnotationTypeDeclaration> topLevelAnnotations = new ArrayList<AnnotationTypeDeclaration>();
    private List<EnumDeclaration> topLevelEnums = new ArrayList<EnumDeclaration>();
    private List<TypeDeclaration> topLevelTypes = new ArrayList<TypeDeclaration>();

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
        fields.add(node);
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        methods.add(node);
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
                topLevelEnums.add(node);
                return false;
            }
        } else {
            enums.add(node);
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
                topLevelTypes.add(node);
                return false;
            }
        } else {
            types.add(node);
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
                topLevelAnnotations.add(node);
                return false;
            }
        } else {
            annotations.add(node);
            return false;
        }
    }
    
    @Override
    public boolean visit(EnumConstantDeclaration node) {
        enumConstants.add(node);
        return false;
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

    public List<EnumConstantDeclaration> enumConstants() {
        return enumConstants;
    }

    public boolean hasEnumConstants() {
        return !enumConstants.isEmpty();
    }
}
