/*
 *  Copyright 2005, 2006, 2008 The Apache Software Foundation
 *  Copyright 2012 The MyBatis.org Team
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

import static org.mybatis.generator.eclipse.core.merge.EclipseDomUtils.getCompilationUnitFromSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.mybatis.generator.eclipse.core.merge.InvalidExistingFileException;
import org.mybatis.generator.exception.ShellException;

/**
 * This class handles the task of merging changes into an existing Java file.
 * 
 * This class makes several assumptions about the structure of the new and
 * existing files, including:
 * 
 * <ul>
 * <li>The imports of both files are fully qualified (no wildcard imports)</li>
 * <li>The super interfaces of both files are NOT fully qualified</li>
 * <li>The super classes of both files are NOT fully qualified</li>
 * </ul>
 * 
 * @author Jeff Butler
 * @author Tomas Neuberg
 */
public class JavaFileMerger2 {

    private String newJavaSource;
    private String existingJavaSource;
    private String[] javaDocTags;

    public JavaFileMerger2(String newJavaSource, String existingJavaSource, String[] javaDocTags) {
        super();
        this.newJavaSource = newJavaSource;
        this.existingJavaSource = existingJavaSource;
        this.javaDocTags = javaDocTags;
    }

    @SuppressWarnings("unchecked")
    public String getMergedSource() throws ShellException, InvalidExistingFileException {
        NewJavaFileVisitor newJavaFileVisitor = visitNewJavaFile();

        IDocument document = new Document(existingJavaSource);

        // delete generated stuff, and collect imports
        GeneratedCodeDeletingVisitor existingFileVisitor = new GeneratedCodeDeletingVisitor(
                javaDocTags);

        CompilationUnit cu = getCompilationUnitFromSource(existingJavaSource);
        AST ast = cu.getAST();
        cu.recordModifications();
        cu.accept(existingFileVisitor);

        if (existingFileVisitor.getCompilationUnitType() != newJavaFileVisitor.getCompilationUnitType()) {
            throw new ShellException("Existing and new Java files must have the same type of public top level element");
        }
        
        // reconcile the imports
        List<ImportDeclaration> newImports = getNewImports(newJavaFileVisitor, existingFileVisitor);
        for (ImportDeclaration newImport : newImports) {
            existingFileVisitor.addImport((ImportDeclaration) ASTNode.copySubtree(ast, newImport));
        }
        
        // reconcile the superinterfaces
        List<Type> newSuperInterfaces = getNewSuperInterfaces(newJavaFileVisitor, existingFileVisitor);
        for (Type newSuperInterface : newSuperInterfaces) {
            existingFileVisitor.addSuperInterface((Type) ASTNode.copySubtree(ast, newSuperInterface));
        }

        // set the superclass (this is only applicable for Classes, ignored otherwise
        if (newJavaFileVisitor.getSuperclass() != null) {
            existingFileVisitor.setSuperClass((Type) ASTNode.copySubtree(ast, newJavaFileVisitor.getSuperclass()));
        } else {
            existingFileVisitor.setSuperClass(null);
        }

        // add enum constants
        if (newJavaFileVisitor.hasEnumConstants()) {
            existingFileVisitor.addNewEnumConstants((List<EnumConstantDeclaration>)
                    ASTNode.copySubtrees(ast, newJavaFileVisitor.enumConstants()));
        }
        
        // add methods and fields
//        existingFileVisitor.addFirstLevelNodes(ASTNode.copySubtrees(ast, newJavaFileVisitor.getNewNodes()));
        
        // add top level nodes
//        existingFileVisitor.addTopLevelNodes(ASTNode.copySubtrees(ast, newJavaFileVisitor.getNewTopLevelNodes()));
        

        TextEdit textEdit = cu.rewrite(document, null);
        try {
            textEdit.apply(document);
        } catch (BadLocationException e) {
            throw new ShellException(
                    "BadLocationException removing prior fields and methods");
        }

        String newSource = document.get();
        return newSource;
    }
    
    private List<Type> getNewSuperInterfaces(NewJavaFileVisitor newJavaFileVisitor, GeneratedCodeDeletingVisitor existingJavaFileVisitor) {
        List<Type> answer = new ArrayList<Type>();

        for (Type newSuperInterface : newJavaFileVisitor.getSuperInterfaceTypes()) {
            if (!existingJavaFileVisitor.hasSuperInterface(newSuperInterface)) {
                answer.add(newSuperInterface);
            }
        }

        return answer;
    }

    private List<ImportDeclaration> getNewImports(NewJavaFileVisitor newJavaFileVisitor, GeneratedCodeDeletingVisitor existingJavaFileVisitor) {
        List<ImportDeclaration> answer = new ArrayList<ImportDeclaration>();

        for (ImportDeclaration importDeclaration : newJavaFileVisitor.getImports()) {
            if (!existingJavaFileVisitor.hasImport(importDeclaration)) {
                answer.add(importDeclaration);
            }
        }
        
        return answer;
    }

    /**
     * This method parses the new Java file and returns a filled out
     * NewJavaFileVisitor. The returned visitor can be used to determine
     * characteristics of the new file, and a lost of new nodes that need to be
     * incorporated into the existing file.
     * 
     * @return
     */
    private NewJavaFileVisitor visitNewJavaFile() {
        CompilationUnit cu = getCompilationUnitFromSource(newJavaSource);
        NewJavaFileVisitor newVisitor = new NewJavaFileVisitor();
        cu.accept(newVisitor);

        return newVisitor;
    }

    @SuppressWarnings("unchecked")
    private void addExistsAnnotations(BodyDeclaration node, List<Annotation> oldAnnotations) {
        Set<String> newAnnotationTypes = new HashSet<String>();
        int lastAnnotationIndex = 0;
        int idx = 0;
        for (Object modifier : node.modifiers()) {
            if (modifier instanceof Annotation) {
                Annotation newAnnotation = (Annotation) modifier;
                newAnnotationTypes.add(newAnnotation.getTypeName()
                        .getFullyQualifiedName());
                lastAnnotationIndex = idx;
            }
            idx++;
        }
        
        if (oldAnnotations != null) {
            for (Annotation oldAnnotation : oldAnnotations) {
                if (newAnnotationTypes.contains(oldAnnotation.getTypeName()
                        .getFullyQualifiedName()))
                    continue;

                AST nodeAst = node.getAST();
                node.modifiers().add(lastAnnotationIndex++,
                        ASTNode.copySubtree(nodeAst, oldAnnotation));
            }
        }
    }
}
