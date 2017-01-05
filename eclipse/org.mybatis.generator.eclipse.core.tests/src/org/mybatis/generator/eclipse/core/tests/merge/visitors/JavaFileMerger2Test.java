/**
 *    Copyright 2006-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.eclipse.core.tests.merge.visitors;

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mybatis.generator.eclipse.core.tests.merge.support.TestResourceGenerator.simpleClassWithAllGeneratedItems;
import static org.mybatis.generator.eclipse.core.tests.merge.support.TestResourceGenerator.simpleClassWithGeneratedAndCustomItems;
import static org.mybatis.generator.eclipse.core.tests.merge.support.TestResourceGenerator.simpleEnumWithAllGeneratedItems;
import static org.mybatis.generator.eclipse.core.tests.merge.support.TestResourceGenerator.simpleEnumWithGeneratedAndCustomItems;
import static org.mybatis.generator.eclipse.core.tests.merge.support.TestResourceGenerator.simpleInterfaceWithAllGeneratedItems;
import static org.mybatis.generator.eclipse.core.tests.merge.support.TestResourceGenerator.simpleInterfaceWithGeneratedAndCustomItems;
import static org.mybatis.generator.eclipse.tests.harness.Utilities.getCompilationUnitSummaryFromSource;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasClass;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasClassCount;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasEnum;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasEnumCount;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasImport;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasImportCount;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasInterface;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.hasInterfaceCount;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.withEnumConstantCount;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.withField;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.withFieldCount;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.withMethod;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.withMethodCount;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.withSuperClass;
import static org.mybatis.generator.eclipse.tests.harness.matchers.Matchers.withSuperInterfaceCount;

import org.junit.Test;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.eclipse.core.merge.visitors.JavaFileMerger2;
import org.mybatis.generator.eclipse.tests.harness.summary.CompilationUnitSummary;

public class JavaFileMerger2Test {

    @Test
    public void testMergeOnRegularClasses() throws Exception {
        String newJavaSource = simpleClassWithAllGeneratedItems();
        String existingJavaSource = simpleClassWithGeneratedAndCustomItems();
        JavaFileMerger2 merger = new JavaFileMerger2(newJavaSource, existingJavaSource, MergeConstants.OLD_ELEMENT_TAGS);
        String mergedSource = merger.getMergedSource();
        
        CompilationUnitSummary summary = getCompilationUnitSummaryFromSource(mergedSource);
        
        assertThat(summary, hasImportCount(1));
        assertThat(summary, hasImport("import java.math.BigDecimal"));
        
        assertThat(summary, hasClassCount(1));
        assertThat(summary, hasClass("SimpleClass", withSuperInterfaceCount(0)));

        assertThat(summary, hasClass("SimpleClass", withMethodCount(4)));
        assertThat(summary, hasClass("SimpleClass", withMethod("getAmount()")));
        assertThat(summary, hasClass("SimpleClass", withMethod("setAmount(BigDecimal)")));
        assertThat(summary, hasClass("SimpleClass", withMethod("getId()")));
        assertThat(summary, hasClass("SimpleClass", withMethod("setId(int)")));

        assertThat(summary, hasClass("SimpleClass", withFieldCount(2)));
        assertThat(summary, hasClass("SimpleClass", withField("amount")));
        assertThat(summary, hasClass("SimpleClass", withField("id")));
        
        assertThat(summary, hasClass("SimpleClass", withSuperClass(nullValue())));
    }

    @Test
    public void testMergeOnRegularInterfaces() throws Exception {
        String newJavaSource = simpleInterfaceWithAllGeneratedItems();
        String existingJavaSource = simpleInterfaceWithGeneratedAndCustomItems();
        JavaFileMerger2 merger = new JavaFileMerger2(newJavaSource, existingJavaSource, MergeConstants.OLD_ELEMENT_TAGS);
        String mergedSource = merger.getMergedSource();
        
        CompilationUnitSummary summary = getCompilationUnitSummaryFromSource(mergedSource);
        
        assertThat(summary, hasImportCount(0));
        
        assertThat(summary, hasInterfaceCount(1));
        assertThat(summary, hasInterface("SimpleInterface", withSuperInterfaceCount(0)));

        assertThat(summary, hasInterface("SimpleInterface", withMethodCount(3)));
        assertThat(summary, hasInterface("SimpleInterface", withMethod("add(int,int)")));
        assertThat(summary, hasInterface("SimpleInterface", withMethod("count()")));
        assertThat(summary, hasInterface("SimpleInterface", withMethod("nonGeneratedMethod()")));
   }

    @Test
    public void testMergeOnRegularEnums() throws Exception {
        String newJavaSource = simpleEnumWithAllGeneratedItems();
        String existingJavaSource = simpleEnumWithGeneratedAndCustomItems();
        JavaFileMerger2 merger = new JavaFileMerger2(newJavaSource, existingJavaSource, MergeConstants.OLD_ELEMENT_TAGS);
        String mergedSource = merger.getMergedSource();
        
        CompilationUnitSummary summary = getCompilationUnitSummaryFromSource(mergedSource);
        
        assertThat(summary, hasImportCount(0));
        
        assertThat(summary, hasEnumCount(1));
        assertThat(summary, hasEnum("SimpleEnum", withSuperInterfaceCount(0)));

        assertThat(summary, hasEnum("SimpleEnum", withMethodCount(2)));
        assertThat(summary, hasEnum("SimpleEnum", withMethod("getCount()")));
        assertThat(summary, hasEnum("SimpleEnum", withMethod("getCountAgain()")));
        assertThat(summary, hasEnum("SimpleEnum", withFieldCount(1)));
        assertThat(summary, hasEnum("SimpleEnum", withField("counter")));
        assertThat(summary, hasEnum("SimpleEnum", withEnumConstantCount(5)));
   }
    
/*
 * Test cases:
 * 
 *  1. Add new enum constants
 *  2. All existing enum constants are generated and removed
 *  
 */
}
