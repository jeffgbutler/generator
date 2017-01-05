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
import static org.mybatis.generator.eclipse.core.merge.visitors.VisitorUtils.nodeShouldBeDeleted;
import static org.mybatis.generator.eclipse.core.merge.visitors.VisitorUtils.retrieveAnnotations;
import static org.mybatis.generator.eclipse.core.merge.visitors.VisitorUtils.stringify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * This visitor deletes generated code from an existing Java file.
 * 
 * The visitor will descend into the public top level annotation/class/interface/enum in the file.  Within
 * that element, the visitor will delete any first level generated element.
 * 
 *  The visitor will also delete any non-public top level generated element.
 * 
 * @author Jeff Butler
 *
 */
public class GeneratedCodeDeletingVisitor extends ASTVisitor {
    private CompilationUnitType compilationUnitType;
    private Set<String> javadocTags = new HashSet<String>();
    private Map<String, List<Annotation>> fieldAnnotations = new HashMap<String, List<Annotation>>();
    private Map<String, List<Annotation>> methodAnnotations = new HashMap<String, List<Annotation>>();
    private AbstractTypeDeclaration typeDeclaration;
    private List<String> imports = new ArrayList<String>();
    private List<String> superInterfaces = new ArrayList<String>();
    private List<String> savedEnumConstants = new ArrayList<String>();
    private CompilationUnit compilationUnit;

    public GeneratedCodeDeletingVisitor(String[] javadocTags) {
        this.javadocTags.addAll(Arrays.asList(javadocTags));
    }
    
    @Override
    public boolean visit(CompilationUnit node) {
        compilationUnit = node;
        return true;
    }

    /**
     * Find the generated fields and delete them
     */
    @Override
    public boolean visit(FieldDeclaration node) {
        if (nodeShouldBeDeleted(node, javadocTags)) {
            List<Annotation> annotations = retrieveAnnotations(node);
            if (!annotations.isEmpty()) {
                VariableDeclarationFragment variable = (VariableDeclarationFragment) node
                        .fragments().get(0);
                fieldAnnotations.put(variable.getName().getIdentifier(),
                        annotations);
            }
            node.delete();
        }

        return false;
    }

    /**
     * Find the generated methods and delete them
     */
    @Override
    public boolean visit(MethodDeclaration node) {
        if (nodeShouldBeDeleted(node, javadocTags)) {
            List<Annotation> annotations = retrieveAnnotations(node);
            if (!annotations.isEmpty()) {
                TypeStringifier mss = new MethodSignatureStringifier();
                node.accept(mss);
                String methodSignature = mss.toString();
                methodAnnotations.put(methodSignature, annotations);
            }
            node.delete();
        }

        return false;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        if (nodeShouldBeDeleted(node, javadocTags)) {
            node.delete();
        } else {
            savedEnumConstants.add(node.getName().getIdentifier());
        }
        return false;
    }
    
    @Override
    public boolean visit(ImportDeclaration node) {
        ImportDeclarationStringifier ids = new ImportDeclarationStringifier();
        node.accept(ids);
        imports.add(ids.toString());
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(TypeDeclaration node) {
        if (deleteOrDescend(node)) {
            compilationUnitType = node.isInterface() ? CompilationUnitType.INTERFACE : CompilationUnitType.CLASS;
            collectSuperInterfaces(node.superInterfaceTypes());
            return true;
        } else {
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(EnumDeclaration node) {
        if (deleteOrDescend(node)) {
            compilationUnitType = CompilationUnitType.ENUM;
            collectSuperInterfaces(node.superInterfaceTypes());
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        if (deleteOrDescend(node)) {
            compilationUnitType = CompilationUnitType.ANNOTATION;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * If this is a public top level type, then descend into it.
     * 
     * Otherwise, if the node is generated then delete it.
     */
    private boolean deleteOrDescend(AbstractTypeDeclaration node) {
        if (isTopLevel(node) && isPublic(node)) {
            typeDeclaration = node;
            // descend
            return true;
        } else {
            // is this a generated inner or non-public top level type? If so, then delete
            if (nodeShouldBeDeleted(node, javadocTags)) {
                node.delete();
            }

            return false;
        }
    }
    
    private void collectSuperInterfaces(List<Type> superinterfaceTypes) {
        for (Type type : superinterfaceTypes) {
            this.superInterfaces.add(stringify(type));
        }
    }
    
    public AbstractTypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    public CompilationUnitType getCompilationUnitType() {
        return compilationUnitType;
    }
    
    public boolean hasImport(ImportDeclaration importDeclaration) {
        ImportDeclarationStringifier stringifier = new ImportDeclarationStringifier();
        importDeclaration.accept(stringifier);
        return imports.contains(stringifier.toString());
    }

    public boolean hasSuperInterface(Type type) {
        return superInterfaces.contains(stringify(type));
    }
    
    @SuppressWarnings("unchecked")
    public void addImport(ImportDeclaration importDeclaration) {
        compilationUnit.imports().add(importDeclaration);
    }
    
    /**
     * Important: this type must have been created as attached the the AST for the compilation unit
     * 
     * @param type
     */
    @SuppressWarnings("unchecked")
    public void addSuperInterface(Type type) {
       if (compilationUnitType == CompilationUnitType.ENUM) {
           ((EnumDeclaration) typeDeclaration).superInterfaceTypes().add(type);
       } else if (compilationUnitType == CompilationUnitType.CLASS || compilationUnitType == CompilationUnitType.INTERFACE) {
           ((TypeDeclaration) typeDeclaration).superInterfaceTypes().add(type);
       }
    }

    /**
     * Important: this type must have been created as attached the the AST for the compilation unit
     * 
     * @param type
     */
    public void setSuperClass(Type type) {
        if (compilationUnitType == CompilationUnitType.CLASS) {
            ((TypeDeclaration) typeDeclaration).setSuperclassType(type);
        }
    }
    
    public void addFirstLevelNodes(List newNodes) {
        typeDeclaration.bodyDeclarations().addAll(newNodes);
    }

    public void addTopLevelNodes(List newTopLevelNodes) {
        compilationUnit.types().addAll(newTopLevelNodes);
    }

    @SuppressWarnings("unchecked")
    public void addNewEnumConstants(List<EnumConstantDeclaration> newEnumConnstants) {
        if (compilationUnitType != CompilationUnitType.ENUM) {
            return;
        }
        
        EnumDeclaration enumDeclaration = (EnumDeclaration) typeDeclaration;
        
        for (EnumConstantDeclaration enumConstant : newEnumConnstants) {
            if (!savedEnumConstants.contains(enumConstant.getName().getIdentifier())) {
                enumDeclaration.enumConstants().add(enumConstant);
            }
        }
    }
}
