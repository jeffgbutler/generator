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
package mbg.test.mb3.dsql.jspecify.record;

import static mbg.test.common.util.TestUtilities.blobsAreEqual;
import static mbg.test.common.util.TestUtilities.generateRandomBlob;
import static mbg.test.mb3.generated.dsql.jspecify.record.mapper.AwfulTableDynamicSqlSupport.AWFUL_TABLE;
import static mbg.test.mb3.generated.dsql.jspecify.record.mapper.FieldsblobsDynamicSqlSupport.FIELDSBLOBS;
import static mbg.test.mb3.generated.dsql.jspecify.record.mapper.FieldsonlyDynamicSqlSupport.FIELDSONLY;
import static mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkblobsDynamicSqlSupport.PKBLOBS;
import static mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkfieldsDynamicSqlSupport.PKFIELDSTABLE;
import static mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkfieldsblobsDynamicSqlSupport.PKFIELDSBLOBS;
import static mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkonlyDynamicSqlSupport.PKONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;



import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isLike;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotEqualTo;

import java.util.List;

import mbg.test.mb3.generated.dsql.jspecify.record.mapper.AwfulTableMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.FieldsblobsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.FieldsonlyMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkblobsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkfieldsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkfieldsblobsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkonlyMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.model.AwfulTable;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Fieldsblobs;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Fieldsonly;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkblobs;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkfields;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkfieldsblobs;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkonly;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

/**
 * @author Jeff Butler
 */
public class UpdateByExampleJSpecifyRecordTest extends AbstractJSpecifyRecordTest {

    @Test
    public void testFieldsOnlyUpdateByExampleSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);

            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);

            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(FIELDSONLY.DOUBLEFIELD).equalTo(99d)
                            .where(FIELDSONLY.INTEGERFIELD, isGreaterThan(5))
            );
            assertThat(rows).isEqualTo(2);

            List<Fieldsonly> answer = mapper.select(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isEqualTo(5)));
            assertThat(answer.size()).isEqualTo(1);
            record = answer.get(0);
            assertThat(record.doublefield()).isCloseTo(11.22, within(0.001));
            assertThat(record.floatfield()).isCloseTo(33.44, within(0.001));
            assertThat(record.integerfield()).isEqualTo(5);

            answer = mapper.select(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isEqualTo(8)));

            assertThat(answer.size()).isEqualTo(1);
            record = answer.get(0);
            assertThat(record.doublefield()).isCloseTo(99d, within(0.001));
            assertThat(record.floatfield()).isCloseTo(66.77, within(0.001));
            assertThat(record.integerfield()).isEqualTo(8);

            answer = mapper.select(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isEqualTo(9)));
            assertThat(answer.size()).isEqualTo(1);
            record = answer.get(0);
            assertThat(record.doublefield()).isCloseTo(99d, within(0.001));
            assertThat(record.floatfield()).isCloseTo(100.111, within(0.001));
            assertThat(record.integerfield()).isEqualTo(9);
        }
    }

    @Test
    public void testFieldsOnlyUpdateByExample() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);

            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);

            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(FIELDSONLY.INTEGERFIELD).equalTo(22)
                            .set(FIELDSONLY.DOUBLEFIELD).equalToNull()
                            .set(FIELDSONLY.FLOATFIELD).equalToNull()
                            .where(FIELDSONLY.INTEGERFIELD, isEqualTo(5)));
            assertThat(rows).isEqualTo(1);

            List<Fieldsonly> answer = mapper.select(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isEqualTo(22)));
            assertThat(answer.size()).isEqualTo(1);
            record = answer.get(0);
            assertThat(record.doublefield()).isNull();
            assertThat(record.floatfield()).isNull();
            assertThat(record.integerfield()).isEqualTo(22);
        }
    }

    @Test
    public void testPKOnlyUpdateByExampleSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            int rows = mapper.update(dsl ->
                    dsl.set(PKONLY.SEQ_NUM).equalTo(3)
                            .where(PKONLY.ID, isGreaterThan(4)));
            assertThat(rows).isEqualTo(2);

            long returnedRows = mapper.count(dsl ->
                    dsl.where(PKONLY.ID, isEqualTo(5))
                    .and(PKONLY.SEQ_NUM, isEqualTo(3)));
            assertThat(returnedRows).isEqualTo(1);

            returnedRows = mapper.count(dsl ->
                    dsl.where(PKONLY.ID, isEqualTo(7))
                    .and(PKONLY.SEQ_NUM, isEqualTo(3)));
            assertThat(returnedRows).isEqualTo(1);
        }
    }

    @Test
    public void testPKOnlyUpdateByExample() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            int rows = mapper.update(dsl ->
                    dsl.set(PKONLY.ID).equalTo(22)
                            .set(PKONLY.SEQ_NUM).equalTo(3)
                            .where(PKONLY.ID, isEqualTo(7)));
            assertThat(rows).isEqualTo(1);

            long returnedRows = mapper.count(dsl ->
                    dsl.where(PKONLY.ID, isEqualTo(22))
                    .and(PKONLY.SEQ_NUM, isEqualTo(3)));
            assertThat(returnedRows).isEqualTo(1);
        }
    }

    @Test
    public void testPKFieldsUpdateByExampleSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder()
                    .id2(2)
                    .id1(1)
                    .firstname("Jeff")
                    .lastname("Smith")
                    .build();
            mapper.insert(record);

            record = Pkfields.newBuilder()
                    .id2(4)
                    .id1(3)
                    .firstname("Bob")
                    .lastname("Jones")
                    .build();
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(PKFIELDSTABLE.FIRSTNAME).equalTo("Fred")
                            .where(PKFIELDSTABLE.LASTNAME, isLike("J%")));
            assertThat(rows).isEqualTo(1);

            long returnedRows = mapper.count(dsl ->
                    dsl.where(PKFIELDSTABLE.FIRSTNAME, isEqualTo("Fred"))
                    .and(PKFIELDSTABLE.LASTNAME, isEqualTo("Jones"))
                    .and(PKFIELDSTABLE.ID1, isEqualTo(3))
                    .and(PKFIELDSTABLE.ID2, isEqualTo(4)));
            assertThat(returnedRows).isEqualTo(1);
        }
    }

    @Test
    public void testPKFieldsUpdateByExample() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder()
                    .id2(2)
                    .id1(1)
                    .firstname("Jeff")
                    .lastname("Smith")
                    .build();
            mapper.insert(record);

            record = Pkfields.newBuilder()
                    .id2(4)
                    .id1(3)
                    .firstname("Bob")
                    .lastname("Jones")
                    .build();
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(PKFIELDSTABLE.FIRSTNAME).equalTo("Fred")
                            .where(PKFIELDSTABLE.ID1, isEqualTo(3))
                            .and(PKFIELDSTABLE.ID2, isEqualTo(4)));
            assertThat(rows).isEqualTo(1);

            long returnedRows = mapper.count(dsl ->
                    dsl.where(PKFIELDSTABLE.FIRSTNAME, isEqualTo("Fred"))
                    .and(PKFIELDSTABLE.ID1, isEqualTo(3))
                    .and(PKFIELDSTABLE.ID2, isEqualTo(4)));
            assertThat(returnedRows).isEqualTo(1);
        }
    }

    @Test
    public void testPKBlobsUpdateByExampleSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Pkblobs(6, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            var newBlob = generateRandomBlob();

            int rows = mapper.update(dsl ->
                    dsl.set(PKBLOBS.BLOB1).equalTo(newBlob)
                            .where(PKBLOBS.ID, isGreaterThan(4)));
            assertThat(rows).isEqualTo(1);

            List<Pkblobs> answer = mapper.select(dsl ->
                    dsl.where(PKBLOBS.ID, isGreaterThan(4)));
            assertThat(answer.size()).isEqualTo(1);

            Pkblobs returnedRecord = answer.get(0);

            assertThat(returnedRecord.id().intValue()).isEqualTo(6);
            assertThat(blobsAreEqual(newBlob, returnedRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), returnedRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testPKBlobsUpdateByExample() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Pkblobs(6, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(PKBLOBS.ID).equalTo(8)
                            .set(PKBLOBS.BLOB1).equalToNull()
                            .set(PKBLOBS.BLOB2).equalToNull()
                            .set(PKBLOBS.CHARACTERLOB).equalToNull()
                            .where(PKBLOBS.ID, isGreaterThan(4)));
            assertThat(rows).isEqualTo(1);

            List<Pkblobs> answer = mapper.select(dsl ->
                    dsl.where(PKBLOBS.ID, isGreaterThan(4)));
            assertThat(answer.size()).isEqualTo(1);

            Pkblobs returnedRecord = answer.get(0);

            assertThat(returnedRecord.id().intValue()).isEqualTo(8);
            assertThat(returnedRecord.blob1()).isNull();
            assertThat(returnedRecord.blob2()).isNull();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(PKFIELDSBLOBS.FIRSTNAME).equalTo("Fred")
                            .where(PKFIELDSBLOBS.ID1, isNotEqualTo(3)));
            assertThat(rows).isEqualTo(1);

            List<Pkfieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSBLOBS.ID1, isNotEqualTo(3)));
            assertThat(answer.size()).isEqualTo(1);

            Pkfieldsblobs returnedRecord = answer.get(0);

            assertThat(returnedRecord.id1()).isEqualTo(record.id1());
            assertThat(returnedRecord.id2()).isEqualTo(record.id2());
            assertThat(returnedRecord.firstname()).isEqualTo("Fred");
            assertThat(returnedRecord.lastname()).isEqualTo(record.lastname());
            assertThat(blobsAreEqual(record.blob1(), returnedRecord.blob1())).isTrue();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExample() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(PKFIELDSBLOBS.ID1).equalTo(3)
                            .set(PKFIELDSBLOBS.ID2).equalTo(8)
                            .set(PKFIELDSBLOBS.FIRSTNAME).equalTo("Fred")
                            .set(PKFIELDSBLOBS.LASTNAME).equalToNull()
                            .set(PKFIELDSBLOBS.BLOB1).equalToNull()
                            .where(PKFIELDSBLOBS.ID1, isEqualTo(3)));
            assertThat(rows).isEqualTo(1);

            List<Pkfieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSBLOBS.ID1, isEqualTo(3)));
            assertThat(answer.size()).isEqualTo(1);

            Pkfieldsblobs returnedRecord = answer.get(0);

            assertThat(returnedRecord.id1()).isEqualTo(3);
            assertThat(returnedRecord.id2()).isEqualTo(8);
            assertThat(returnedRecord.firstname()).isEqualTo("Fred");
            assertThat(returnedRecord.lastname()).isNull();
            assertThat(returnedRecord.blob1()).isNull();
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExampleSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(FIELDSBLOBS.LASTNAME).equalTo("Doe")
                            .where(FIELDSBLOBS.FIRSTNAME, isLike("S%")));
            assertThat(rows).isEqualTo(1);

            List<Fieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(FIELDSBLOBS.FIRSTNAME, isLike("S%")));
            assertThat(answer.size()).isEqualTo(1);

            Fieldsblobs returnedRecord = answer.get(0);

            assertThat(returnedRecord.firstname()).isEqualTo(record.firstname());
            assertThat(returnedRecord.lastname()).isEqualTo("Doe");
            assertThat(blobsAreEqual(record.blob1(), returnedRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), returnedRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExample() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(FIELDSBLOBS.FIRSTNAME).equalTo("Scott")
                            .set(FIELDSBLOBS.LASTNAME).equalTo("Doe")
                            .set(FIELDSBLOBS.BLOB1).equalToNull()
                            .set(FIELDSBLOBS.BLOB2).equalToNull()
                            .set(FIELDSBLOBS.BLOB3).equalToNull()
                            .where(FIELDSBLOBS.FIRSTNAME, isLike("S%")));
            assertThat(rows).isEqualTo(1);

            List<Fieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(FIELDSBLOBS.FIRSTNAME, isLike("S%")));
            assertThat(answer.size()).isEqualTo(1);

            Fieldsblobs returnedRecord = answer.get(0);

            assertThat(returnedRecord.firstname()).isEqualTo("Scott");
            assertThat(returnedRecord.lastname()).isEqualTo("Doe");
            assertThat(returnedRecord.blob1()).isNull();
            assertThat(returnedRecord.blob2()).isNull();
        }
    }

    @Test
    public void testAwfulTableUpdateByExampleSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");

            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("fred2@fred.com");
            record.setEmailaddress("alsofred2@fred.com");
            record.setFirstFirstName("fred11");
            record.setFrom("from from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("fred22");
            record.setThirdFirstName("fred33");

            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(AWFUL_TABLE.FIRST_FIRST_NAME).equalTo("Alonzo")
                            .where(AWFUL_TABLE.E_MAIL, isLike("fred2@%")));
            assertThat(rows).isEqualTo(1);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.E_MAIL, isLike("fred2@%")));
            assertThat(answer.size()).isEqualTo(1);

            AwfulTable returnedRecord = answer.get(0);

            assertThat(returnedRecord.getCustomerId()).isEqualTo(record.getCustomerId());
            assertThat(returnedRecord.geteMail()).isEqualTo(record.geteMail());
            assertThat(returnedRecord.getEmailaddress()).isEqualTo(record.getEmailaddress());
            assertThat(returnedRecord.getFirstFirstName()).isEqualTo("Alonzo");
            assertThat(returnedRecord.getFrom()).isEqualTo(record.getFrom());
            assertThat(returnedRecord.getId1()).isEqualTo(record.getId1());
            assertThat(returnedRecord.getId2()).isEqualTo(record.getId2());
            assertThat(returnedRecord.getId5()).isEqualTo(record.getId5());
            assertThat(returnedRecord.getId6()).isEqualTo(record.getId6());
            assertThat(returnedRecord.getId7()).isEqualTo(record.getId7());
            assertThat(returnedRecord.getSecondFirstName()).isEqualTo(record.getSecondFirstName());
            assertThat(returnedRecord.getThirdFirstName()).isEqualTo(record.getThirdFirstName());
        }
    }

    @Test
    public void testAwfulTableUpdateByExample() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");

            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("fred2@fred.com");
            record.setEmailaddress("alsofred2@fred.com");
            record.setFirstFirstName("fred11");
            record.setFrom("from from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("fred22");
            record.setThirdFirstName("fred33");

            mapper.insert(record);

            int rows = mapper.update(dsl ->
                    dsl.set(AWFUL_TABLE.FIRST_FIRST_NAME).equalTo("Alonzo")
                            .set(AWFUL_TABLE.ID1).equalTo(111)
                            .set(AWFUL_TABLE.ID2).equalTo(222)
                            .set(AWFUL_TABLE.ID5).equalTo(555)
                            .set(AWFUL_TABLE.ID6).equalTo(666)
                            .set(AWFUL_TABLE.ID7).equalTo(777)
                            .set(AWFUL_TABLE.E_MAIL).equalToNull()
                            .set(AWFUL_TABLE.EMAILADDRESS).equalToNull()
                            .set(AWFUL_TABLE.FROM).equalToNull()
                            .set(AWFUL_TABLE.SECOND_FIRST_NAME).equalToNull()
                            .set(AWFUL_TABLE.THIRD_FIRST_NAME).equalToNull()
                .where(AWFUL_TABLE.E_MAIL, isLike("fred2@%")));
            assertThat(rows).isEqualTo(1);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.CUSTOMER_ID, isEqualTo(58)));
            assertThat(answer.size()).isEqualTo(1);

            AwfulTable returnedRecord = answer.get(0);

            assertThat(returnedRecord.getCustomerId()).isEqualTo(58);
            assertThat(returnedRecord.geteMail()).isNull();
            assertThat(returnedRecord.getEmailaddress()).isNull();
            assertThat(returnedRecord.getFirstFirstName()).isEqualTo("Alonzo");
            assertThat(returnedRecord.getFrom()).isNull();
            assertThat(returnedRecord.getId1()).isEqualTo(111);
            assertThat(returnedRecord.getId2()).isEqualTo(222);
            assertThat(returnedRecord.getId5()).isEqualTo(555);
            assertThat(returnedRecord.getId6()).isEqualTo(666);
            assertThat(returnedRecord.getId7()).isEqualTo(777);
            assertThat(returnedRecord.getSecondFirstName()).isNull();
            assertThat(returnedRecord.getThirdFirstName()).isNull();
        }
    }
}
