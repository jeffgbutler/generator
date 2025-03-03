/*
 *    Copyright 2006-2025 the original author or authors.
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
package org.mybatis.generator.exception;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class MultiMessageException extends Exception {

    @Serial
    private static final long serialVersionUID = -5358501949588130025L;
    private final List<String> errors = new ArrayList<>();

    public MultiMessageException(List<String> errors) {
        this.errors.addAll(errors);
    }

    public MultiMessageException(String error) {
        this.errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return errors.get(0);
    }
}
