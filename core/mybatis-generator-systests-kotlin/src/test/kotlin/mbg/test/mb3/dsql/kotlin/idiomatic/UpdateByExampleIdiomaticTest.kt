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
import mbg.test.mb3.generated.dsql.kotlin.idiomatic.model.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Jeff Butler
 */
class UpdateByExampleIdiomaticTest : AbstractIdiomaticTest() {

    @Test
    fun testFieldsOnlyUpdateByExampleSelective() {
        openSession().use { sqlSession ->
            val offset = Offset.offset(0.001)
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            mapper.insert(Fieldsonly(5, 11.22, 33.44))
            mapper.insert(Fieldsonly(8, 44.55, 66.77))
            mapper.insert(Fieldsonly(9, 88.88, 100.11))

            val rows = mapper.update {
                set(fieldsonly.doublefield) equalTo 99.0
                where { fieldsonly.integerfield isGreaterThan 5 }
            }

            assertThat(rows).isEqualTo(2)

            var answer = mapper.select {
                where { fieldsonly.integerfield isEqualTo 5 }
            }

            assertThat(answer).hasSize(1)
            with(answer[0]) {
                assertThat(doublefield).isEqualTo(11.22, offset)
                assertThat(floatfield).isEqualTo(33.44, offset)
                assertThat(integerfield).isEqualTo(5)
            }

            answer = mapper.select {
                where { fieldsonly.integerfield isEqualTo 8 }
            }

            assertThat(answer).hasSize(1)
            with(answer[0]) {
                assertThat(doublefield).isEqualTo(99.0, offset)
                assertThat(floatfield).isEqualTo(66.77, offset)
                assertThat(integerfield).isEqualTo(8)

            }

            answer = mapper.select {
                where { fieldsonly.integerfield isEqualTo 9 }
            }
            assertThat(answer).hasSize(1)
            with(answer[0]) {
                assertThat(doublefield).isEqualTo(99.0, offset)
                assertThat(floatfield).isEqualTo(100.11, offset)
                assertThat(integerfield).isEqualTo(9)
            }
        }
    }

    @Test
    fun testFieldsOnlyUpdateByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsonlyMapper::class.java)
            var record = Fieldsonly(5, 11.22, 33.44)
            mapper.insert(record)

            record = Fieldsonly(8, 44.55, 66.77)
            mapper.insert(record)

            record = Fieldsonly(9, 88.99, 100.111)
            mapper.insert(record)

            val updateRecord = Fieldsonly(integerfield = 22)

            val rows = mapper.update {
                updateAllColumns(updateRecord)
                where { fieldsonly.integerfield isEqualTo 5 }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { fieldsonly.integerfield isEqualTo 22 } }
            assertEquals(1, answer.size)
            record = answer[0]
            assertNull(record.doublefield)
            assertNull(record.floatfield)
            assertEquals(record.integerfield!!, 22)
        }
    }

    @Test
    fun testPKOnlyUpdateByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkonlyMapper::class.java)
            var key = Pkonly(1, 3)
            mapper.insert(key)

            key = Pkonly(5, 6)
            mapper.insert(key)

            key = Pkonly(7, 8)
            mapper.insert(key)

            val updateKey = Pkonly(22, 3)

            val rows = mapper.update {
                updateAllColumns(updateKey)
                where { pkonly.id isEqualTo 7 }
            }
            assertEquals(1, rows)

            val returnedRows = mapper.count {
                where {
                    pkonly.id isEqualTo 22
                    and { pkonly.seqNum isEqualTo 3 }
                }
            }
            assertEquals(1, returnedRows)
        }
    }

    @Test
    fun testPKFieldsUpdateByExampleSelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            var record = Pkfields(2, 1, firstname = "Jeff", lastname = "Smith")
            mapper.insert(record)

            record = Pkfields(4, 3, firstname = "Bob", lastname = "Jones")
            mapper.insert(record)

            val rows = mapper.update {
                set(pkfieldstable.firstname) equalTo "Fred"
                where { pkfieldstable.lastname isLike "J%" }
            }
            assertEquals(1, rows)

            val returnedRows = mapper.count {
                where {
                    pkfieldstable.firstname isEqualTo "Fred"
                    and { pkfieldstable.lastname isEqualTo "Jones" }
                    and { pkfieldstable.id1 isEqualTo 3 }
                    and { pkfieldstable.id2 isEqualTo 4 }
                }
            }
            assertEquals(1, returnedRows)
        }
    }

    @Test
    fun testPKFieldsUpdateByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsMapper::class.java)
            var record = Pkfields(2, 1, firstname = "Jeff", lastname = "Smith")
            mapper.insert(record)

            record = Pkfields(4, 3, firstname = "Bob", lastname = "Jones")

            mapper.insert(record)

            val updateRecord = Pkfields(id1 = 3, id2 = 4, firstname = "Fred")

            val rows = mapper.update {
                updateAllColumns(updateRecord)
                where {
                    pkfieldstable.id1 isEqualTo 3
                    and { pkfieldstable.id2 isEqualTo 4 }
                }
            }
            assertEquals(1, rows)

            val returnedRows = mapper.count {
                where {
                    pkfieldstable.firstname isEqualTo "Fred"
                    and { pkfieldstable.id1 isEqualTo 3 }
                    and { pkfieldstable.id2 isEqualTo 4 }
                }
            }
            assertEquals(1, returnedRows)
        }
    }

    @Test
    fun testPKBlobsUpdateByExampleSelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            var record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Pkblobs(6, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val newBlob = generateRandomBlob()

            val rows = mapper.update {
                set(pkblobs.blob1) equalTo newBlob
                where { pkblobs.id isGreaterThan 4 }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { pkblobs.id isGreaterThan 4 } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(6, returnedRecord.id)
            assertTrue(blobsAreEqual(newBlob, returnedRecord.blob1))
            assertTrue(blobsAreEqual(record.blob2, returnedRecord.blob2))
        }
    }

    @Test
    fun testPKBlobsUpdateByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkblobsMapper::class.java)
            var record = Pkblobs(3, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Pkblobs(6, generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val newRecord = Pkblobs(id = 8)

            val rows = mapper.update {
                updateAllColumns(newRecord)
                where { pkblobs.id isGreaterThan 4 }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { pkblobs.id isGreaterThan 4 } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(8, returnedRecord.id)
            assertNull(returnedRecord.blob1)
            assertNull(returnedRecord.blob2)
        }
    }

    @Test
    fun testPKFieldsBlobsUpdateByExampleSelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record)

            val rows = mapper.update {
                set(pkfieldsblobs.firstname) equalTo "Fred"
                where { pkfieldsblobs.id1 isNotEqualTo 3 }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { pkfieldsblobs.id1 isNotEqualTo 3 } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(record.id1, returnedRecord.id1)
            assertEquals(record.id2, returnedRecord.id2)
            assertEquals("Fred", returnedRecord.firstname)
            assertEquals(record.lastname, returnedRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, returnedRecord.blob1))
        }
    }

    @Test
    fun testPKFieldsBlobsUpdateByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(PkfieldsblobsMapper::class.java)
            var record = Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob())
            mapper.insert(record)

            record = Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob())
            mapper.insert(record)

            val newRecord = Pkfieldsblobs(3, 8, "Fred")

            val rows = mapper.update {
                updateAllColumns(newRecord)
                where { pkfieldsblobs.id1 isEqualTo 3 }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { pkfieldsblobs.id1 isEqualTo 3 } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(newRecord.id1, returnedRecord.id1)
            assertEquals(newRecord.id2, returnedRecord.id2)
            assertEquals(newRecord.firstname, returnedRecord.firstname)
            assertNull(returnedRecord.lastname)
            assertNull(returnedRecord.blob1)
        }
    }

    @Test
    fun testFieldsBlobsUpdateByExampleSelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsblobsMapper::class.java)
            var record = Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val rows = mapper.update {
                set(fieldsblobs.lastname) equalTo "Doe"
                where { fieldsblobs.firstname isLike "S%" }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { fieldsblobs.firstname isLike "S%" } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(record.firstname, returnedRecord.firstname)
            assertEquals("Doe", returnedRecord.lastname)
            assertTrue(blobsAreEqual(record.blob1, returnedRecord.blob1))
            assertTrue(blobsAreEqual(record.blob2, returnedRecord.blob2))
        }
    }

    @Test
    fun testFieldsBlobsUpdateByExample() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(FieldsblobsMapper::class.java)
            var record = Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            record = Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob())
            mapper.insert(record)

            val newRecord = Fieldsblobs("Scott", "Doe")

            val rows = mapper.update {
                updateAllColumns(newRecord)
                where { fieldsblobs.firstname isLike "S%" }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { fieldsblobs.firstname isLike "S%" } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(newRecord.firstname, returnedRecord.firstname)
            assertEquals(newRecord.lastname, returnedRecord.lastname)
            assertNull(returnedRecord.blob1)
            assertNull(returnedRecord.blob2)
        }
    }

    @Test
    fun testAwfulTableUpdateByExampleSelective() {
        openSession().use { sqlSession ->
            val mapper = sqlSession.getMapper(AwfulTableMapper::class.java)
            var record = AwfulTable(firstFirstName = "Fred", secondFirstName = "fred2", thirdFirstName = "fred3",
                eMail = "fred@fred.com", id1 = 1, id2 = 2, id5 = 5, id6 = 6, id7 = 7,
                emailaddress = "alsofred@fred.com", from = "from field")
            mapper.insert(record)

            record = AwfulTable(firstFirstName = "Barney", secondFirstName = "barney2", thirdFirstName = "barney3",
                eMail = "barney@barney.com", id1 = 1111, id2 = 2222, id5 = 5555, id6 = 6666, id7 = 7777,
                emailaddress = "alsobarney@barney.com", from = "from field")
            mapper.insert(record)

            val rows = mapper.update {
                set(awfulTable.firstFirstName) equalTo "Alonzo"
                where { awfulTable.eMail isLike "barney@%" }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { awfulTable.eMail isLike "barney@%" } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(record.customerId, returnedRecord.customerId)
            assertEquals(record.eMail, returnedRecord.eMail)
            assertEquals(record.emailaddress, returnedRecord.emailaddress)
            assertEquals("Alonzo", returnedRecord.firstFirstName)
            assertEquals(record.from, returnedRecord.from)
            assertEquals(record.id1!!, returnedRecord.id1)
            assertEquals(record.id2!!, returnedRecord.id2)
            assertEquals(record.id5!!, returnedRecord.id5)
            assertEquals(record.id6!!, returnedRecord.id6)
            assertEquals(record.id7!!, returnedRecord.id7)
            assertEquals(record.secondFirstName, returnedRecord.secondFirstName)
            assertEquals(record.thirdFirstName, returnedRecord.thirdFirstName)
        }
    }

    @Test
    fun testAwfulTableUpdateByExample() {
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

            val newRecord = AwfulTable(customerId = 57, firstFirstName = "Alonzo", id1 = 111, id2 = 222, id5 = 555,
                id6 = 666, id7 = 777)

            val rows = mapper.update {
                updateAllColumns(newRecord)
                where { awfulTable.eMail isLike "fred@%" }
            }
            assertEquals(1, rows)

            val answer = mapper.select { where { awfulTable.customerId isEqualTo 57 } }
            assertEquals(1, answer.size)

            val returnedRecord = answer[0]

            assertEquals(newRecord.customerId!!, returnedRecord.customerId)
            assertNull(returnedRecord.eMail)
            assertNull(returnedRecord.emailaddress)
            assertEquals(newRecord.firstFirstName, returnedRecord.firstFirstName)
            assertNull(returnedRecord.from)
            assertEquals(newRecord.id1!!, returnedRecord.id1)
            assertEquals(newRecord.id2!!, returnedRecord.id2)
            assertEquals(newRecord.id5!!, returnedRecord.id5)
            assertEquals(newRecord.id6!!, returnedRecord.id6)
            assertEquals(newRecord.id7!!, returnedRecord.id7)
            assertNull(returnedRecord.secondFirstName)
            assertNull(returnedRecord.thirdFirstName)
        }
    }
}
