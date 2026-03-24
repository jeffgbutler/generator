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
package org.mybatis.generator.runtime.mybatis3.xmlmapper.elements;

import java.util.Optional;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class SelectByPrimaryKeyElementGenerator extends AbstractXmlMapperElementGenerator {

    protected SelectByPrimaryKeyElementGenerator(Builder builder) {
        super(builder);
    }

    @Override
    public Optional<XmlElement> generateElement() {
        if (!introspectedTable.getRules().generateSelectByPrimaryKey()) {
            return Optional.empty();
        }

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getSelectByPrimaryKeyStatementId())); //$NON-NLS-1$
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                    introspectedTable.getBaseResultMapId()));
        }

        String parameterType;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            parameterType = introspectedTable.getPrimaryKeyType();
        } else {
            // PK fields are in the base class. If more than on PK
            // field, then they are coming in a map.
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                parameterType = "map"; //$NON-NLS-1$
            } else {
                parameterType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().toString();
            }
        }

        answer.addAttribute(new Attribute("parameterType", parameterType)); //$NON-NLS-1$

        commentGenerator.addComment(answer);

        answer.addElement(new TextElement("select ")); //$NON-NLS-1$
        answer.addElement(getBaseColumnListElement());
        if (introspectedTable.hasBLOBColumns()) {
            answer.addElement(new TextElement(",")); //$NON-NLS-1$
            answer.addElement(getBlobColumnListElement());
        }

        String fromLine = "from " + //$NON-NLS-1$
                introspectedTable.getAliasedFullyQualifiedRuntimeTableName();
        answer.addElement(new TextElement(fromLine));

        buildPrimaryKeyWhereClause().forEach(answer::addElement);

        return Optional.of(answer);
    }

    @Override
    public boolean callPlugins(XmlElement element) {
        return pluginAggregator.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    public static class Builder extends AbstractGeneratorBuilder<Builder> {
        @Override
        protected Builder getThis() {
            return this;
        }

        public SelectByPrimaryKeyElementGenerator build() {
            return new SelectByPrimaryKeyElementGenerator(this);
        }
    }
}
