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
package mbg.test.mb3.dsql.kotlin.idiomatic

import mbg.test.common.util.TestUtilities.blobsAreEqual
import mbg.test.common.util.TestUtilities.generateRandomBlob
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.*
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.AwfulTableDynamicSqlSupport.awfulTable
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.FieldsblobsDynamicSqlSupport.fieldsblobs
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.FieldsonlyDynamicSqlSupport.fieldsonly
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.PkblobsDynamicSqlSupport.pkblobs
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.PkfieldsDynamicSqlSupport.pkfieldstable
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.PkfieldsblobsDynamicSqlSupport.pkfieldsblobs
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.PkonlyDynamicSqlSupport.pkonly
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.mapper.mbgtest.*
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.model.*
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.model.Id
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.model.Translation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * @author Jeff Butler
 */
class DynamicSqlIdiomaticTest : AbstractIdiomaticTest() {

    @Test
    fun testFieldsOnlyInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            mapper.insert(Fieldsonly(5, 11.22, 33.44))

            val answer = mapper.select {
                where { fieldsonly.integerfield isEqualTo 5 }
            }
            assertThat(answer).hasSize(1)

            with(answer[0]) {
                assertThat(integerfield).isEqualTo(5)
                assertThat(doublefield).isEqualTo(11.22)
                assertThat(floatfield).isEqualTo(33.44)
            }
        }
    }

    @Test
    fun testFieldsOnlyselect() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            mapper.insert(Fieldsonly(5, 11.22, 33.44))
            mapper.insert(Fieldsonly(8, 44.55, 66.77))
            mapper.insert(Fieldsonly(9, 88.99, 100.111))

            var answer = mapper.select {
                where { fieldsonly.integerfield isGreaterThan 5 }
            }
            assertThat(answer).hasSize(2)

            answer = mapper.select {
                allRows()
            }
            assertThat(answer).hasSize(3)
        }
    }

    @Test
    fun testFieldsOnlySelectByExamplewithMultiInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)

            val record1 = Fieldsonly(5, 11.22, 33.44)
            val record2 = Fieldsonly(8, 44.55, 66.77)
            val record3 = Fieldsonly(9, 88.99, 100.111)

            mapper.insertMultiple(record1, record2, record3)

            var answer = mapper.select {
                where { fieldsonly.integerfield isGreaterThan 5 }
            }
            assertThat(answer).hasSize(2)

            answer = mapper.select {
                allRows()
                orderBy(fieldsonly.integerfield)
            }

            assertThat(answer).hasSize(3)
            assertThat(answer[0].integerfield).isEqualTo(5)
            assertThat(answer[1].integerfield).isEqualTo(8)
            assertThat(answer[2].integerfield).isEqualTo(9)
        }
    }

    @Test
    fun testFieldsOnlySelectByExampleDistinct() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            val record = Fieldsonly(5, 11.22, 33.44)
            mapper.insert(record)
            mapper.insert(record)
            mapper.insert(record)
            mapper.insert(Fieldsonly(8, 44.55, 66.77))
            mapper.insert(Fieldsonly(9, 88.99, 100.111))

            var answer = mapper.selectDistinct {
                where { fieldsonly.integerfield isEqualTo 5 }
            }
            assertThat(answer).hasSize(1)

            answer = mapper.select {
                allRows()
            }
            assertThat(answer).hasSize(5)
        }
    }

    @Test
    fun testFieldsOnlySelectByExampleNoCriteria() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            mapper.insert(Fieldsonly(5, 11.22, 33.44))
            mapper.insert(Fieldsonly(8, 44.55, 66.77))
            mapper.insert(Fieldsonly(9, 88.99, 100.111))

            val answer = mapper.select { allRows() }
            assertThat(answer).hasSize(3)
        }
    }

    @Test
    fun testFieldsOnlyDelete() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            mapper.insert(Fieldsonly(5, 11.22, 33.44))
            mapper.insert(Fieldsonly(8, 44.55, 66.77))
            mapper.insert(Fieldsonly(9, 88.99, 100.111))

            val rows = mapper.delete {
                where { fieldsonly.integerfield isGreaterThan 5 }
            }
            assertThat(rows).isEqualTo(2)

            val answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)
        }
    }

    @Test
    fun testFieldsOnlyCount() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            mapper.insert(Fieldsonly(5, 11.22, 33.44))
            mapper.insert(Fieldsonly(8, 44.55, 66.77))
            mapper.insert(Fieldsonly(9, 88.99, 100.111))

            var rows = mapper.count {
                where { fieldsonly.integerfield isGreaterThan 5 }
            }
            assertThat(rows).isEqualTo(2)

            rows = mapper.count { allRows() }
            assertThat(rows).isEqualTo(3)
        }
    }

    @Test
    fun testPKOnlyInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            val key = Pkonly(1, 3)
            mapper.insert(key)

            val answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)

            with(answer[0]) {
                assertThat(id).isEqualTo(key.id)
                assertThat(seqNum).isEqualTo(key.seqNum)
            }
        }
    }

    @Test
    fun testPKOnlyDeleteByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insert(Pkonly(1, 3))
            mapper.insert(Pkonly(5, 6))

            var answer = mapper.select { allRows() }
            assertThat(answer).hasSize(2)

            val rows = mapper.deleteByPrimaryKey(5, 6)
            assertThat(rows).isEqualTo(1)

            answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)
        }
    }

    @Test
    fun testPKOnlyDelete() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insert(Pkonly(1, 3))
            mapper.insert(Pkonly(5, 6))
            mapper.insert(Pkonly(7, 8))

            val rows = mapper.delete {
                where { pkonly.id isGreaterThan 4 }
            }
            assertThat(rows).isEqualTo(2)

            val answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)
        }
    }

    @Test
    fun testPKOnlySelect() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insert(Pkonly(1, 3))
            mapper.insert(Pkonly(5, 6))
            mapper.insert(Pkonly(7, 8))

            val answer = mapper.select {
                where { pkonly.id isGreaterThan 4 }
                orderBy(pkonly.id)
            }
            assertThat(answer).hasSize(2)
            assertThat(answer[0].id).isEqualTo(5)
            assertThat(answer[0].seqNum).isEqualTo(6)
            assertThat(answer[1].id).isEqualTo(7)
            assertThat(answer[1].seqNum).isEqualTo(8)
        }
    }

    @Test
    fun testPKOnlySelectByExampleWithMultiInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insertMultiple(Pkonly(1, 3), Pkonly(5, 6), Pkonly(7, 8))

            val answer = mapper.select {
                where { pkonly.id isGreaterThan 4 }
                orderBy(pkonly.id)
            }
            assertThat(answer).hasSize(2)
            assertThat(answer[0].id).isEqualTo(5)
            assertThat(answer[0].seqNum).isEqualTo(6)
            assertThat(answer[1].id).isEqualTo(7)
            assertThat(answer[1].seqNum).isEqualTo(8)
        }
    }

    @Test
    fun testPKOnlySelectByExampleBackwards() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insert(Pkonly(1, 3))
            mapper.insert(Pkonly(5, 6))
            mapper.insert(Pkonly(7, 8))

            val answer = mapper.select {
                where { pkonly.id isGreaterThan 4 }
                orderBy(pkonly.id)
            }

            assertThat(answer).hasSize(2)
            assertThat(answer[0].id).isEqualTo(5)
            assertThat(answer[0].seqNum).isEqualTo(6)
            assertThat(answer[1].id).isEqualTo(7)
            assertThat(answer[1].seqNum).isEqualTo(8)
        }
    }

    @Test
    fun testPKOnlySelectByExampleWithBackwardsResults() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insert(Pkonly(1, 3))
            mapper.insert(Pkonly(5, 6))
            mapper.insert(Pkonly(7, 8))

            val answer = mapper.select {
                where { pkonly.id isGreaterThan 4 }
                orderBy(pkonly.id)
            }

            assertThat(answer).hasSize(2)
            assertThat(answer[0].id).isEqualTo(5)
            assertThat(answer[0].seqNum).isEqualTo(6)
            assertThat(answer[1].id).isEqualTo(7)
            assertThat(answer[1].seqNum).isEqualTo(8)
        }
    }

    @Test
    fun testPKOnlySelectByExampleNoCriteria() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insert(Pkonly(1, 3))
            mapper.insert(Pkonly(5, 6))
            mapper.insert(Pkonly(7, 8))

            val answer = mapper.select { allRows() }
            assertEquals(3, answer.size)
        }
    }

    @Test
    fun testPKOnlyCount() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            mapper.insert(Pkonly(1, 3))
            mapper.insert(Pkonly(5, 6))
            mapper.insert(Pkonly(7, 8))

            var rows = mapper.count {
                where { pkonly.id isGreaterThan 4 }
            }
            assertThat(rows).isEqualTo(2)

            rows = mapper.count { allRows() }
            assertThat(rows).isEqualTo(3)
        }
    }

    @Test
    fun testPKFieldsInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            val record = Pkfields(id2 = 2, id1 = 1, firstname = "Jeff", lastname = "Butler",
                datefield = LocalDate.now(), timefield = LocalTime.of(13, 2, 4),
                timestampfield = LocalDateTime.now(), decimal30field = 3, decimal60field = 6,
                decimal100field = 10L, decimal155field = BigDecimal("15.12345"), stringboolean = true)
            mapper.insert(record)

            val returnedRecord = mapper.selectByPrimaryKey(2, 1)

            assertThat(returnedRecord).isNotNull
            assertThat(returnedRecord).usingRecursiveComparison().ignoringFields("timestampfield").isEqualTo(record)
            assertThat(returnedRecord?.timestampfield).isCloseTo(record.timestampfield, within(1, ChronoUnit.MILLIS))
        }
    }

    @Test
    fun testPKFieldsUpdateByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            val record = Pkfields(2, 1, "Jeff", "Smith")

            mapper.insert(record)

            val updateRecord = record.copy(firstname = "Scott", lastname = "Jones")

            val rows = mapper.updateByPrimaryKey(updateRecord)
            assertThat(rows).isEqualTo(1)

            val record2 = mapper.selectByPrimaryKey(2, 1)

            assertThat(record2).isNotNull
            if (record2 != null) {
                assertEquals(updateRecord.firstname, record2.firstname)
                assertEquals(updateRecord.lastname, record2.lastname)
                assertEquals(updateRecord.id1, record2.id1)
                assertEquals(updateRecord.id2, record2.id2)
            }
        }
    }

    @Test
    fun testPKFieldsUpdateByPrimaryKeySelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            val record = Pkfields(firstname = "Jeff", lastname = "Smith", decimal60field = 5, id1 = 1, id2 = 2)
            mapper.insert(record)

            val rows = mapper.update {
                set(pkfieldstable.firstname) equalTo "Scott"
                set(pkfieldstable.decimal60field) equalTo 4
                where {
                    pkfieldstable.id1 isEqualTo 1
                    and { pkfieldstable.id2 isEqualTo 2 }
                }
            }
            assertThat(rows).isEqualTo(1)

            val returnedRecord = mapper.selectByPrimaryKey(2, 1)

            assertThat(returnedRecord).isNotNull
            if(returnedRecord != null) {
                assertEquals(record.datefield, returnedRecord.datefield)
                assertEquals(record.decimal100field, returnedRecord.decimal100field)
                assertEquals(record.decimal155field, returnedRecord.decimal155field)
                assertEquals(record.decimal30field, returnedRecord.decimal30field)
                assertEquals(4, returnedRecord.decimal60field)
                assertEquals("Scott", returnedRecord.firstname)
                assertEquals(record.id1, returnedRecord.id1)
                assertEquals(record.id2, returnedRecord.id2)
                assertEquals(record.lastname, returnedRecord.lastname)
                assertEquals(record.timefield, returnedRecord.timefield)
                assertEquals(record.timestampfield, returnedRecord.timestampfield)
            }
        }
    }

    @Test
    fun testPKFieldsDeleteByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            val record = Pkfields(id1 = 1, id2 = 2, firstname = "Jeff", lastname = "Smith")
            mapper.insert(record)

            val rows = mapper.deleteByPrimaryKey(2, 1)
            assertThat(rows).isEqualTo(1)

            val answer = mapper.select { allRows() }
            assertThat(answer).isEmpty()
        }
    }

    @Test
    fun testPKFieldsDelete() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Jeff", lastname = "Smith"))
            mapper.insert(Pkfields(id1 = 3, id2 = 4, firstname = "Bob", lastname = "Jones"))

            var answer = mapper.select { allRows() }
            assertThat(answer).hasSize(2)

            val rows = mapper.delete {
                where { pkfieldstable.lastname isLike "J%" }
            }
            assertThat(rows).isEqualTo(1)

            answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)
        }
    }

    @Test
    fun testPKFieldsSelectByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Jeff", lastname = "Smith"))

            val record1 = Pkfields(id1 = 3, id2 = 4, firstname = "Bob", lastname = "Jones")
            mapper.insert(record1)

            val newRecord = mapper.selectByPrimaryKey(4, 3)

            assertThat(newRecord).isNotNull
            if (newRecord != null) {
                assertEquals(record1.firstname, newRecord.firstname)
                assertEquals(record1.lastname, newRecord.lastname)
                assertEquals(record1.id1, newRecord.id1)
                assertEquals(record1.id2, newRecord.id2)
            }
        }
    }

    @Test
    fun testPKFieldsSelectByExampleLike() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble"))

            val answer = mapper.select {
                where { pkfieldstable.firstname isLike "B%" }
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }

            assertThat(answer).hasSize(3)
            assertThat(answer[0].id1).isEqualTo(2)
            assertThat(answer[0].id2).isEqualTo(1)
            assertThat(answer[1].id1).isEqualTo(2)
            assertThat(answer[1].id2).isEqualTo(2)
            assertThat(answer[2].id1).isEqualTo(2)
            assertThat(answer[2].id2).isEqualTo(3)
        }
    }

    @Test
    fun testPKFieldsSelectByExampleNotLike() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble"))

            val answer = mapper.select {
                where { pkfieldstable.firstname isNotLike "B%" }
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }

            assertThat(answer).hasSize(3)
            assertThat(answer[0].id1).isEqualTo(1)
            assertThat(answer[0].id2).isEqualTo(1)
            assertThat(answer[1].id1).isEqualTo(1)
            assertThat(answer[1].id2).isEqualTo(2)
            assertThat(answer[2].id1).isEqualTo(1)
            assertThat(answer[2].id2).isEqualTo(3)
        }
    }

    @Test
    fun testPKFieldsSelectByExampleComplexLike() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble"))

            val answer = mapper.select {
                where {
                    group {
                        pkfieldstable.firstname isLike "B%"
                        and { pkfieldstable.id2 isEqualTo 3 }
                    }
                    or { pkfieldstable.firstname isLike "Wi%" }
                }
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }

            assertThat(answer).hasSize(2)
            assertThat(answer[0].id1).isEqualTo(1)
            assertThat(answer[0].id2).isEqualTo(2)
            assertThat(answer[1].id1).isEqualTo(2)
            assertThat(answer[1].id2).isEqualTo(3)
        }
    }

    @Test
    fun testPKFieldsSelectByExampleIn() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble"))

            val answer = mapper.select {
                where { pkfieldstable.id2.isIn(1, 3) }
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }

            assertThat(answer).hasSize(4)
            assertThat(answer[0].id1).isEqualTo(1)
            assertThat(answer[0].id2).isEqualTo(1)
            assertThat(answer[1].id1).isEqualTo(1)
            assertThat(answer[1].id2).isEqualTo(3)
            assertThat(answer[2].id1).isEqualTo(2)
            assertThat(answer[2].id2).isEqualTo(1)
            assertThat(answer[3].id1).isEqualTo(2)
            assertThat(answer[3].id2).isEqualTo(3)
        }
    }

    @Test
    fun testPKFieldsSelectByExampleBetween() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble"))

            val answer = mapper.select {
                where { pkfieldstable.id2 isBetween 1 and 3 }
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }
            assertThat(answer).hasSize(6)
        }
    }

    @Test
    fun testPKFieldsSelectByExampleNoCriteria() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone"))
            mapper.insert(Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble"))
            mapper.insert(Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble"))

            val answer = mapper.select {
                allRows()
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }

            assertThat(answer).hasSize(6)
        }
    }

    @Test
    fun testPKFieldsSelectByExampleNoCriteriaWithMultiInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            val records = listOf(
                    Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone"),
                    Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone"),
                    Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone"),
                    Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble"),
                    Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble"),
                    Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble"))

            val rowsInserted = mapper.insertMultiple(records)

            assertThat(rowsInserted).isEqualTo(6)

            val answer = mapper.select {
                allRows()
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }

            assertThat(answer).hasSize(6)
        }
    }

    @Test
    fun testPKFieldsSelectByExampleEscapedFields() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 1, firstname = "Fred", lastname = "Flintstone", wierdField = 11))
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Wilma", lastname = "Flintstone", wierdField = 22))
            mapper.insert(Pkfields(id1 = 1, id2 = 3, firstname = "Pebbles", lastname = "Flintstone", wierdField = 33))
            mapper.insert(Pkfields(id1 = 2, id2 = 1, firstname = "Barney", lastname = "Rubble", wierdField = 44))
            mapper.insert(Pkfields(id1 = 2, id2 = 2, firstname = "Betty", lastname = "Rubble", wierdField = 55))
            mapper.insert(Pkfields(id1 = 2, id2 = 3, firstname = "Bamm Bamm", lastname = "Rubble", wierdField = 66))

            val answer = mapper.select {
                where {
                    pkfieldstable.wierdField isLessThan 40
                    and { pkfieldstable.wierdField.isIn(11, 22) }
                }
                orderBy(pkfieldstable.id1, pkfieldstable.id2)
            }

            assertThat(answer).hasSize(2)
        }
    }

    @Test
    fun testPKFieldsCount() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            mapper.insert(Pkfields(id1 = 1, id2 = 2, firstname = "Jeff", lastname = "Smith"))
            mapper.insert(Pkfields(id1 = 3, id2 = 4, firstname = "Bob", lastname = "Jones"))

            var rows = mapper.count {
                where { pkfieldstable.lastname isLike "J%" }
            }

            assertThat(rows).isEqualTo(1)

            rows = mapper.count { allRows() }
            assertThat(rows).isEqualTo(2)
        }
    }

    @Test
    fun testPKBlobsInsert() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            val record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)

            with(answer[0]) {
                assertThat(id).isEqualTo(record.id)
                assertThat(blobsAreEqual(blob1, record.blob1)).isTrue
                assertThat(blobsAreEqual(blob2, record.blob2)).isTrue
            }
        }
    }

    @Test
    fun testPKBlobsUpdateByPrimaryKeyWithBLOBs() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            mapper.insert(Pkblobs(3, generateRandomBlob(), generateRandomBlob()))

            val record1 = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            val rows = mapper.updateByPrimaryKey(record1)
            assertThat(rows).isEqualTo(1)

            val newRecord = mapper.selectByPrimaryKey(3)

            assertThat(newRecord).isNotNull
            assertThat(newRecord!!.id).isEqualTo(record1.id)
            assertThat(blobsAreEqual(newRecord.blob1, record1.blob1)).isTrue
            assertThat(blobsAreEqual(newRecord.blob2, record1.blob2)).isTrue
        }
    }

    @Test
    fun testPKBlobsUpdateByPrimaryKeySelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            val record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val newBlob = generateRandomBlob()
            mapper.update {
                set(pkblobs.blob2) equalTo newBlob
                where { pkblobs.id isEqualTo 3 }
            }

            val returnedRecord = mapper.selectByPrimaryKey(3)

            assertThat(returnedRecord).isNotNull
            assertThat(returnedRecord!!.id).isEqualTo(record.id)
            assertThat(blobsAreEqual(returnedRecord.blob1, record.blob1)).isTrue
            assertThat(blobsAreEqual(returnedRecord.blob2, newBlob)).isTrue
        }
    }

    @Test
    fun testPKBlobsDeleteByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            mapper.insert(Pkblobs(3, generateRandomBlob(), generateRandomBlob()))

            var answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)

            val rows = mapper.deleteByPrimaryKey(3)
            assertThat(rows).isEqualTo(1)

            answer = mapper.select { allRows() }
            assertThat(answer).isEmpty()
        }
    }

    @Test
    fun testPKBlobsDelete() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            mapper.insert(Pkblobs(3, generateRandomBlob(), generateRandomBlob()))
            mapper.insert(Pkblobs(6, generateRandomBlob(), generateRandomBlob()))

            var answer = mapper.select { allRows() }
            assertThat(answer).hasSize(2)

            val rows = mapper.delete { where { pkblobs.id isLessThan 4 } }
            assertThat(rows).isEqualTo(1)

            answer = mapper.select { allRows() }
            assertThat(answer).hasSize(1)
        }
    }

    @Test
    fun testPKBlobsSelectByPrimaryKey() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            val record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val record1 = Pkblobs(6, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record1)

            val newRecord = mapper.selectByPrimaryKey(6)

            assertThat(newRecord).isNotNull
            if (newRecord != null) {
                assertThat(newRecord.id).isEqualTo(record1.id)
                assertThat(blobsAreEqual(newRecord.blob1, record1.blob1)).isTrue
                assertThat(blobsAreEqual(newRecord.blob2, record1.blob2)).isTrue
            }
        }
    }

    @Test
    fun testPKBlobsSelectByExampleWithBlobs() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            var record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Pkblobs(6, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { where { pkblobs.id isGreaterThan 4 } }

            assertEquals(1, answer.size)

            val newRecord = answer[0]
            assertEquals(record.id, newRecord.id)
            assertTrue(blobsAreEqual(record.blob1, newRecord.blob1))
            assertTrue(blobsAreEqual(record.blob2, newRecord.blob2))
        }
    }

    @Test
    fun testPKBlobsSelectByExampleWithMultiInsert() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            val records = mutableListOf<Pkblobs>()

            var record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            records.add(record)

            record = Pkblobs(6, generateRandomBlob(), generateRandomBlob())
            records.add(record)

            val recordsInserted = mapper.insertMultiple(records)
            assertEquals(2, recordsInserted)

            val answer = mapper.select { where { pkblobs.id isGreaterThan 4 } }

            assertEquals(1, answer.size)

            val newRecord = answer[0]
            assertEquals(record.id, newRecord.id)
            assertTrue(blobsAreEqual(record.blob1, newRecord.blob1))
            assertTrue(blobsAreEqual(record.blob2, newRecord.blob2))
        }
    }

    @Test
    fun testPKBlobscount() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            var record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Pkblobs(6, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            var rows = mapper.count { where { pkblobs.id isLessThan 4 } }
            assertEquals(1, rows)

            rows = mapper.count { allRows() }
            assertEquals(2, rows)
        }
    }

    @Test
    fun testPKFieldsBlobsInsert() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            val record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { allRows() }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]
            assertEquals(record.id1, returnedRecord.id1)
            assertEquals(record.id2, returnedRecord.id2)
            assertEquals(record.firstname, returnedRecord.firstname)
            assertEquals(record.lastname, returnedRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, returnedRecord.blob1))
        }
    }

    @Test
    fun testPKFieldsBlobsUpdateByPrimaryKeyWithBLOBs() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            val record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            val updateRecord = Pkfieldsblobs(3, 4, "Scott", "Jones", generateRandomBlob())

            val rows = mapper.updateByPrimaryKey(updateRecord)
            assertEquals(1, rows)

            val newRecord = mapper.selectByPrimaryKey(3, 4)

            assertThat(newRecord).isNotNull
            if (newRecord != null) {
                assertEquals(updateRecord.firstname, newRecord.firstname)
                assertEquals(updateRecord.lastname, newRecord.lastname)
                assertEquals(record.id1, newRecord.id1)
                assertEquals(record.id2, newRecord.id2)
                assertTrue(blobsAreEqual(updateRecord.blob1, newRecord.blob1))
            }
        }
    }

    @Test
    fun testPKFieldsBlobsUpdateByPrimaryKeySelective() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            val record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            val rows = mapper.update {
                set(pkfieldsblobs.lastname) equalTo "Jones"
                where {
                    pkfieldsblobs.id1 isEqualTo 3
                    and { pkfieldsblobs.id2 isEqualTo 4 }
                }
            }
            assertEquals(1, rows)

            val returnedRecord = mapper.selectByPrimaryKey(3, 4)

            assertThat(returnedRecord).isNotNull
            assertEquals(record.firstname, returnedRecord!!.firstname)
            assertEquals("Jones", returnedRecord.lastname)
            assertEquals(record.id1, returnedRecord.id1)
            assertEquals(record.id2, returnedRecord.id2)
            assertTrue(blobsAreEqual(record.blob1, returnedRecord.blob1))
        }
    }

    @Test
    fun testPKFieldsBlobsDeleteByPrimaryKey() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record)

            var answer = mapper.select { allRows() }
            assertEquals(2, answer.size)

            val rows = mapper.deleteByPrimaryKey(5, 6)
            assertEquals(1, rows)

            answer = mapper.select { allRows() }
            assertEquals(1, answer.size)
        }
    }

    @Test
    fun testPKFieldsBlobsdelete() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record)

            var answer = mapper.select { allRows() }
            assertEquals(2, answer.size)

            val rows = mapper.delete { where { pkfieldsblobs.id1 isNotEqualTo 3 } }
            assertEquals(1, rows)

            answer = mapper.select { allRows() }
            assertEquals(1, answer.size)
        }
    }

    @Test
    fun testPKFieldsBlobsSelectByPrimaryKey() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            val record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            val record1 = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record1)

            val answer = mapper.select { allRows() }
            assertEquals(2, answer.size)

            val newRecord = mapper.selectByPrimaryKey(5, 6)

            assertThat(newRecord).isNotNull
            if (newRecord != null) {
                assertEquals(record1.id1, newRecord.id1)
                assertEquals(record1.id2, newRecord.id2)
                assertEquals(record1.firstname, newRecord.firstname)
                assertEquals(record1.lastname, newRecord.lastname)
                assertTrue(blobsAreEqual(record1.blob1, newRecord.blob1))
            }
        }
    }

    @Test
    fun testPKFieldsBlobsSelectByExampleWithBlobs() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { where { pkfieldsblobs.id2 isEqualTo 6 } }
            assertEquals(1, answer.size)

            val newRecord = answer[0]
            assertEquals(record.id1, newRecord.id1)
            assertEquals(record.id2, newRecord.id2)
            assertEquals(record.firstname, newRecord.firstname)
            assertEquals(record.lastname, newRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, newRecord.blob1))
        }
    }

    @Test
    fun testPKFieldsBlobsSelectByExampleWithMultiInsert() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            val records = mutableListOf<Pkfieldsblobs>()

            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            records.add(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            records.add(record)

            val rowsInserted = mapper.insertMultiple(records)
            assertEquals(2, rowsInserted)

            val answer = mapper.select { where { pkfieldsblobs.id2 isEqualTo 6 } }
            assertEquals(1, answer.size)

            val newRecord = answer[0]
            assertEquals(record.id1, newRecord.id1)
            assertEquals(record.id2, newRecord.id2)
            assertEquals(record.firstname, newRecord.firstname)
            assertEquals(record.lastname, newRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, newRecord.blob1))
        }
    }

    @Test
    fun testPKFieldsBlobsSelectByExampleWithBlobsNoCriteria() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { allRows() }
            assertEquals(2, answer.size)
        }
    }

    @Test
    fun testFieldsBlobsInsert() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(FieldsblobsMapper::class.java)
            val record = Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { allRows() }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]
            assertEquals(record.firstname, returnedRecord.firstname)
            assertEquals(record.lastname, returnedRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, returnedRecord.blob1))
            assertTrue(blobsAreEqual(record.blob2, returnedRecord.blob2))
        }
    }

    @Test
    fun testFieldsBlobsdelete() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(FieldsblobsMapper::class.java)
            var record = Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            var answer = mapper.select { allRows() }
            assertEquals(2, answer.size)

            val rows = mapper.delete { where { fieldsblobs.firstname isLike "S%" } }
            assertEquals(1, rows)

            answer = mapper.select { allRows() }
            assertEquals(1, answer.size)
        }
    }

    @Test
    fun testFieldsBlobsSelectByExampleWithBlobs() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(FieldsblobsMapper::class.java)
            var record = Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { where { fieldsblobs.firstname isLike "S%" } }
            assertEquals(1, answer.size)

            val newRecord = answer[0]
            assertEquals(record.firstname, newRecord.firstname)
            assertEquals(record.lastname, newRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, newRecord.blob1))
            assertTrue(blobsAreEqual(record.blob2, newRecord.blob2))
        }
    }

    @Test
    fun testFieldsBlobsSelectByExampleWithMultiInsert() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(FieldsblobsMapper::class.java)
            val records = mutableListOf<Fieldsblobs>()

            var record = Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob())
            records.add(record)

            record = Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob())
            records.add(record)

            val rowsInserted = mapper.insertMultiple(records)
            assertEquals(2, rowsInserted)

            val answer = mapper.select { where { fieldsblobs.firstname isLike "S%" } }
            assertEquals(1, answer.size)

            val newRecord = answer[0]
            assertEquals(record.firstname, newRecord.firstname)
            assertEquals(record.lastname, newRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, newRecord.blob1))
            assertTrue(blobsAreEqual(record.blob2, newRecord.blob2))
        }
    }

    @Test
    fun testFieldsBlobsSelectByExampleWithBlobsNoCriteria() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(FieldsblobsMapper::class.java)
            var record = Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val answer = mapper.select { allRows() }
            assertEquals(2, answer.size)
        }
    }

    @Test
    fun testPKFieldsBlobscount() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record)

            var rows = mapper.count { where { pkfieldsblobs.id1 isNotEqualTo 3 } }
            assertEquals(1, rows)

            rows = mapper.count { allRows() }
            assertEquals(2, rows)
        }
    }

    @Test
    fun testAwfulTableInsert() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            val record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field", active = true, active1 = false,
                active2 = byteArrayOf(-128, 127))

            mapper.insert(record)
            val generatedCustomerId = record.customerId
            assertEquals(57, generatedCustomerId!!)

            val returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(generatedCustomerId, returnedRecord.customerId)
                assertEquals(record.eMail, returnedRecord.eMail)
                assertEquals(record.emailaddress, returnedRecord.emailaddress)
                assertEquals(record.firstFirstName, returnedRecord.firstFirstName)
                assertEquals(record.from, returnedRecord.from)
                assertEquals(record.id1!!, returnedRecord.id1)
                assertEquals(record.id2!!, returnedRecord.id2)
                assertEquals(record.id5!!, returnedRecord.id5)
                assertEquals(record.id6!!, returnedRecord.id6)
                assertEquals(record.id7!!, returnedRecord.id7)
                assertEquals(record.secondFirstName, returnedRecord.secondFirstName)
                assertEquals(record.thirdFirstName, returnedRecord.thirdFirstName)
                assertThat(returnedRecord.active).isTrue
                assertThat(returnedRecord.active1).isFalse
                assertEquals(3, returnedRecord.active2!!.size)
                assertEquals(-128, returnedRecord.active2!![0])
                assertEquals(127, returnedRecord.active2!![1])
                assertEquals(0, returnedRecord.active2!![2])
            }
        }
    }

    @Test
    fun testAwfulTableInsertSelective() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            val record = AwfulTable(secondFirstName = "fred2", thirdFirstName = "fred3", eMail = "fred@fred.com",
                id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7, emailaddress = "alscfred@fred.com", from = "from field")

            mapper.insertSelective(record)
            val generatedCustomerId = record.customerId
            assertEquals(57, generatedCustomerId!!)

            val returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertThat(returnedRecord.customerId).isEqualTo(generatedCustomerId)
                assertThat(returnedRecord.eMail).isEqualTo(record.eMail)
                assertThat(returnedRecord.emailaddress).isEqualTo(record.emailaddress)
                assertThat(returnedRecord.firstFirstName).isEqualTo("Mabel")
                assertThat(returnedRecord.from).isEqualTo(record.from)
                assertThat(returnedRecord.id1).isEqualTo(record.id1!!)
                assertThat(returnedRecord.id2).isEqualTo(record.id2!!)
                assertThat(returnedRecord.id5).isEqualTo(record.id5!!)
                assertThat(returnedRecord.id6).isEqualTo(record.id6!!)
                assertThat(returnedRecord.id7).isEqualTo(record.id7!!)
                assertThat(returnedRecord.secondFirstName).isEqualTo(record.secondFirstName)
                assertThat(returnedRecord.thirdFirstName).isEqualTo(record.thirdFirstName)
            }
        }
    }

    @Test
    fun testAwfulTableUpdateByPrimaryKey() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            val record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")

            mapper.insert(record)
            val generatedCustomerId = record.customerId!!

            val record2 = record.copy(id1 = 11, id2 = 22)

            val rows = mapper.updateByPrimaryKey(record2)
            assertEquals(1, rows)

            val returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId)

            assertThat(returnedRecord).isNotNull
            assertThat(returnedRecord).usingRecursiveComparison().isEqualTo(record2)
        }
    }

    @Test
    fun testAwfulTableUpdateByPrimaryKeySelective() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            val record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)
            val generatedCustomerId = record.customerId!!

            val rows = mapper.update {
                set(awfulTable.id1) equalTo 11
                set(awfulTable.id2) equalTo 22
                where {
                    awfulTable.customerId isEqualTo generatedCustomerId
                }
            }
            assertEquals(1, rows)

            val returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(generatedCustomerId, returnedRecord.customerId)
                assertEquals(record.eMail, returnedRecord.eMail)
                assertEquals(record.emailaddress, returnedRecord.emailaddress)
                assertEquals(record.firstFirstName, returnedRecord.firstFirstName)
                assertEquals(11, returnedRecord.id1)
                assertEquals(22, returnedRecord.id2)
                assertEquals(record.id5!!, returnedRecord.id5)
                assertEquals(record.id6!!, returnedRecord.id6)
                assertEquals(record.id7!!, returnedRecord.id7)
                assertEquals(record.secondFirstName, returnedRecord.secondFirstName)
                assertEquals(record.thirdFirstName, returnedRecord.thirdFirstName)
            }
        }
    }

    @Test
    fun testAwfulTableDeleteByPrimaryKey() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            val record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")

            mapper.insert(record)
            val generatedCustomerId = record.customerId

            val rows = mapper.deleteByPrimaryKey(generatedCustomerId!!)
            assertEquals(1, rows)

            val answer = mapper.select { allRows() }
            assertEquals(0, answer.size)
        }
    }

    @Test
    fun testAwfulTableDelete() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            var answer = mapper.select { allRows() }
            assertEquals(2, answer.size)

            val rows = mapper.delete { where { awfulTable.eMail isLike "fred@%" } }
            assertEquals(1, rows)

            answer = mapper.select { allRows() }
            assertEquals(1, answer.size)
        }
    }

    @Test
    fun testAwfulTableSelectByPrimaryKey() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            val record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")

            mapper.insert(record)

            val record1 = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record1)
            val generatedKey = record1.customerId!!

            val returnedRecord = mapper.selectByPrimaryKey(generatedKey)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(record1.customerId!!, returnedRecord.customerId)
                assertEquals(record1.eMail, returnedRecord.eMail)
                assertEquals(record1.emailaddress, returnedRecord.emailaddress)
                assertEquals(record1.firstFirstName, returnedRecord.firstFirstName)
                assertEquals(record1.from, returnedRecord.from)
                assertEquals(record1.id1!!, returnedRecord.id1)
                assertEquals(record1.id2!!, returnedRecord.id2)
                assertEquals(record1.id5!!, returnedRecord.id5)
                assertEquals(record1.id6!!, returnedRecord.id6)
                assertEquals(record1.id7!!, returnedRecord.id7)
                assertEquals(record1.secondFirstName, returnedRecord.secondFirstName)
                assertEquals(record1.thirdFirstName, returnedRecord.thirdFirstName)
            }
        }
    }

    @Test
    fun testAwfulTableSelectByExampleLike() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Pebbles", secondFirstName = "pebbles2", thirdFirstName = "pebbles3",
                eMail = "pebbles@pebbles.com", id1 = 111, id2 = 222, id5 = 555, id6 = 666, id7 = 777,
                emailaddress = "alsopebbles@pebbles.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Betty", secondFirstName = "betty2", thirdFirstName = "betty3",
                eMail = "betty@betty.com", id1 = 11111, id2 = 22222, id5 = 55555, id6 = 66666, id7 = 77777,
                emailaddress = "alsobetty@betty.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "BammBamm", secondFirstName = "bammbamm2", thirdFirstName = "bammbamm3",
                eMail = "bammbamm@bammbamm.com", id1 = 111111, id2 = 222222, id5 = 555555, id6 = 666666, id7 = 777777,
                emailaddress = "alsobammbamm@bammbamm.com", from = "from field")
            mapper.insert(record)

            val answer = mapper.select {
                where { awfulTable.firstFirstName isLike "B%" }
                orderBy(awfulTable.customerId)
            }
            assertEquals(3, answer.size)
            var returnedRecord = answer[0]
            assertEquals(1111, returnedRecord.id1)
            assertEquals(2222, returnedRecord.id2)
            assertEquals(60, returnedRecord.customerId)

            returnedRecord = answer[1]
            assertEquals(11111, returnedRecord.id1)
            assertEquals(22222, returnedRecord.id2)
            assertEquals(61, returnedRecord.customerId)

            returnedRecord = answer[2]
            assertEquals(111111, returnedRecord.id1)
            assertEquals(222222, returnedRecord.id2)
            assertEquals(62, returnedRecord.customerId)
        }
    }

    @Test
    fun testAwfulTableSelectByExampleLikeWithMultiInsert() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            val records = mutableListOf<AwfulTable>()

            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            records.add(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            records.add(record)

            record = AwfulTable(firstFirstName = "Pebbles", secondFirstName = "pebbles2", thirdFirstName = "pebbles3",
                eMail = "pebbles@pebbles.com", id1 = 111, id2 = 222, id5 = 555, id6 = 666, id7 = 777,
                emailaddress = "alsopebbles@pebbles.com", from = "from field")
            records.add(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            records.add(record)

            record = AwfulTable(firstFirstName = "Betty", secondFirstName = "betty2", thirdFirstName = "betty3",
                eMail = "betty@betty.com", id1 = 11111, id2 = 22222, id5 = 55555, id6 = 66666, id7 = 77777,
                emailaddress = "alsobetty@betty.com", from = "from field")
            records.add(record)

            record = AwfulTable(firstFirstName = "BammBamm", secondFirstName = "bammbamm2", thirdFirstName = "bammbamm3",
                eMail = "bammbamm@bammbamm.com", id1 = 111111, id2 = 222222, id5 = 555555, id6 = 666666, id7 = 777777,
                emailaddress = "alsobammbamm@bammbamm.com", from = "from field")
            records.add(record)

            val rowsInserted = mapper.insertMultiple(records)
            assertEquals(6, rowsInserted)

            // check generated keys
            assertEquals(57, records[0].customerId)
            assertEquals(58, records[1].customerId)
            assertEquals(59, records[2].customerId)
            assertEquals(60, records[3].customerId)
            assertEquals(61, records[4].customerId)
            assertEquals(62, records[5].customerId)

            val answer = mapper.select {
                where { awfulTable.firstFirstName isLike "B%" }
                orderBy(awfulTable.customerId)
            }
            assertEquals(3, answer.size)
            var returnedRecord = answer[0]
            assertEquals(1111, returnedRecord.id1)
            assertEquals(2222, returnedRecord.id2)
            assertEquals(60, returnedRecord.customerId)

            returnedRecord = answer[1]
            assertEquals(11111, returnedRecord.id1)
            assertEquals(22222, returnedRecord.id2)
            assertEquals(61, returnedRecord.customerId)

            returnedRecord = answer[2]
            assertEquals(111111, returnedRecord.id1)
            assertEquals(222222, returnedRecord.id2)
            assertEquals(62, returnedRecord.customerId)
        }
    }

    @Test
    fun testAwfulTableSelectByExampleNotLike() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Pebbles", secondFirstName = "pebbles2", thirdFirstName = "pebbles3",
                eMail = "pebbles@pebbles.com", id1 = 111, id2 = 222, id5 = 555, id6 = 666, id7 = 777,
                emailaddress = "alsopebbles@pebbles.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Betty", secondFirstName = "betty2", thirdFirstName = "betty3",
                eMail = "betty@betty.com", id1 = 11111, id2 = 22222, id5 = 55555, id6 = 66666, id7 = 77777,
                emailaddress = "alsobetty@betty.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "BammBamm", secondFirstName = "bammbamm2", thirdFirstName = "bammbamm3",
                eMail = "bammbamm@bammbamm.com", id1 = 111111, id2 = 222222, id5 = 555555, id6 = 666666, id7 = 777777,
                emailaddress = "alsobammbamm@bammbamm.com", from = "from field")
            mapper.insert(record)

            val answer = mapper.select {
                where { awfulTable.firstFirstName isNotLike "B%" }
                orderBy(awfulTable.customerId)
            }
            assertEquals(3, answer.size)
            var returnedRecord = answer[0]
            assertEquals(1, returnedRecord.id1)
            assertEquals(2, returnedRecord.id2)
            returnedRecord = answer[1]
            assertEquals(11, returnedRecord.id1)
            assertEquals(22, returnedRecord.id2)
            returnedRecord = answer[2]
            assertEquals(111, returnedRecord.id1)
            assertEquals(222, returnedRecord.id2)
        }
    }

    @Test
    fun testAwfulTableSelectByExampleComplexLike() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Pebbles", secondFirstName = "pebbles2", thirdFirstName = "pebbles3",
                eMail = "pebbles@pebbles.com", id1 = 111, id2 = 222, id5 = 555, id6 = 666, id7 = 777,
                emailaddress = "alsopebbles@pebbles.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Betty", secondFirstName = "betty2", thirdFirstName = "betty3",
                eMail = "betty@betty.com", id1 = 11111, id2 = 22222, id5 = 55555, id6 = 66666, id7 = 77777,
                emailaddress = "alsobetty@betty.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "BammBamm", secondFirstName = "bammbamm2", thirdFirstName = "bammbamm3",
                eMail = "bammbamm@bammbamm.com", id1 = 111111, id2 = 222222, id5 = 555555, id6 = 666666, id7 = 777777,
                emailaddress = "alsobammbamm@bammbamm.com", from = "from field")
            mapper.insert(record)

            val answer = mapper.select {
                where {
                    group {
                        awfulTable.firstFirstName isLike "B%"
                        and { awfulTable.id2 isEqualTo 222222 }
                    }
                    or { awfulTable.firstFirstName isLike "Wi%" }
                }
                orderBy(awfulTable.customerId)
            }

            assertEquals(2, answer.size)
            var returnedRecord = answer[0]
            assertEquals(11, returnedRecord.id1)
            assertEquals(22, returnedRecord.id2)
            returnedRecord = answer[1]
            assertEquals(111111, returnedRecord.id1)
            assertEquals(222222, returnedRecord.id2)
        }
    }

    @Test
    fun testAwfulTableSelectByExampleIn() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Pebbles", secondFirstName = "pebbles2", thirdFirstName = "pebbles3",
                eMail = "pebbles@pebbles.com", id1 = 111, id2 = 222, id5 = 555, id6 = 666, id7 = 777,
                emailaddress = "alsopebbles@pebbles.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Betty", secondFirstName = "betty2", thirdFirstName = "betty3",
                eMail = "betty@betty.com", id1 = 11111, id2 = 22222, id5 = 55555, id6 = 66666, id7 = 77777,
                emailaddress = "alsobetty@betty.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "BammBamm", secondFirstName = "bammbamm2", thirdFirstName = "bammbamm3",
                eMail = "bammbamm@bammbamm.com", id1 = 111111, id2 = 222222, id5 = 555555, id6 = 666666, id7 = 777777,
                emailaddress = "alsobammbamm@bammbamm.com", from = "from field")
            mapper.insert(record)

            val answer = mapper.select {
                where { awfulTable.id1.isIn(1, 11) }
                orderBy(awfulTable.customerId)
            }

            assertEquals(2, answer.size)
            var returnedRecord = answer[0]
            assertEquals(1, returnedRecord.id1)
            assertEquals(2, returnedRecord.id2)
            returnedRecord = answer[1]
            assertEquals(11, returnedRecord.id1)
            assertEquals(22, returnedRecord.id2)
        }
    }

    @Test
    fun testAwfulTableSelectByExampleBetween() {
        openSession().use { sqlSession ->

            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Pebbles", secondFirstName = "pebbles2", thirdFirstName = "pebbles3",
                eMail = "pebbles@pebbles.com", id1 = 111, id2 = 222, id5 = 555, id6 = 666, id7 = 777,
                emailaddress = "alsopebbles@pebbles.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Betty", secondFirstName = "betty2", thirdFirstName = "betty3",
                eMail = "betty@betty.com", id1 = 11111, id2 = 22222, id5 = 55555, id6 = 66666, id7 = 77777,
                emailaddress = "alsobetty@betty.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "BammBamm", secondFirstName = "bammbamm2", thirdFirstName = "bammbamm3",
                eMail = "bammbamm@bammbamm.com", id1 = 111111, id2 = 222222, id5 = 555555, id6 = 666666, id7 = 777777,
                emailaddress = "alsobammbamm@bammbamm.com", from = "from field")
            mapper.insert(record)

            val answer = mapper.select { where { awfulTable.id1 isBetween 1 and 1000 } }
            assertEquals(3, answer.size)
        }
    }

    @Test
    fun testAwfulTableSelectByExampleNoCriteria() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Pebbles", secondFirstName = "pebbles2", thirdFirstName = "pebbles3",
                eMail = "pebbles@pebbles.com", id1 = 111, id2 = 222, id5 = 555, id6 = 666, id7 = 777,
                emailaddress = "alsopebbles@pebbles.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Betty", secondFirstName = "betty2", thirdFirstName = "betty3",
                eMail = "betty@betty.com", id1 = 11111, id2 = 22222, id5 = 55555, id6 = 66666, id7 = 77777,
                emailaddress = "alsobetty@betty.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "BammBamm", secondFirstName = "bammbamm2", thirdFirstName = "bammbamm3",
                eMail = "bammbamm@bammbamm.com", id1 = 111111, id2 = 222222, id5 = 555555, id6 = 666666, id7 = 777777,
                emailaddress = "alsobammbamm@bammbamm.com", from = "from field")
            mapper.insert(record)

            val answer = mapper.select {
                allRows()
                orderBy(awfulTable.customerId.descending())
            }

            assertEquals(6, answer.size)
            var returnedRecord = answer[0]
            assertEquals(111111, returnedRecord.id1)
            returnedRecord = answer[1]
            assertEquals(11111, returnedRecord.id1)
            returnedRecord = answer[2]
            assertEquals(1111, returnedRecord.id1)
            returnedRecord = answer[3]
            assertEquals(111, returnedRecord.id1)
            returnedRecord = answer[4]
            assertEquals(11, returnedRecord.id1)
            returnedRecord = answer[5]
            assertEquals(1, returnedRecord.id1)
        }
    }

    @Test
    fun testAwfulTablecount() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Wilma", secondFirstName = "wilma2", thirdFirstName = "wilma3",
                eMail = "wilma@wilma.com", id1 = 11, id2 = 22, id5 = 55, id6 = 66, id7 = 77,
                emailaddress = "alsowilma@wilma.com", from = "from field")
            mapper.insert(record)

            var rows = mapper.count { where { awfulTable.eMail isLike "fred@%" } }
            assertEquals(1, rows)

            rows = mapper.count { allRows() }
            assertEquals(2, rows)
        }
    }

    @Test
    fun testTranslationTable() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(TranslationMapper::class.java)

            val t = Translation(1, "Spanish")
            mapper.insert(t)

            val t1 = Translation(2, "French")
            mapper.insert(t1)

            var returnedRecord = mapper.selectByPrimaryKey(2)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(t1.id, returnedRecord.id)
                assertEquals(t1.translation, returnedRecord.translation)
            }

            val t2 = t1.copy(translation = "Italian")
            mapper.updateByPrimaryKey(t2)

            returnedRecord = mapper.selectByPrimaryKey(2)
            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(t2.id, returnedRecord.id)
                assertEquals(t2.translation, returnedRecord.translation)
            }
        }
    }

    @Test
    fun testIdTable() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(IdMapper::class.java)

            val id = Id(1, "Spanish")
            mapper.insert(id)

            val id1 = Id(2, "French")
            mapper.insert(id1)

            var returnedRecord = mapper.selectByPrimaryKey(2)
            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(id1.id, returnedRecord.id)
                assertEquals(id1.description, returnedRecord.description)
            }

            val id3 = id1.copy(description = "Italian")
            mapper.updateByPrimaryKey(id3)

            returnedRecord = mapper.selectByPrimaryKey(2)

            assertThat(returnedRecord).isNotNull
            if (returnedRecord != null) {
                assertEquals(id3.id, returnedRecord.id)
                assertEquals(id3.description, returnedRecord.description)
            }
        }
    }
}
