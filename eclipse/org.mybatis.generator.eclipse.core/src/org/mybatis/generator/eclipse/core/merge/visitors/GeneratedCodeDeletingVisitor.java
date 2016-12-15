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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IDocElement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * This visitor deletes generated code from an existing Java file.
 * 
 * The visitor will descend into the public top level class/interface/enum in the file.  Within
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
    private String superClass;

    public GeneratedCodeDeletingVisitor(String[] javadocTags) {
        this.javadocTags.addAll(Arrays.asList(javadocTags));
    }

    /**
     * Find the generated fields and delete them
     */
    @Override
    public boolean visit(FieldDeclaration node) {
        if (nodeShouldBeDeleted(node)) {
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
        if (nodeShouldBeDeleted(node)) {
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
        if (nodeShouldBeDeleted(node)) {
            node.delete();
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
            collectSuperClass(node.getSuperclassType());
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
            if (nodeShouldBeDeleted(node)) {
                node.delete();
            }

            return false;
        }
    }
    
    private boolean isPublic(BodyDeclaration node) {
        return Modifier.isPublic(node.getModifiers());
    }

    private boolean isTopLevel(ASTNode node) {
        ASTNode parent = node.getParent();
        return parent != null && parent.getNodeType() == ASTNode.COMPILATION_UNIT;
    }
    
    private void collectSuperInterfaces(List<Type> superinterfaceTypes) {
        for (Type type : superinterfaceTypes) {
            this.superInterfaces.add(stringify(type));
        }
    }
    
    private void collectSuperClass(Type superclassType) {
        if (superclassType != null) {
            superClass = stringify(superclassType);
        }
    }
    
    private String stringify(Type type) {
        TypeStringifier ts = new TypeStringifier();
        type.accept(ts);
        return ts.toString();
    }
    
    private boolean nodeShouldBeDeleted(BodyDeclaration node) {
        Javadoc javadoc = node.getJavadoc();
        if (javadoc != null && javadocContainsGeneratedTag(javadoc)) {
            return true;
        }

        return false;
    }

    private boolean javadocContainsGeneratedTag(Javadoc javadoc) {
        @SuppressWarnings("unchecked")
        List<TagElement> tagElements = javadoc.tags();
        for (TagElement tagElement : tagElements) {
            if (tagElementContainsGeneratedTag(tagElement)) {
                return true;
            }
        }

        return false;
    }

    private boolean tagElementContainsGeneratedTag(TagElement tagElement) {
        String tagName = tagElement.getTagName();
        if (tagName != null && javadocTags.contains(tagName)) {
            if (!generatedNodeShouldBeKept(tagElement)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean generatedNodeShouldBeKept(TagElement tagElement) {
        @SuppressWarnings("unchecked")
        List<IDocElement> fragments = tagElement.fragments();
        for (IDocElement fragment : fragments) {
            if (fragmentIsDoNotDelete(fragment)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean fragmentIsDoNotDelete(IDocElement fragment) {
        if (fragment instanceof TextElement) {
            TextElement textElement = (TextElement) fragment;
            if ("do_not_delete_during_merge".equals(textElement.getText())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<Annotation> retrieveAnnotations(BodyDeclaration node) {
        List<IExtendedModifier> modifiers = node.modifiers();
        List<Annotation> annotations = new ArrayList<Annotation>();
        for (IExtendedModifier modifier : modifiers) {
            if (modifier.isAnnotation()) {
                annotations.add((Annotation) modifier);
            }
        }
        return annotations;
    }
}
