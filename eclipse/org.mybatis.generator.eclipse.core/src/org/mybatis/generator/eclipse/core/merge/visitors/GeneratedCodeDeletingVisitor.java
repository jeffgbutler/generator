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
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IDocElement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * This visitor deletes generated code from an existing Java file.
 * 
 * @author Jeff Butler
 *
 */
public class GeneratedCodeDeletingVisitor extends ASTVisitor {
    private CompilationUnitType compilationUnitType;
    private Set<String> javadocTags = new HashSet<String>();
    private Map<String, List<Annotation>> fieldAnnotations = new HashMap<String, List<Annotation>>();
    private Map<String, List<Annotation>> methodAnnotations = new HashMap<String, List<Annotation>>();
    private TypeDeclaration typeDeclaration;

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

    /**
     * Find any generated inner types and delete them
     */
    @Override
    public boolean visit(TypeDeclaration node) {
        // make sure we only pick up the top level type
        if (node.getParent().getNodeType() == ASTNode.COMPILATION_UNIT) {
            if (node.isInterface()) {
                compilationUnitType = CompilationUnitType.INTERFACE;
            } else {
                compilationUnitType = CompilationUnitType.CLASS;
            }
            
            typeDeclaration = node;
            return true;
        } else {
            // is this a generated inner class? If so, then delete
            if (nodeShouldBeDeleted(node)) {
                node.delete();
            }

            return false;
        }
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        if (nodeShouldBeDeleted(node)) {
            node.delete();
        }

        return false;
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
