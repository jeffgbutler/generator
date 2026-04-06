/*
 *    Copyright 2006-2026 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.plugins;

import java.util.List;
import java.util.function.Consumer;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.java.TopLevelRecord;

public class JSpecifyPlugin extends PluginAdapter {
    private static final String JSPECIFY_PLUGIN_ENABLED = "JSpecifyPluginEnabled"; //$NON-NLS-1$
    private static final String SKIP_PROPERTY = "skipJSpecifyPlugin"; //$NON-NLS-1$
    public static final FullyQualifiedJavaType NULLABLE_IMPORT
            = new FullyQualifiedJavaType("org.jspecify.annotations.Nullable"); //$NON-NLS-1$
    public static final String NULLABLE_ANNOTATION = "@Nullable"; //$NON-NLS-1$
    public static final FullyQualifiedJavaType NULL_MARKED_IMPORT
            = new FullyQualifiedJavaType("org.jspecify.annotations.NullMarked"); //$NON-NLS-1$
    public static final String NULL_MARKED_ANNOTATION = "@NullMarked"; //$NON-NLS-1$

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        boolean skipped = Boolean.parseBoolean(introspectedTable.getTableConfigurationProperty(SKIP_PROPERTY));

        if (!skipped) {
            introspectedTable.setAttribute(JSPECIFY_PLUGIN_ENABLED, Boolean.TRUE);
        }
    }

    @Override
    public boolean clientUpdateSelectiveColumnsMethodGenerated(Method method, Interface interfaze,
                                                               IntrospectedTable introspectedTable) {
        // this method doesn't work well with null-marked models. Better to use the full function update statement
        return !isEnabled(introspectedTable);
    }

    @Override
    public boolean clientUpdateAllColumnsMethodGenerated(Method method, Interface interfaze,
                                                         IntrospectedTable introspectedTable) {
        // this method doesn't work well with null-marked models. Better to use the full function update statement
        return !isEnabled(introspectedTable);
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        if (introspectedTable.getKnownRuntime().isLegacyMyBatis3Based()) {
            // we don't have a general purpose update in the legacy MyBatis 3 runtimes, so we need it there
            return true;
        }

        // this method doesn't work well with null-marked models. Better to use the full function update statement
        return !isEnabled(introspectedTable);
    }

    @Override
    public boolean modelRecordGenerated(TopLevelRecord topLevelRecord, IntrospectedTable introspectedTable) {
        if (isEnabled(introspectedTable)) {
            topLevelRecord.addImportedType(NULL_MARKED_IMPORT);
            topLevelRecord.addAnnotation(NULL_MARKED_ANNOTATION);

            addNullableToParameters(topLevelRecord.getParameters(), introspectedTable, topLevelRecord::addImportedType);
        }

        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return processGeneratedClass(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return processGeneratedClass(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
                                                      IntrospectedTable introspectedTable) {
        return processGeneratedClass(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (isEnabled(introspectedTable) && introspectedColumn.isNullable()
                && !introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
            topLevelClass.addImportedType(NULLABLE_IMPORT);
            field.addTypeAnnotation(NULLABLE_ANNOTATION);
        }

        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (isEnabled(introspectedTable) && introspectedColumn.isNullable()
                && !introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
            topLevelClass.addImportedType(NULLABLE_IMPORT);
            method.addReturnTypeAnnotation(NULLABLE_ANNOTATION);
        }

        return true;
    }

    private boolean processGeneratedClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (isEnabled(introspectedTable)) {
            topLevelClass.addImportedType(NULL_MARKED_IMPORT);
            topLevelClass.addAnnotation(NULL_MARKED_ANNOTATION);

            // add nullable to parameterized constructor parameters
            topLevelClass.getMethods().stream()
                    .filter(Method::isConstructor)
                    .filter(m -> !m.getParameters().isEmpty())
                    .findFirst()
                    .ifPresent(m -> addNullableToParameters(m.getParameters(), introspectedTable,
                            topLevelClass::addImportedType));
        }

        return true;
    }

    private void addNullableToParameters(List<Parameter> parameters, IntrospectedTable introspectedTable,
                                         Consumer<FullyQualifiedJavaType> importConsumer) {
        for (Parameter parameter : parameters) {
            if (isColumnNullable(parameter.getName(), introspectedTable)) {
                importConsumer.accept(NULLABLE_IMPORT);
                parameter.addAnnotation(NULLABLE_ANNOTATION);
            }
        }
    }

    private boolean isColumnNullable(String property, IntrospectedTable introspectedTable) {
        return introspectedTable.getAllColumns().stream()
                .filter(c -> !c.getFullyQualifiedJavaType().isPrimitive())
                .filter(c -> c.getJavaProperty().equals(property))
                .anyMatch(IntrospectedColumn::isNullable);
    }

    public static boolean isEnabled(IntrospectedTable introspectedTable) {
        Boolean enabled = (Boolean) introspectedTable.getAttribute(JSPECIFY_PLUGIN_ENABLED);
        return Boolean.TRUE.equals(enabled);
    }
}
