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
package org.mybatis.generator.runtime.mybatis3;

import org.mybatis.generator.api.AbstractRuntime;
import org.mybatis.generator.config.ClientGeneratorConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.runtime.common.RecordModelGenerator;
import org.mybatis.generator.runtime.mybatis3.javamapper.AnnotatedMapperGenerator;
import org.mybatis.generator.runtime.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.runtime.mybatis3.javamapper.MixedMapperGenerator;
import org.mybatis.generator.runtime.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.runtime.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.runtime.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.runtime.mybatis3.model.RecordWithBLOBsGenerator;
import org.mybatis.generator.runtime.mybatis3.xmlmapper.MixedXmlMapperGenerator;
import org.mybatis.generator.runtime.mybatis3.xmlmapper.XMLMapperGenerator;

/**
 * Runtime for generating MyBatis3 artifacts.
 *
 * @author Jeff Butler
 */
public class LegacyJavaRuntime extends AbstractRuntime {
    protected LegacyJavaRuntime(Builder builder) {
        super(builder);
        calculateGenerators();
    }

    private void calculateGenerators() {
        calculateJavaModelGenerators();
        calculateClientGenerator();
        calculateXmlMapperGenerator();
    }

    protected void calculateXmlMapperGenerator() {
        if (context.getSqlMapGeneratorConfiguration().isPresent()) {
            xmlMapperGenerator = context.getClientGeneratorConfiguration()
                    .flatMap(ClientGeneratorConfiguration::getLegacyClientType)
                    .filter(ct -> ct == ClientGeneratorConfiguration.LegacyClientType.MIXED_MAPPER)
                    .map(ct -> initializeSubBuilder(new MixedXmlMapperGenerator.Builder()).build())
                    .orElseGet(() -> initializeSubBuilder(new XMLMapperGenerator.Builder()).build());
        }
    }

    protected void calculateClientGenerator() {
        if (!introspectedTable.getRules().generateClient()) {
            return;
        }

        context.getClientGeneratorConfiguration().ifPresent(this::calculateClientGenerator);
    }

    protected void calculateClientGenerator(ClientGeneratorConfiguration clientGeneratorConfiguration) {
        clientGeneratorConfiguration.getLegacyClientType()
                .map(s -> switch (s) {
                case XML_MAPPER -> new JavaMapperGenerator.Builder();
                case MIXED_MAPPER -> new MixedMapperGenerator.Builder();
                case ANNOTATED_MAPPER -> new AnnotatedMapperGenerator.Builder();
                })
                .ifPresent(builder -> {
                    initializeSubBuilder(builder)
                            .withProject(clientGeneratorConfiguration.getTargetProject())
                            .build();
                    javaGenerators.add(builder.build());
                });
    }

    protected void calculateJavaModelGenerators() {
        if (introspectedTable.getRules().generateExampleClass()) {
            var javaGenerator = initializeSubBuilder(new ExampleGenerator.Builder())
                    .withProject(getExampleProject())
                    .build();
            javaGenerators.add(javaGenerator);
        }

        if (introspectedTable.isRecordBased()) {
            var javaGenerator =  initializeSubBuilder(new RecordModelGenerator.Builder())
                    .includeNonBlobsConstructor(introspectedTable.getRules().generateSelectByExampleWithoutBLOBs())
                    .withProject(getModelProject())
                    .build();
            javaGenerators.add(javaGenerator);
            return;
        }

        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            var javaGenerator = initializeSubBuilder(new PrimaryKeyGenerator.Builder())
                    .withProject(getModelProject())
                    .build();
            javaGenerators.add(javaGenerator);
        }

        if (introspectedTable.getRules().generateBaseRecordClass()) {
            var javaGenerator = initializeSubBuilder(new BaseRecordGenerator.Builder())
                    .withProject(getModelProject())
                    .build();
            javaGenerators.add(javaGenerator);
        }

        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            var javaGenerator = initializeSubBuilder(new RecordWithBLOBsGenerator.Builder())
                    .withProject(getModelProject())
                    .build();
            javaGenerators.add(javaGenerator);
        }
    }

    protected String getExampleProject() {
        String project = context.getModelGeneratorConfiguration()
                .getProperty(PropertyRegistry.MODEL_GENERATOR_EXAMPLE_PROJECT);

        return StringUtility.stringValueOrElseGet(project, this::getModelProject);
    }

    public static class Builder extends AbstractRuntimeBuilder<Builder> {
        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LegacyJavaRuntime build() {
            return new LegacyJavaRuntime(this);
        }
    }
}
