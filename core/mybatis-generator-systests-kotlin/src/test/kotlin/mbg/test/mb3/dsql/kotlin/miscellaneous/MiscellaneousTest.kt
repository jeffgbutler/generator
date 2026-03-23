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
package mbg.test.mb3.dsql.kotlin.miscellaneous

import mbg.test.common.FirstName
import mbg.test.common.MyTime
import mbg.test.mb3.common.TestEnum
import mbg.test.mb3.generated.dsql.kotlin.miscellaneous.mapper.MyObjectMapper
import mbg.test.mb3.generated.dsql.kotlin.miscellaneous.mapper.*
import mbg.test.mb3.generated.dsql.kotlin.miscellaneous.mapper.MyObjectDynamicSqlSupport.MY_OBJECT
import mbg.test.mb3.generated.dsql.kotlin.miscellaneous.model.EnumOrdinalTest
import mbg.test.mb3.generated.dsql.kotlin.miscellaneous.model.EnumTest
import mbg.test.mb3.generated.dsql.kotlin.miscellaneous.model.MyObject
import mbg.test.mb3.generated.dsql.kotlin.miscellaneous.model.Regexrename
import org.apache.ibatis.session.RowBounds
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author Jeff Butler
 */
class MiscellaneousTest : AbstractAnnotatedMiscellaneousTest() {

    @Test
    fun testMyObjectInsertMyObject() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            val record = MyObject(2, 1, FirstName("Jeff"), "Butler", LocalDate.now(), MyTime(12, 34, 5),
                LocalDateTime.now().withNano(0), 6, 10L, 15.12345)

            mapper.insert(record)

            val returnedRecord = mapper.selectByPrimaryKey(2, 1)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(record.startDate, returnedRecord.startDate)
                assertEquals(record.decimal100field!!, returnedRecord.decimal100field)
                assertEquals(record.decimal155field!!, returnedRecord.decimal155field)
                assertEquals(record.decimal60field!!, returnedRecord.decimal60field)
                assertEquals(record.firstname, returnedRecord.firstname)
                assertEquals(record.id1!!, returnedRecord.id1)
                assertEquals(record.id2!!, returnedRecord.id2)
                assertEquals(record.lastname, returnedRecord.lastname)
                assertEquals(record.timefield, returnedRecord.timefield)
                assertEquals(record.timestampfield, returnedRecord.timestampfield)
            }
        }
    }

    @Test
    fun testMyObjectUpdateByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            val record = MyObject(2, 1, FirstName("Jeff"), "Smith")
            mapper.insert(record)

            val updateRecord = record.copy(firstname = FirstName("Scott"), lastname = "Jones")
            val rows = mapper.updateByPrimaryKey(updateRecord)
            assertThat(rows).isEqualTo(1)

            val record2 = mapper.selectByPrimaryKey(2, 1)

            assertThat(record2).isNotNull
            if (record2 != null) {
                assertEquals(updateRecord.firstname, record2.firstname)
                assertEquals(updateRecord.lastname, record2.lastname)
                assertEquals(updateRecord.id1!!, record2.id1)
                assertEquals(updateRecord.id2!!, record2.id2)
            }
        }
    }

    @Test
    fun testMyObjectUpdateByPrimaryKeySelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            val record = MyObject(2, 1, FirstName("Jeff"), "Smith", decimal60field = 5)
            mapper.insert(record)

            val newRecord = MyObject(2, 1, FirstName("Scott"), startDate = LocalDate.now())

            val rows = mapper.updateByPrimaryKeySelective(newRecord)
            assertEquals(1, rows)

            val returnedRecord = mapper.selectByPrimaryKey(2, 1)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(newRecord.startDate, returnedRecord.startDate)
                assertEquals(record.decimal100field, returnedRecord.decimal100field)
                assertEquals(record.decimal155field, returnedRecord.decimal155field)
                assertEquals(record.decimal60field!!, returnedRecord.decimal60field)
                assertEquals(newRecord.firstname, returnedRecord.firstname)
                assertEquals(record.id1!!, returnedRecord.id1)
                assertEquals(record.id2!!, returnedRecord.id2)
                assertEquals(record.lastname, returnedRecord.lastname)
                assertEquals(record.timefield, returnedRecord.timefield)
                assertEquals(record.timestampfield, returnedRecord.timestampfield)
            }
        }
    }

    @Test
    fun testMyObjectDeleteByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            val record = MyObject(2, 1, FirstName("Jeff"), "Smith")
            mapper.insert(record)

            val rows = mapper.deleteByPrimaryKey(2, 1)
            assertEquals(1, rows)

            val answer = mapper.select { allRows() }
            assertEquals(0, answer.size)
        }
    }

    @Test
    fun testMyObjectDeleteByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(2, 1, FirstName("Jeff"), "Smith")
            mapper.insert(record)

            record = MyObject(4, 3, FirstName("Bob"), "Jones")
            mapper.insert(record)

            var answer = mapper.select { allRows() }
            assertEquals(2, answer.size)

            val rows = mapper.delete { where { MY_OBJECT.LASTNAME isLike "J%" } }

            assertEquals(1, rows)

            answer = mapper.select { allRows() }
            assertEquals(1, answer.size)
        }
    }

    @Test
    fun testMyObjectSelectByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            val record = MyObject(2, 1, FirstName("Jeff"), "Smith")
            mapper.insert(record)

            val record1 = MyObject(4, 3, FirstName("Bob"), "Jones")
            mapper.insert(record1)

            val newRecord = mapper.selectByPrimaryKey(4, 3)

            assertThat(newRecord).isNotNull
            if (newRecord != null) {
                assertEquals(record1.firstname, newRecord.firstname)
                assertEquals(record1.lastname, newRecord.lastname)
                assertEquals(record1.id1!!, newRecord.id1)
                assertEquals(record1.id2!!, newRecord.id2)
            }
        }
    }

    @Test
    fun testMyObjectSelectByExampleLike() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(1, 1, FirstName("Fred"), "Flintstone")
            mapper.insert(record)

            record = MyObject(2, 1, FirstName("Wilma"), "Flintstone")
            mapper.insert(record)

            record = MyObject(3, 1, FirstName("Pebbles"), "Flintstone")
            mapper.insert(record)

            record = MyObject(1, 2, FirstName("Barney"), "Rubble")
            mapper.insert(record)

            record = MyObject(2, 2, FirstName("Betty"), "Rubble")
            mapper.insert(record)

            record = MyObject(3, 2, FirstName("Bamm Bamm"), "Rubble")
            mapper.insert(record)

            val fn1 = FirstName()
            fn1.value = "B%"

            val answer = mapper.select {
                where { MY_OBJECT.FIRSTNAME isLike fn1 }
                orderBy(MY_OBJECT.ID1, MY_OBJECT.ID2)
            }

            assertEquals(3, answer.size)
            var returnedRecord = answer[0]
            assertEquals(2, returnedRecord.id1)
            assertEquals(1, returnedRecord.id2)
            returnedRecord = answer[1]
            assertEquals(2, returnedRecord.id1)
            assertEquals(2, returnedRecord.id2)
            returnedRecord = answer[2]
            assertEquals(2, returnedRecord.id1)
            assertEquals(3, returnedRecord.id2)
        }
    }

    @Test
    fun testMyObjectSelectByExampleNotLike() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(1, 1, FirstName("Fred"), "Flintstone")
            mapper.insert(record)

            record = MyObject(2, 1, FirstName("Wilma"), "Flintstone")
            mapper.insert(record)

            record = MyObject(3, 1, FirstName("Pebbles"), "Flintstone")
            mapper.insert(record)

            record = MyObject(1, 2, FirstName("Barney"), "Rubble")
            mapper.insert(record)

            record = MyObject(2, 2, FirstName("Betty"), "Rubble")
            mapper.insert(record)

            record = MyObject(3, 2, FirstName("Bamm Bamm"), "Rubble")
            mapper.insert(record)

            val fn1 = FirstName()
            fn1.value = "B%"

            val answer = mapper.select {
                where { MY_OBJECT.FIRSTNAME isNotLike fn1 }
                orderBy(MY_OBJECT.ID1, MY_OBJECT.ID2)
            }

            assertEquals(3, answer.size)
            var returnedRecord = answer[0]
            assertEquals(1, returnedRecord.id1)
            assertEquals(1, returnedRecord.id2)
            returnedRecord = answer[1]
            assertEquals(1, returnedRecord.id1)
            assertEquals(2, returnedRecord.id2)
            returnedRecord = answer[2]
            assertEquals(1, returnedRecord.id1)
            assertEquals(3, returnedRecord.id2)
        }
    }

    @Test
    fun testMyObjectSelectByExampleComplexLike() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)

            mapper.insert(MyObject(id1 = 1, id2 = 1, firstname = FirstName("Fred"), lastname = "Flintstone"))
            mapper.insert(MyObject(id1 = 1, id2 = 2, firstname = FirstName("Wilma"), lastname = "Flintstone"))
            mapper.insert(MyObject(id1 = 1, id2 = 3, firstname = FirstName("Pebbles"), lastname = "Flintstone"))
            mapper.insert(MyObject(id1 = 2, id2 = 1, firstname = FirstName("Barney"), lastname = "Rubble"))
            mapper.insert(MyObject(id1 = 2, id2 = 2, firstname = FirstName("Betty"), lastname = "Rubble"))
            mapper.insert(MyObject(id1 = 2, id2 = 3, firstname = FirstName("Bamm Bamm"), lastname = "Rubble"))

            val fn1 = FirstName()
            fn1.value = "B%"
            val fn2 = FirstName()
            fn2.value = "W%"

            val answer = mapper.select {
                where {
                    group {
                        MY_OBJECT.FIRSTNAME isLike fn1
                        and { MY_OBJECT.ID2 isEqualTo 3 }
                    }
                    or { MY_OBJECT.FIRSTNAME isLike fn2 }
                }
                orderBy(MY_OBJECT.ID1, MY_OBJECT.ID2)
            }

            assertEquals(2, answer.size)
            var returnedRecord = answer[0]
            assertEquals(1, returnedRecord.id1)
            assertEquals(2, returnedRecord.id2)
            returnedRecord = answer[1]
            assertEquals(2, returnedRecord.id1)
            assertEquals(3, returnedRecord.id2)
        }
    }

    @Test
    fun testMyObjectSelectByExampleIn() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(1, 1, FirstName("Fred"), "Flintstone")
            mapper.insert(record)

            record = MyObject(2, 1, FirstName("Wilma"), "Flintstone")
            mapper.insert(record)

            record = MyObject(3, 1, FirstName("Pebbles"), "Flintstone")
            mapper.insert(record)

            record = MyObject(1, 2, FirstName("Barney"), "Rubble")
            mapper.insert(record)

            record = MyObject(2, 2, FirstName("Betty"), "Rubble")
            mapper.insert(record)

            record = MyObject(3, 2, FirstName("Bamm Bamm"), "Rubble")
            mapper.insert(record)

            val ids = listOf(1, 3)

            val answer = mapper.select {
                where { MY_OBJECT.ID2 isIn ids }
                orderBy(MY_OBJECT.ID1, MY_OBJECT.ID2)
            }
            assertEquals(4, answer.size)
            var returnedRecord = answer[0]
            assertEquals(1, returnedRecord.id1)
            assertEquals(1, returnedRecord.id2)
            assertEquals("Flintstone", returnedRecord.lastname)

            returnedRecord = answer[1]
            assertEquals(1, returnedRecord.id1)
            assertEquals(3, returnedRecord.id2)
            assertEquals("Flintstone", returnedRecord.lastname)

            returnedRecord = answer[2]
            assertEquals(2, returnedRecord.id1)
            assertEquals(1, returnedRecord.id2)
            assertEquals("Rubble", returnedRecord.lastname)

            returnedRecord = answer[3]
            assertEquals(2, returnedRecord.id1)
            assertEquals(3, returnedRecord.id2)
            assertEquals("Rubble", returnedRecord.lastname)
        }
    }

    @Test
    fun testMyObjectSelectByExampleBetween() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(1, 1, FirstName("Fred"), "Flintstone")
            mapper.insert(record)

            record = MyObject(2, 1, FirstName("Wilma"), "Flintstone")
            mapper.insert(record)

            record = MyObject(3, 1, FirstName("Pebbles"), "Flintstone")
            mapper.insert(record)

            record = MyObject(1, 2, FirstName("Barney"), "Rubble")
            mapper.insert(record)

            record = MyObject(2, 2, FirstName("Betty"), "Rubble")
            mapper.insert(record)

            record = MyObject(3, 2, FirstName("Bamm Bamm"), "Rubble")
            mapper.insert(record)

            val answer = mapper.select {
                where { MY_OBJECT.ID2 isBetween 1 and 3 }
                orderBy(MY_OBJECT.ID1, MY_OBJECT.ID2)
            }
            assertEquals(6, answer.size)
        }
    }

    @Test
    fun testMyObjectSelectByExampleTimeEquals() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            val record = MyObject(2, 1, FirstName("Jeff"), "Butler", LocalDate.now(),
                timefield = MyTime(12, 34, 5), timestampfield = LocalDateTime.now().withNano(0), decimal60field = 6,
                decimal100field = 10L, decimal155field = 15.12345)
            mapper.insert(record)

            val results = mapper.select { where { MY_OBJECT.TIMEFIELD isEqualTo MyTime(12, 34, 5) } }
            assertEquals(1, results.size)

            val returnedRecord = results[0]
            assertEquals(record.startDate, returnedRecord.startDate)
            assertEquals(record.decimal100field!!, returnedRecord.decimal100field)
            assertEquals(record.decimal155field!!, returnedRecord.decimal155field)
            assertEquals(record.decimal60field!!, returnedRecord.decimal60field)
            assertEquals(record.firstname, returnedRecord.firstname)
            assertEquals(record.id1!!, returnedRecord.id1)
            assertEquals(record.id2!!, returnedRecord.id2)
            assertEquals(record.lastname, returnedRecord.lastname)
            assertEquals(record.timefield, returnedRecord.timefield)
            assertEquals(record.timestampfield, returnedRecord.timestampfield)
        }
    }

    @Test
    fun testFieldIgnored() {
        assertThrows(NoSuchFieldException::class.java) { MY_OBJECT::class.java.getDeclaredField("DECIMAL30FIELD") }
    }

    @Test
    fun testMyObjectUpdateByExampleSelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(2, 1, FirstName("Jeff"), "Smith")
            mapper.insert(record)

            record = MyObject(4, 3, FirstName("Bob"), "Jones")
            mapper.insert(record)

            val newRecord = MyObject(lastname = "Barker")

            val fn1 = FirstName("B%")

            val rows = mapper.update {
                updateSelectiveColumns(newRecord)
                where { MY_OBJECT.FIRSTNAME isLike fn1 }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { MY_OBJECT.FIRSTNAME isLike fn1 } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]
            assertEquals(newRecord.lastname, returnedRecord.lastname)
            assertEquals(record.firstname, returnedRecord.firstname)
            assertEquals(record.id1!!, returnedRecord.id1)
            assertEquals(record.id2!!, returnedRecord.id2)
        }
    }

    @Test
    fun testMyObjectUpdateByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(2, 1, FirstName("Jeff"), "Smith")
            mapper.insert(record)

            record = MyObject(4, 3, FirstName("Bob"), "Jones")
            mapper.insert(record)

            val newRecord = MyObject(4, 3, lastname = "Barker")

            val rows = mapper.update {
                updateAllColumns(newRecord)
                where {
                    MY_OBJECT.ID1 isEqualTo 3
                    and { MY_OBJECT.ID2 isEqualTo 4 }
                }
            }
            assertEquals(1, rows)

            val answer = mapper.select {
                where {
                    MY_OBJECT.ID1 isEqualTo 3
                    and { MY_OBJECT.ID2 isEqualTo 4 }
                }
            }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(newRecord.lastname, returnedRecord.lastname)
            assertNull(returnedRecord.firstname)
            assertEquals(newRecord.id1!!, returnedRecord.id1)
            assertEquals(newRecord.id2!!, returnedRecord.id2)
        }
    }

    @Test
    fun testThatMultiRowInsertMethodsAreNotGenerated() {
        // regex rename has a generated key, but it is not JDBC. So it should be
        // ignored by the generator
        assertThrows(NoSuchMethodException::class.java) {
            RegexrenameMapper::class.java.getMethod("insertMultiple", Collection::class.java)
        }

        assertThrows(NoSuchMethodException::class.java) {
            RegexrenameMapper::class.java.getMethod("insertMultiple", MultiRowInsertStatementProvider::class.java)
        }

        assertThrows(NoSuchMethodException::class.java) {
            RegexrenameMapper::class.java.getMethod("insertMultiple", String::class.java, List::class.java)
        }
    }

    @Test
    fun testThatRowBoundsMethodsAreNotGenerated() {
        // regex rename has the rowbounds plugin, but that plugin is disabled for MyBatisDynamicSQLV2
        assertThrows(NoSuchMethodException::class.java) {
            RegexrenameMapper::class.java.getMethod("selectManyWithRowbounds", SelectStatementProvider::class.java, RowBounds::class.java)
        }

        assertThrows(NoSuchMethodException::class.java) {
            RegexrenameMapper::class.java.getMethod("selectManyWithRowbounds", RowBounds::class.java)
        }

        assertThrows(NoSuchMethodException::class.java) {
            RegexrenameMapper::class.java.getMethod("selectByExample", RowBounds::class.java)
        }

        assertThrows(NoSuchMethodException::class.java) {
            RegexrenameMapper::class.java.getMethod("selectDistinctByExample", RowBounds::class.java)
        }
    }

    @Test
    fun testRegexRenameInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(RegexrenameMapper::class.java)
            val record = Regexrename(name = "Fred", address = "123 Main Street", zipCode = "99999")

            mapper.insert(record)
            // test generated id
            assertEquals(1, record.id)

            val returnedRecord = mapper.selectByPrimaryKey(1)

            assertThat(returnedRecord).isNotNull
            if(returnedRecord != null) {
                assertEquals(record.address, returnedRecord.address)
                assertEquals(1, returnedRecord.id)
                assertEquals(record.name, returnedRecord.name)
                assertEquals(record.zipCode, returnedRecord.zipCode)
            }
        }
    }

    @Test
    fun testRegexRenameInsertSelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(RegexrenameMapper::class.java)
            val record = Regexrename(zipCode = "99999")

            mapper.insertSelective(record)
            assertEquals(1, record.id)

            val returnedRecord = mapper.selectByPrimaryKey(1)

            assertThat(returnedRecord).isNotNull
            if(returnedRecord != null) {
                assertNull(returnedRecord.address)
                assertEquals(record.id, returnedRecord.id)
                assertNull(returnedRecord.name)
                assertEquals(record.zipCode, returnedRecord.zipCode)
            }
        }
    }

    @Test
    fun testMyObjectSelectByExampleLikeInsensitive() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(MyObjectMapper::class.java)
            var record = MyObject(1, 1, FirstName("Fred"), "Flintstone")
            mapper.insert(record)

            record = MyObject(2, 1, FirstName("Wilma"), "Flintstone")
            mapper.insert(record)

            record = MyObject(3, 1, FirstName("Pebbles"), "Flintstone")
            mapper.insert(record)

            record = MyObject(1, 2, FirstName("Barney"), "Rubble")
            mapper.insert(record)

            record = MyObject(2, 2, FirstName("Betty"), "Rubble")
            mapper.insert(record)

            record = MyObject(3, 2, FirstName("Bamm Bamm"), "Rubble")
            mapper.insert(record)

            var answer = mapper.select {
                where { MY_OBJECT.LASTNAME isLike "RU%" }
                orderBy(MY_OBJECT.ID1, MY_OBJECT.ID2)
            }
            assertEquals(0, answer.size)

            answer = mapper.select { where { MY_OBJECT.LASTNAME isLikeCaseInsensitive "RU%" } }
            assertEquals(3, answer.size)

            var returnedRecord = answer[0]
            assertEquals(2, returnedRecord.id1)
            assertEquals(1, returnedRecord.id2)
            returnedRecord = answer[1]
            assertEquals(2, returnedRecord.id1)
            assertEquals(2, returnedRecord.id2)
            returnedRecord = answer[2]
            assertEquals(2, returnedRecord.id1)
            assertEquals(3, returnedRecord.id2)
        }
    }

    @Test
    fun testEnum() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(EnumTestMapper::class.java)

            val enumTest = EnumTest(1, TestEnum.FRED)
            val rows = mapper.insert(enumTest)
            assertEquals(1, rows)

            val returnedRecords = mapper.select { allRows() }
            assertEquals(1, returnedRecords.size)

            val returnedRecord = returnedRecords[0]
            assertEquals(1, returnedRecord.id)
            assertEquals(TestEnum.FRED, returnedRecord.name)
        }
    }

    @Test
    fun testEnumInsertMultiple() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(EnumTestMapper::class.java)
            val records = listOf(
                EnumTest(1, TestEnum.FRED),
                EnumTest(2, TestEnum.BARNEY)
            )

            val rows = mapper.insertMultiple(records)
            assertEquals(2, rows)

            val returnedRecords = mapper.select { allRows() }
            assertEquals(2, returnedRecords.size)

            val returnedRecord = returnedRecords[0]
            assertEquals(1, returnedRecord.id)
            assertEquals(TestEnum.FRED, returnedRecord.name)
        }
    }

    @Test
    fun testEnumOrdinal() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(EnumOrdinalTestMapper::class.java)


            val enumTest = EnumOrdinalTest(1, TestEnum.FRED)

            val rows = mapper.insert(enumTest)
            assertEquals(1, rows)

            val returnedRecords = mapper.select { allRows() }
            assertEquals(1, returnedRecords.size)

            val returnedRecord = returnedRecords[0]
            assertEquals(1, returnedRecord.id)
            assertEquals(TestEnum.FRED, returnedRecord.name)
        }
    }

    @Test
    fun testEnumOrdinalInsertMultiple() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(EnumOrdinalTestMapper::class.java)
            val records = listOf(
                EnumOrdinalTest(1, TestEnum.FRED),
                EnumOrdinalTest(2, TestEnum.BARNEY)
            )

            val rows = mapper.insertMultiple(records)
            assertEquals(2, rows)

            val returnedRecords = mapper.select { allRows() }
            assertEquals(2, returnedRecords.size)

            val returnedRecord = returnedRecords[0]
            assertEquals(1, returnedRecord.id)
            assertEquals(TestEnum.FRED, returnedRecord.name)
        }
    }
}
