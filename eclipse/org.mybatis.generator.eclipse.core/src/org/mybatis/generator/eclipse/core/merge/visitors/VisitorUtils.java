package org.mybatis.generator.eclipse.core.merge.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.IDocElement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;

/**
 * Utility methods used by the visitors.
 * 
 * @author Jeff Butler
 *
 */
public abstract class VisitorUtils extends ASTVisitor {

    public static boolean isPublic(BodyDeclaration node) {
        return Modifier.isPublic(node.getModifiers());
    }

    public static boolean isTopLevel(ASTNode node) {
        ASTNode parent = node.getParent();
        return parent != null && parent.getNodeType() == ASTNode.COMPILATION_UNIT;
    }

    public static String stringify(Type type) {
        TypeStringifier ts = new TypeStringifier();
        type.accept(ts);
        return ts.toString();
    }
    
    public static boolean nodeShouldBeDeleted(BodyDeclaration node, Set<String> javadocTags) {
        Javadoc javadoc = node.getJavadoc();
        if (javadoc != null && javadocContainsGeneratedTag(javadoc, javadocTags)) {
            return true;
        }

        return false;
    }

    private static boolean javadocContainsGeneratedTag(Javadoc javadoc, Set<String> javadocTags) {
        @SuppressWarnings("unchecked")
        List<TagElement> tagElements = javadoc.tags();
        for (TagElement tagElement : tagElements) {
            if (tagElementContainsGeneratedTag(tagElement, javadocTags)) {
                return true;
            }
        }

        return false;
    }

    private static boolean tagElementContainsGeneratedTag(TagElement tagElement, Set<String> javadocTags) {
        String tagName = tagElement.getTagName();
        if (tagName != null && javadocTags.contains(tagName)) {
            if (!generatedNodeShouldBeKept(tagElement)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean generatedNodeShouldBeKept(TagElement tagElement) {
        @SuppressWarnings("unchecked")
        List<IDocElement> fragments = tagElement.fragments();
        for (IDocElement fragment : fragments) {
            if (fragmentIsDoNotDelete(fragment)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean fragmentIsDoNotDelete(IDocElement fragment) {
        if (fragment instanceof TextElement) {
            TextElement textElement = (TextElement) fragment;
            if ("do_not_delete_during_merge".equals(textElement.getText())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<Annotation> retrieveAnnotations(BodyDeclaration node) {
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
