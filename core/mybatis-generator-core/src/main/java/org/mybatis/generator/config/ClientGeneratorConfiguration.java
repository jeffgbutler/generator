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
package org.mybatis.generator.config;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.mybatis.generator.api.KnownRuntime;

public class ClientGeneratorConfiguration extends PropertyHolder {
    private final String targetPackage;
    private final String targetProject;
    private final @Nullable LegacyClientType legacyClientType;

    protected ClientGeneratorConfiguration(Builder builder) {
        super(builder);
        targetPackage = Objects.requireNonNull(builder.targetPackage);
        targetProject = Objects.requireNonNull(builder.targetProject);
        legacyClientType = builder.legacyClientType;
    }

    public String getTargetProject() {
        return targetProject;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public Optional<LegacyClientType> getLegacyClientType() {
        return Optional.ofNullable(legacyClientType);
    }

    public void validate(List<String> errors, String contextId, KnownRuntime knownRuntime) {
        String errorMessage = "ValidationError.31"; //$NON-NLS-1$

        if (knownRuntime.isLegacyMyBatis3Based() && legacyClientType == null) {
            errors.add(getString(errorMessage, "null", contextId)); //$NON-NLS-1$
        }

        if (knownRuntime == KnownRuntime.MYBATIS3_SIMPLE && legacyClientType == LegacyClientType.MIXED_MAPPER) {
            errors.add(getString(errorMessage, legacyClientType.name(), contextId));
        }

        if (!stringHasValue(targetProject)) {
            errors.add(getString("ValidationError.2", contextId)); //$NON-NLS-1$
        }

        if (!stringHasValue(targetPackage)) {
            errors.add(getString("ValidationError.12", "clientGenerator", contextId)); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public boolean requiresXmlMapper() {
        return legacyClientType == LegacyClientType.XML_MAPPER || legacyClientType == LegacyClientType.MIXED_MAPPER;
    }

    public static class Builder extends AbstractBuilder<Builder> {
        private @Nullable String targetPackage;
        private @Nullable String targetProject;
        private @Nullable LegacyClientType legacyClientType;

        public Builder withTargetPackage(@Nullable String targetPackage) {
            this.targetPackage = targetPackage;
            return this;
        }

        public Builder withTargetProject(@Nullable String targetProject) {
            this.targetProject = targetProject;
            return this;
        }

        public Builder withLegacyClientType(@Nullable LegacyClientType legacyClientType) {
            this.legacyClientType = legacyClientType;
            return this;
        }

        public ClientGeneratorConfiguration build() {
            return new ClientGeneratorConfiguration(this);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

    public enum LegacyClientType {
        ANNOTATED_MAPPER("AnnotatedMapper"), //$NON-NLS-1$
        MIXED_MAPPER("MixedMapper"), //$NON-NLS-1$
        XML_MAPPER("XmlMapper"); //$NON-NLS-1$

        private final String alias;

        LegacyClientType(String alias) {
            this.alias = alias;
        }

        public static @Nullable LegacyClientType getByAlias(String alias) {
            for (LegacyClientType ct : values()) {
                if (ct.alias.equalsIgnoreCase(alias)) {
                    return ct;
                }
            }
            return null;
        }
    }
}
