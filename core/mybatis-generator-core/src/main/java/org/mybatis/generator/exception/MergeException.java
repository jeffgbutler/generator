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
package org.mybatis.generator.exception;

import java.io.Serial;
import java.util.List;

public class MergeException extends MultiMessageException {

    @Serial
    private static final long serialVersionUID = -7091914043920657025L;

    public MergeException(String message, List<String> extraMessages) {
        super(message, extraMessages);
    }

    public MergeException(String error) {
        super(error);
    }

    public MergeException(String error, Throwable cause) {
        super(error, cause);
    }
}
