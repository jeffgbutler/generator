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

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import org.mybatis.generator.config.ClientGeneratorConfiguration;
import org.mybatis.generator.exception.InternalException;
import org.mybatis.generator.runtime.common.RecordModelGenerator;
import org.mybatis.generator.runtime.common.SimpleModelGenerator;
import org.mybatis.generator.runtime.mybatis3.javamapper.SimpleAnnotatedMapperGenerator;
import org.mybatis.generator.runtime.mybatis3.javamapper.SimpleJavaMapperGenerator;
import org.mybatis.generator.runtime.mybatis3.xmlmapper.SimpleXMLMapperGenerator;

/**
 * Runtime for generating simple MyBatis3 artifacts.
 *
 * <p>Simple means no "by example" methods, flat model, etc.
 *
 * @author Jeff Butler
 */
public class LegacySimpleJavaRuntime extends LegacyJavaRuntime {
    protected LegacySimpleJavaRuntime(Builder builder) {
        super(builder);
    }

    @Override
    protected void calculateXmlMapperGenerator() {
        if (context.getSqlMapGeneratorConfiguration().isPresent()) {
            xmlMapperGenerator = initializeSubBuilder(new SimpleXMLMapperGenerator.Builder()).build();
        }
    }

    @Override
    protected void calculateClientGenerator(ClientGeneratorConfiguration clientGeneratorConfiguration) {
        clientGeneratorConfiguration.getLegacyClientType()
                .map(s -> switch (s) {
                case XML_MAPPER -> new SimpleJavaMapperGenerator.Builder();
                case ANNOTATED_MAPPER -> new SimpleAnnotatedMapperGenerator.Builder();
                default -> throw new InternalException(
                        getString("ValidationError.31", s.name(), context.getId())); //$NON-NLS-1$
                })
                .ifPresent(builder -> {
                    initializeSubBuilder(builder)
                            .withProject(clientGeneratorConfiguration.getTargetProject())
                            .build();
                    javaGenerators.add(builder.build());
                });
    }

    @Override
    protected void calculateJavaModelGenerators() {
        if (introspectedTable.isRecordBased()) {
            var javaGenerator =  initializeSubBuilder(new RecordModelGenerator.Builder())
                    .withProject(getModelProject())
                    .build();
            javaGenerators.add(javaGenerator);
        } else {
            var javaGenerator = initializeSubBuilder(new SimpleModelGenerator.Builder())
                    .withProject(getModelProject())
                    .build();
            javaGenerators.add(javaGenerator);
        }
    }

    public static class Builder extends LegacyJavaRuntime.Builder {
        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LegacySimpleJavaRuntime build() {
            return new LegacySimpleJavaRuntime(this);
        }
    }
}
