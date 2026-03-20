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
import static org.mybatis.dynamic.sql.SqlBuilder.and;
import static org.mybatis.dynamic.sql.SqlBuilder.isBetween;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isLike;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotLike;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import mbg.test.mb3.generated.dsql.jspecify.record.mapper.AwfulTableMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.FieldsblobsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.FieldsonlyMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkblobsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkfieldsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkfieldsblobsMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.PkonlyMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.mbgtest.IdMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.mapper.mbgtest.sub.TranslationMapper;
import mbg.test.mb3.generated.dsql.jspecify.record.model.AwfulTable;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Fieldsblobs;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Fieldsonly;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkblobs;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkfields;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkfieldsblobs;
import mbg.test.mb3.generated.dsql.jspecify.record.model.Pkonly;
import mbg.test.mb3.generated.dsql.jspecify.record.model.mbgtest.Id;
import mbg.test.mb3.generated.dsql.jspecify.record.model.mbgtest.sub.Translation;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;

/**
 * @author Jeff Butler
 */
public class DynamicSqlJSpecifyRecordTest extends AbstractJSpecifyRecordTest {

    @Test
    public void testFieldsOnlyInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);

            List<Fieldsonly> answer = mapper.select(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isEqualTo(5)));
            assertThat(answer.size()).isEqualTo(1);

            Fieldsonly returnedRecord = answer.get(0);
            assertThat(returnedRecord.integerfield()).isEqualTo(record.integerfield());
            assertThat(returnedRecord.doublefield()).isEqualTo(record.doublefield());
            assertThat(returnedRecord.floatfield()).isEqualTo(record.floatfield());
        }
    }

    @Test
    public void testFieldsOnlySelect() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);

            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);

            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);

            List<Fieldsonly> answer = mapper.select(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isGreaterThan(5)));
            assertThat(answer.size()).isEqualTo(2);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(3);
        }
    }

    @Test
    public void testFieldsOnlySelectByExampleWithMultiInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);

            Fieldsonly record1 = new Fieldsonly(5, 11.22, 33.44);
            Fieldsonly record2 = new Fieldsonly(8, 44.55, 66.77);
            Fieldsonly record3 = new Fieldsonly(9, 88.99, 100.111);

            mapper.insertMultiple(Arrays.asList(record1, record2, record3));

            List<Fieldsonly> answer = mapper.select(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isGreaterThan(5)));
            assertThat(answer.size()).isEqualTo(2);

            answer = mapper.select(SelectDSLCompleter.allRowsOrderedBy(FIELDSONLY.INTEGERFIELD));
            assertThat(answer.size()).isEqualTo(3);
            assertThat(answer.get(0).integerfield()).isEqualTo(5);
            assertThat(answer.get(1).integerfield()).isEqualTo(8);
            assertThat(answer.get(2).integerfield()).isEqualTo(9);
        }
    }

    @Test
    public void testFieldsOnlySelectByExampleDistinct() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);
            mapper.insert(record);
            mapper.insert(record);

            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);

            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);

            List<Fieldsonly> answer = mapper.selectDistinct(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isEqualTo(5)));
            assertThat(answer.size()).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(5);
        }
    }

    @Test
    public void testFieldsOnlySelectByExampleNoCriteria() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);

            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);

            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);

            List<Fieldsonly> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(3);
        }
    }

    @Test
    public void testFieldsOnlyDelete() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);

            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);

            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);

            int rows = mapper.delete(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isGreaterThan(5)));
            assertThat(rows).isEqualTo(2);

            List<Fieldsonly> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testFieldsOnlyCount() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);

            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);

            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);

            long rows = mapper.count(dsl ->
                    dsl.where(FIELDSONLY.INTEGERFIELD, isGreaterThan(5)));
            assertThat(rows).isEqualTo(2);

            rows = mapper.count(CountDSLCompleter.allRows());
            assertThat(rows).isEqualTo(3);
        }
    }

    @Test
    public void testPKOnlyInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            List<Pkonly> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);

            Pkonly returnedRecord = answer.get(0);
            assertThat(returnedRecord.id()).isEqualTo(key.id());
            assertThat(returnedRecord.seqNum()).isEqualTo(key.seqNum());
        }
    }

    @Test
    public void testPKOnlyDeleteByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            int rows = mapper.insert(key);
            assertThat(rows).isEqualTo(1);

            key = new Pkonly(5, 6);
            rows = mapper.insert(key);
            assertThat(rows).isEqualTo(1);

            List<Pkonly> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            rows = mapper.deleteByPrimaryKey(5, 6);
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testPKOnlyDelete() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            int rows = mapper.delete(dsl ->
                    dsl.where(PKONLY.ID, isGreaterThan(4)));
            assertThat(rows).isEqualTo(2);

            List<Pkonly> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testPKOnlySelect() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            List<Pkonly> answer = mapper.select(dsl ->
                    dsl.where(PKONLY.ID, isGreaterThan(4))
                    .orderBy(PKONLY.ID));
            assertThat(answer.size()).isEqualTo(2);
            assertThat(answer.get(0).id().intValue()).isEqualTo(5);
            assertThat(answer.get(0).seqNum().intValue()).isEqualTo(6);
            assertThat(answer.get(1).id().intValue()).isEqualTo(7);
            assertThat(answer.get(1).seqNum().intValue()).isEqualTo(8);
        }
    }

    @Test
    public void testPKOnlySelectByExampleWithMultiInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);

            mapper.insertMultiple(Arrays.asList(new Pkonly(1,3), new Pkonly(5, 6), new Pkonly(7, 8)));

            List<Pkonly> answer = mapper.select(dsl ->
                    dsl.where(PKONLY.ID, isGreaterThan(4))
                    .orderBy(PKONLY.ID));
            assertThat(answer.size()).isEqualTo(2);
            assertThat(answer.get(0).id().intValue()).isEqualTo(5);
            assertThat(answer.get(0).seqNum().intValue()).isEqualTo(6);
            assertThat(answer.get(1).id().intValue()).isEqualTo(7);
            assertThat(answer.get(1).seqNum().intValue()).isEqualTo(8);
        }
    }

    @Test
    public void testPKOnlySelectByExampleBackwards() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            List<Pkonly> answer = mapper.select(c ->
                c.where(PKONLY.ID, isGreaterThan(4))
                .orderBy(PKONLY.ID)
            );
            assertThat(answer.size()).isEqualTo(2);
            assertThat(answer.get(0).id().intValue()).isEqualTo(5);
            assertThat(answer.get(0).seqNum().intValue()).isEqualTo(6);
            assertThat(answer.get(1).id().intValue()).isEqualTo(7);
            assertThat(answer.get(1).seqNum().intValue()).isEqualTo(8);
        }
    }

    @Test
    public void testPKOnlySelectByExampleWithBackwardsResults() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            List<Pkonly> answer = mapper.select(c ->
                c.where(PKONLY.ID, isGreaterThan(4))
                .orderBy(PKONLY.ID)
            );
            assertThat(answer.size()).isEqualTo(2);
            assertThat(answer.get(0).id().intValue()).isEqualTo(5);
            assertThat(answer.get(0).seqNum().intValue()).isEqualTo(6);
            assertThat(answer.get(1).id().intValue()).isEqualTo(7);
            assertThat(answer.get(1).seqNum().intValue()).isEqualTo(8);
        }
    }

    @Test
    public void testPKOnlySelectByExampleNoCriteria() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            List<Pkonly> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer).hasSize(3);
        }
    }

    @Test
    public void testPKOnlyCount() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);

            key = new Pkonly(5, 6);
            mapper.insert(key);

            key = new Pkonly(7, 8);
            mapper.insert(key);

            long rows = mapper.count(dsl ->
                    dsl.where(PKONLY.ID, isGreaterThan(4)));
            assertThat(rows).isEqualTo(2);

            rows = mapper.count(dsl -> dsl);
            assertThat(rows).isEqualTo(3);
        }
    }

    @Test
    public void testPKFieldsInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields(2, 1, "Jeff", "Butler", LocalDate.now(), LocalTime.of(13, 2, 4),
                    LocalDateTime.now(), (short) 3, 6, 10L, new BigDecimal("15.12345"), null, null, true);
            mapper.insert(record);

            Optional<Pkfields> returnedRecord = mapper.selectByPrimaryKey(2, 1);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.datefield()).isEqualTo(record.datefield());
                assertThat(rr.decimal100field()).isEqualTo(record.decimal100field());
                assertThat(rr.decimal155field()).isEqualTo(record.decimal155field());
                assertThat(rr.decimal30field()).isEqualTo(record.decimal30field());
                assertThat(rr.decimal60field()).isEqualTo(record.decimal60field());
                assertThat(rr.firstname()).isEqualTo(record.firstname());
                assertThat(rr.id1()).isEqualTo(record.id1());
                assertThat(rr.id2()).isEqualTo(record.id2());
                assertThat(rr.lastname()).isEqualTo(record.lastname());
                assertThat(rr.timefield()).isEqualTo(record.timefield());
                assertThat(record.timestampfield()).isCloseTo(rr.timestampfield(), within(1, ChronoUnit.MILLIS));
                assertThat(rr.stringboolean()).isEqualTo(record.stringboolean());
            });
        }
    }

    @Test
    public void testPKFieldsUpdateByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder()
                    .id2(2)
                    .id1(1)
                    .firstname("Jeff")
                    .lastname("Smith")
                    .build();
            mapper.insert(record);

            Pkfields updateRecord = record.withFirstname("Scott").withLastname("Jones");
            int rows = mapper.updateByPrimaryKey(updateRecord);
            assertThat(rows).isEqualTo(1);

            Optional<Pkfields> record2 = mapper.selectByPrimaryKey(2, 1);

            assertThat(record2).hasValueSatisfying(r2 -> {
                assertThat(r2.firstname()).isEqualTo(updateRecord.firstname());
                assertThat(r2.lastname()).isEqualTo(updateRecord.lastname());
                assertThat(r2.id1()).isEqualTo(updateRecord.id1());
                assertThat(r2.id2()).isEqualTo(updateRecord.id2());
            });
        }
    }

    @Test
    public void testPKFieldsUpdateByPrimaryKeySelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder()
                    .id2(2).id1(1).firstname("Jeff").lastname("Smith").decimal60field(5).build();
            mapper.insert(record);

            int rows = mapper.update(d ->
                    d.set(PKFIELDSTABLE.FIRSTNAME).equalTo("Scott")
                            .set(PKFIELDSTABLE.DECIMAL60FIELD).equalTo(4)
                            .where(PKFIELDSTABLE.ID1, isEqualTo(1))
                            .and(PKFIELDSTABLE.ID2, isEqualTo(2))
            );

            assertThat(rows).isEqualTo(1);

            Optional<Pkfields> returnedRecord = mapper.selectByPrimaryKey(2, 1);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.datefield()).isEqualTo(record.datefield());
                assertThat(rr.decimal100field()).isEqualTo(record.decimal100field());
                assertThat(rr.decimal155field()).isEqualTo(record.decimal155field());
                assertThat(rr.decimal30field()).isEqualTo(record.decimal30field());
                assertThat(rr.decimal60field()).isEqualTo(4);
                assertThat(rr.firstname()).isEqualTo("Scott");
                assertThat(rr.id1()).isEqualTo(record.id1());
                assertThat(rr.id2()).isEqualTo(record.id2());
                assertThat(rr.lastname()).isEqualTo(record.lastname());
                assertThat(rr.timefield()).isEqualTo(record.timefield());
                assertThat(rr.timestampfield()).isEqualTo(record.timestampfield());
            });
        }
    }

    @Test
    public void testPKfieldsDeleteByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(2).id1(1).firstname("Jeff").lastname("Smith").build();
            mapper.insert(record);

            int rows = mapper.deleteByPrimaryKey(2, 1);
            assertThat(rows).isEqualTo(1);

            List<Pkfields> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(0);
        }
    }

    @Test
    public void testPKFieldsDelete() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(2).id1(1).firstname("Jeff").lastname("Smith").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(4).id1(3).firstname("Bob").lastname("Jones").build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            int rows = mapper.delete(dsl ->
                    dsl.where(PKFIELDSTABLE.LASTNAME, isLike("J%")));
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testPKFieldsSelectByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(2).id1(1).firstname("Jeff").lastname("Smith").build();
            mapper.insert(record);

            Pkfields record1 = Pkfields.newBuilder().id2(4).id1(3).firstname("Bob").lastname("Jones").build();
            mapper.insert(record1);

            Optional<Pkfields> newRecord = mapper.selectByPrimaryKey(4, 3);

            assertThat(newRecord).hasValueSatisfying(nr -> {
                assertThat(nr.firstname()).isEqualTo(record1.firstname());
                assertThat(nr.lastname()).isEqualTo(record1.lastname());
                assertThat(nr.id1()).isEqualTo(record1.id1());
                assertThat(nr.id2()).isEqualTo(record1.id2());
            });
        }
    }

    @Test
    public void testPKFieldsSelectByExampleLike() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble").build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSTABLE.FIRSTNAME, isLike("B%"))
                    .orderBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));
            assertThat(answer.size()).isEqualTo(3);
            Pkfields returnedRecord = answer.get(0);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(2);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(1);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(2);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(2);
            returnedRecord = answer.get(2);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(2);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(3);
        }
    }

    @Test
    public void testPKFieldsSelectByExampleNotLike() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble").build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSTABLE.FIRSTNAME, isNotLike("B%"))
                    .orderBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));
            assertThat(answer.size()).isEqualTo(3);
            Pkfields returnedRecord = answer.get(0);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(1);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(1);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(1);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(2);
            returnedRecord = answer.get(2);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(1);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(3);
        }
    }

    @Test
    public void testPKFieldsSelectByExampleComplexLike() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble").build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSTABLE.FIRSTNAME, isLike("B%"), and(PKFIELDSTABLE.ID2, isEqualTo(3)))
                    .or(PKFIELDSTABLE.FIRSTNAME, isLike("Wi%"))
                    .orderBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));

            assertThat(answer.size()).isEqualTo(2);
            Pkfields returnedRecord = answer.get(0);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(1);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(2);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(2);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(3);
        }
    }

    @Test
    public void testPKFieldsSelectByExampleIn() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble").build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSTABLE.ID2, isIn(1, 3))
                    .orderBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));
            assertThat(answer.size()).isEqualTo(4);
            Pkfields returnedRecord = answer.get(0);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(1);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(1);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(1);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(3);
            returnedRecord = answer.get(2);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(2);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(1);
            returnedRecord = answer.get(3);
            assertThat(returnedRecord.id1().intValue()).isEqualTo(2);
            assertThat(returnedRecord.id2().intValue()).isEqualTo(3);
        }
    }

    @Test
    public void testPKFieldsSelectByExampleBetween() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble").build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSTABLE.ID2, isBetween(1).and(3))
                    .orderBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));
            assertThat(answer.size()).isEqualTo(6);
        }
    }

    @Test
    public void testPKFieldsSelectByExampleNoCriteria() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble").build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(
                    SelectDSLCompleter.allRowsOrderedBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));

            assertThat(answer.size()).isEqualTo(6);
        }
    }

    @Test
    public void testPKFieldsSelectByExampleNoCriteriaWithMultiInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);

            Collection<Pkfields> records = new ArrayList<>();

            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone").build();
            records.add(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone").build();
            records.add(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone").build();
            records.add(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble").build();
            records.add(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble").build();
            records.add(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble").build();
            records.add(record);

            int rowsInserted = mapper.insertMultiple(records);

            assertThat(rowsInserted).isEqualTo(6);

            List<Pkfields> answer = mapper.select(
                    SelectDSLCompleter.allRowsOrderedBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));

            assertThat(answer.size()).isEqualTo(6);
        }
    }

    @Test
    public void testPKFieldsSelectByExampleEscapedFields() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(1).id1(1).firstname("Fred").lastname("Flintstone")
                    .wierdField(11).build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(1).firstname("Wilma").lastname("Flintstone")
                    .wierdField(22).build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(1).firstname("Pebbles").lastname("Flintstone")
                    .wierdField(33).build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(1).id1(2).firstname("Barney").lastname("Rubble")
                    .wierdField(44).build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(2).id1(2).firstname("Betty").lastname("Rubble")
                    .wierdField(55).build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(3).id1(2).firstname("Bamm Bamm").lastname("Rubble")
                    .wierdField(66).build();
            mapper.insert(record);

            List<Pkfields> answer = mapper.select(DSL ->
                    DSL.where(PKFIELDSTABLE.WIERD_FIELD, isLessThan(40))
                    .and(PKFIELDSTABLE.WIERD_FIELD, isIn(11, 22))
                    .orderBy(PKFIELDSTABLE.ID1, PKFIELDSTABLE.ID2));

            assertThat(answer.size()).isEqualTo(2);
        }
    }

    @Test
    public void testPKFieldsCount() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = Pkfields.newBuilder().id2(2).id1(1).firstname("Jeff").lastname("Smith").build();
            mapper.insert(record);

            record = Pkfields.newBuilder().id2(4).id1(3).firstname("Bob").lastname("Jones").build();
            mapper.insert(record);

            long rows = mapper.count(dsl ->
                    dsl.where(PKFIELDSTABLE.LASTNAME, isLike("J%")));

            assertThat(rows).isEqualTo(1);

            rows = mapper.count(dsl -> dsl);
            assertThat(rows).isEqualTo(2);
        }
    }

    @Test
    public void testPKBlobsInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Pkblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);

            Pkblobs returnedRecord = answer.get(0);
            assertThat(returnedRecord.id()).isEqualTo(record.id());
            assertThat(blobsAreEqual(record.blob1(), returnedRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), returnedRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testPKBlobsUpdateByPrimaryKeyWithBLOBs() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            Pkblobs record1 = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            int rows = mapper.updateByPrimaryKey(record1);
            assertThat(rows).isEqualTo(1);

            Optional<Pkblobs> newRecord = mapper.selectByPrimaryKey(3);

            assertThat(newRecord).hasValueSatisfying(nr -> {
                assertThat(nr.id()).isEqualTo(record1.id());
                assertThat(blobsAreEqual(record1.blob1(), nr.blob1())).isTrue();
                assertThat(blobsAreEqual(record1.blob2(), nr.blob2())).isTrue();
            });
        }
    }

    @Test
    public void testPKBlobsUpdateByPrimaryKeySelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            var newBlob = generateRandomBlob();
            mapper.update(d ->
                    d.set(PKBLOBS.BLOB2).equalTo(newBlob)
                            .where(PKBLOBS.ID, isEqualTo(3))
            );

            Optional<Pkblobs> returnedRecord = mapper.selectByPrimaryKey(3);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.id()).isEqualTo(record.id());
                assertThat(blobsAreEqual(record.blob1(), rr.blob1())).isTrue();
                assertThat(blobsAreEqual(newBlob, rr.blob2())).isTrue();
            });
        }
    }

    @Test
    public void testPKBlobsDeleteByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Pkblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);

            int rows = mapper.deleteByPrimaryKey(3);
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(0);
        }
    }

    @Test
    public void testPKBlobsDelete() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Pkblobs(6, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Pkblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            int rows = mapper.delete(dsl ->
                    dsl.where(PKBLOBS.ID, isLessThan(4)));
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testPKBlobsSelectByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            Pkblobs record1 = new Pkblobs(6, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record1);

            Optional<Pkblobs> newRecord = mapper.selectByPrimaryKey(6);

            assertThat(newRecord).hasValueSatisfying(nr -> {
                assertThat(nr.id()).isEqualTo(record1.id());
                assertThat(blobsAreEqual(record1.blob1(), nr.blob1())).isTrue();
                assertThat(blobsAreEqual(record1.blob2(), nr.blob2())).isTrue();
            });
        }
    }

    @Test
    public void testPKBlobsSelectByExampleWithBlobs() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Pkblobs(6, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Pkblobs> answer = mapper.select(DSL ->
                    DSL.where(PKBLOBS.ID, isGreaterThan(4)));

            assertThat(answer.size()).isEqualTo(1);

            Pkblobs newRecord = answer.get(0);
            assertThat(newRecord.id()).isEqualTo(record.id());
            assertThat(blobsAreEqual(record.blob1(), newRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), newRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testPKBlobsSelectByExampleWithMultiInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Collection<Pkblobs> records = new ArrayList<>();

            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            records.add(record);

            record = new Pkblobs(6, generateRandomBlob(), generateRandomBlob(), null);
            records.add(record);

            int recordsInserted = mapper.insertMultiple(records);
            assertThat(recordsInserted).isEqualTo(2);

            List<Pkblobs> answer = mapper.select(dsl ->
                    dsl.where(PKBLOBS.ID, isGreaterThan(4)));

            assertThat(answer.size()).isEqualTo(1);

            Pkblobs newRecord = answer.get(0);
            assertThat(newRecord.id()).isEqualTo(record.id());
            assertThat(blobsAreEqual(record.blob1(), newRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), newRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testPKBlobsCount() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs(3, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Pkblobs(6, generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            long rows = mapper.count(dsl ->
                    dsl.where(PKBLOBS.ID, isLessThan(4)));
            assertThat(rows).isEqualTo(1);

            rows = mapper.count(dsl -> dsl);
            assertThat(rows).isEqualTo(2);
        }
    }

    @Test
    public void testPKFieldsBlobsInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            List<Pkfieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);

            Pkfieldsblobs returnedRecord = answer.get(0);
            assertThat(returnedRecord.id1()).isEqualTo(record.id1());
            assertThat(returnedRecord.id2()).isEqualTo(record.id2());
            assertThat(returnedRecord.firstname()).isEqualTo(record.firstname());
            assertThat(returnedRecord.lastname()).isEqualTo(record.lastname());
            assertThat(blobsAreEqual(record.blob1(), returnedRecord.blob1())).isTrue();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByPrimaryKeyWithBLOBs() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            Pkfieldsblobs updateRecord = new Pkfieldsblobs(3, 4, "Scott", "Jones", generateRandomBlob());

            int rows = mapper.updateByPrimaryKey(updateRecord);
            assertThat(rows).isEqualTo(1);

            Optional<Pkfieldsblobs> newRecord = mapper.selectByPrimaryKey(3, 4);

            assertThat(newRecord).hasValueSatisfying(nr -> {
                assertThat(nr.firstname()).isEqualTo(updateRecord.firstname());
                assertThat(nr.lastname()).isEqualTo(updateRecord.lastname());
                assertThat(nr.id1()).isEqualTo(record.id1());
                assertThat(nr.id2()).isEqualTo(record.id2());
                assertThat(blobsAreEqual(updateRecord.blob1(), nr.blob1())).isTrue();
            });
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByPrimaryKeySelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            int rows = mapper.update(d ->
                    d.set(PKFIELDSBLOBS.LASTNAME).equalTo("Jones")
                            .where(PKFIELDSBLOBS.ID1, isEqualTo(3))
                            .and(PKFIELDSBLOBS.ID2, isEqualTo(4))
            );
            assertThat(rows).isEqualTo(1);

            Optional<Pkfieldsblobs> returnedRecord = mapper.selectByPrimaryKey(3, 4);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.firstname()).isEqualTo(record.firstname());
                assertThat(rr.lastname()).isEqualTo("Jones");
                assertThat(rr.id1()).isEqualTo(record.id1());
                assertThat(rr.id2()).isEqualTo(record.id2());
                assertThat(blobsAreEqual(record.blob1(), rr.blob1())).isTrue();
            });
        }
    }

    @Test
    public void testPKFieldsBlobsDeleteByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);

            List<Pkfieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            int rows = mapper.deleteByPrimaryKey(5, 6);
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testPKFieldsBlobsDelete() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);

            List<Pkfieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            int rows = mapper.delete(dsl ->
                    dsl.where(PKFIELDSBLOBS.ID1, isNotEqualTo(3)));
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByPrimaryKey() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            Pkfieldsblobs record1 = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record1);

            List<Pkfieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            Optional<Pkfieldsblobs> newRecord = mapper.selectByPrimaryKey(5, 6);

            assertThat(newRecord).hasValueSatisfying(nr -> {
                assertThat(nr.id1()).isEqualTo(record1.id1());
                assertThat(nr.id2()).isEqualTo(record1.id2());
                assertThat(nr.firstname()).isEqualTo(record1.firstname());
                assertThat(nr.lastname()).isEqualTo(record1.lastname());
                assertThat(blobsAreEqual(record1.blob1(), nr.blob1())).isTrue();
            });
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByExampleWithBlobs() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);

            List<Pkfieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSBLOBS.ID2, isEqualTo(6)));
            assertThat(answer.size()).isEqualTo(1);

            Pkfieldsblobs newRecord = answer.get(0);
            assertThat(newRecord.id1()).isEqualTo(record.id1());
            assertThat(newRecord.id2()).isEqualTo(record.id2());
            assertThat(newRecord.firstname()).isEqualTo(record.firstname());
            assertThat(newRecord.lastname()).isEqualTo(record.lastname());
            assertThat(blobsAreEqual(record.blob1(), newRecord.blob1())).isTrue();
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByExampleWithMultiInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Collection<Pkfieldsblobs> records = new ArrayList<>();

            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            records.add(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            records.add(record);

            int rowsInserted = mapper.insertMultiple(records);
            assertThat(rowsInserted).isEqualTo(2);

            List<Pkfieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(PKFIELDSBLOBS.ID2, isEqualTo(6)));
            assertThat(answer.size()).isEqualTo(1);

            Pkfieldsblobs newRecord = answer.get(0);
            assertThat(newRecord.id1()).isEqualTo(record.id1());
            assertThat(newRecord.id2()).isEqualTo(record.id2());
            assertThat(newRecord.firstname()).isEqualTo(record.firstname());
            assertThat(newRecord.lastname()).isEqualTo(record.lastname());
            assertThat(blobsAreEqual(record.blob1(), newRecord.blob1())).isTrue();
        }
    }

    @Test
    public void testPKFieldsBlobsSelectByExampleWithBlobsNoCriteria() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);

            List<Pkfieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);
        }
    }

    @Test
    public void testFieldsBlobsInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Fieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);

            Fieldsblobs returnedRecord = answer.get(0);
            assertThat(returnedRecord.firstname()).isEqualTo(record.firstname());
            assertThat(returnedRecord.lastname()).isEqualTo(record.lastname());
            assertThat(blobsAreEqual(record.blob1(), returnedRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), returnedRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testFieldsBlobsDelete() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Fieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            int rows = mapper.delete(dsl ->
                    dsl.where(FIELDSBLOBS.FIRSTNAME, isLike("S%")));
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testFieldsBlobsSelectByExampleWithBlobs() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Fieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(FIELDSBLOBS.FIRSTNAME, isLike("S%")));
            assertThat(answer.size()).isEqualTo(1);

            Fieldsblobs newRecord = answer.get(0);
            assertThat(newRecord.firstname()).isEqualTo(record.firstname());
            assertThat(newRecord.lastname()).isEqualTo(record.lastname());
            assertThat(blobsAreEqual(record.blob1(), newRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), newRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testFieldsBlobsSelectByExampleWithMultiInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Collection<Fieldsblobs> records = new ArrayList<>();

            Fieldsblobs record = new Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            records.add(record);

            record = new Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            records.add(record);

            int rowsInserted = mapper.insertMultiple(records);
            assertThat(rowsInserted).isEqualTo(2);

            List<Fieldsblobs> answer = mapper.select(dsl ->
                    dsl.where(FIELDSBLOBS.FIRSTNAME, isLike("S%")));
            assertThat(answer.size()).isEqualTo(1);

            Fieldsblobs newRecord = answer.get(0);
            assertThat(newRecord.firstname()).isEqualTo(record.firstname());
            assertThat(newRecord.lastname()).isEqualTo(record.lastname());
            assertThat(blobsAreEqual(record.blob1(), newRecord.blob1())).isTrue();
            assertThat(blobsAreEqual(record.blob2(), newRecord.blob2())).isTrue();
        }
    }

    @Test
    public void testFieldsBlobsSelectByExampleWithBlobsNoCriteria() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            record = new Fieldsblobs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);

            List<Fieldsblobs> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);
        }
    }

    @Test
    public void testPKFieldsBlobsCount() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);

            long rows = mapper.count(dsl ->
                    dsl.where(PKFIELDSBLOBS.ID1, isNotEqualTo(3)));
            assertThat(rows).isEqualTo(1);

            rows = mapper.count(dsl -> dsl);
            assertThat(rows).isEqualTo(2);
        }
    }

    @Test
    public void testAwfulTableInsert() {
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

            record.setActive(true);
            record.setActive1(Boolean.FALSE);
            record.setActive2(new byte[]{-128, 127});

            mapper.insert(record);
            Integer generatedCustomerId = record.getCustomerId();
            assertThat(generatedCustomerId.intValue()).isEqualTo(57);

            Optional<AwfulTable> returnedRecord = mapper
                    .selectByPrimaryKey(generatedCustomerId);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.getCustomerId()).isEqualTo(generatedCustomerId);
                assertThat(rr.geteMail()).isEqualTo(record.geteMail());
                assertThat(rr.getEmailaddress()).isEqualTo(record.getEmailaddress());
                assertThat(rr.getFirstFirstName()).isEqualTo(record.getFirstFirstName());
                assertThat(rr.getFrom()).isEqualTo(record.getFrom());
                assertThat(rr.getId1()).isEqualTo(record.getId1());
                assertThat(rr.getId2()).isEqualTo(record.getId2());
                assertThat(rr.getId5()).isEqualTo(record.getId5());
                assertThat(rr.getId6()).isEqualTo(record.getId6());
                assertThat(rr.getId7()).isEqualTo(record.getId7());
                assertThat(rr.getSecondFirstName()).isEqualTo(record.getSecondFirstName());
                assertThat(rr.getThirdFirstName()).isEqualTo(record.getThirdFirstName());
                assertThat(rr.isActive()).isTrue();
                assertThat(rr.getActive1()).isFalse();
                assertThat(rr.getActive2().length).isEqualTo(3);
                assertThat(rr.getActive2()[0]).isEqualTo((byte)-128);
                assertThat(rr.getActive2()[1]).isEqualTo((byte)127);
                assertThat(rr.getActive2()[2]).isEqualTo((byte)0);
            });
        }
    }

    @Test
    public void testAwfulTableInsertSelective() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();

            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");

            mapper.insertSelective(record);
            Integer generatedCustomerId = record.getCustomerId();
            assertThat(generatedCustomerId.intValue()).isEqualTo(57);

            Optional<AwfulTable> returnedRecord = mapper
                    .selectByPrimaryKey(generatedCustomerId);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.getCustomerId()).isEqualTo(generatedCustomerId);
                assertThat(rr.geteMail()).isEqualTo(record.geteMail());
                assertThat(rr.getEmailaddress()).isEqualTo(record.getEmailaddress());
                assertThat(rr.getFirstFirstName()).isEqualTo("Mabel");
                assertThat(rr.getFrom()).isEqualTo(record.getFrom());
                assertThat(rr.getId1()).isEqualTo(record.getId1());
                assertThat(rr.getId2()).isEqualTo(record.getId2());
                assertThat(rr.getId5()).isEqualTo(record.getId5());
                assertThat(rr.getId6()).isEqualTo(record.getId6());
                assertThat(rr.getId7()).isEqualTo(record.getId7());
                assertThat(rr.getSecondFirstName()).isEqualTo(record.getSecondFirstName());
                assertThat(rr.getThirdFirstName()).isEqualTo(record.getThirdFirstName());
            });
        }
    }

    @Test
    public void testAwfulTableUpdateByPrimaryKey() {
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
            Integer generatedCustomerId = record.getCustomerId();

            record.setId1(11);
            record.setId2(22);

            int rows = mapper.updateByPrimaryKey(record);
            assertThat(rows).isEqualTo(1);

            Optional<AwfulTable> returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.getCustomerId()).isEqualTo(generatedCustomerId);
                assertThat(rr.geteMail()).isEqualTo(record.geteMail());
                assertThat(rr.getEmailaddress()).isEqualTo(record.getEmailaddress());
                assertThat(rr.getFirstFirstName()).isEqualTo(record.getFirstFirstName());
                assertThat(rr.getFrom()).isEqualTo(record.getFrom());
                assertThat(rr.getId1()).isEqualTo(record.getId1());
                assertThat(rr.getId2()).isEqualTo(record.getId2());
                assertThat(rr.getId5()).isEqualTo(record.getId5());
                assertThat(rr.getId6()).isEqualTo(record.getId6());
                assertThat(rr.getId7()).isEqualTo(record.getId7());
                assertThat(rr.getSecondFirstName()).isEqualTo(record.getSecondFirstName());
                assertThat(rr.getThirdFirstName()).isEqualTo(record.getThirdFirstName());
            });
        }
    }

    @Test
    public void testAwfulTableUpdateByPrimaryKeySelective() {
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
            Integer generatedCustomerId = record.getCustomerId();

            int rows = mapper.update(d ->
                    d.set(AWFUL_TABLE.ID1).equalTo(11)
                            .set(AWFUL_TABLE.ID2).equalTo(22)
                            .where(AWFUL_TABLE.CUSTOMER_ID, isEqualTo(generatedCustomerId))
            );

            assertThat(rows).isEqualTo(1);

            Optional<AwfulTable> returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.getCustomerId()).isEqualTo(generatedCustomerId);
                assertThat(rr.geteMail()).isEqualTo(record.geteMail());
                assertThat(rr.getEmailaddress()).isEqualTo(record.getEmailaddress());
                assertThat(rr.getFirstFirstName()).isEqualTo(record.getFirstFirstName());
                assertThat(rr.getFrom()).isEqualTo(record.getFrom());
                assertThat(rr.getId1()).isEqualTo(11);
                assertThat(rr.getId2()).isEqualTo(22);
                assertThat(rr.getId5()).isEqualTo(record.getId5());
                assertThat(rr.getId6()).isEqualTo(record.getId6());
                assertThat(rr.getId7()).isEqualTo(record.getId7());
                assertThat(rr.getSecondFirstName()).isEqualTo(record.getSecondFirstName());
                assertThat(rr.getThirdFirstName()).isEqualTo(record.getThirdFirstName());
            });
        }
    }

    @Test
    public void testAwfulTableDeleteByPrimaryKey() {
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
            Integer generatedCustomerId = record.getCustomerId();

            int rows = mapper.deleteByPrimaryKey(generatedCustomerId);
            assertThat(rows).isEqualTo(1);

            List<AwfulTable> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(0);
        }
    }

    @Test
    public void testAwfulTableDelete() {
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

            List<AwfulTable> answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(2);

            int rows = mapper.delete(dsl ->
                    dsl.where(AWFUL_TABLE.E_MAIL, isLike("fred@%")));
            assertThat(rows).isEqualTo(1);

            answer = mapper.select(SelectDSLCompleter.allRows());
            assertThat(answer.size()).isEqualTo(1);
        }
    }

    @Test
    public void testAwfulTableSelectByPrimaryKey() {
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

            AwfulTable record1 = new AwfulTable();
            record1.seteMail("fred2@fred.com");
            record1.setEmailaddress("alsofred2@fred.com");
            record1.setFirstFirstName("fred11");
            record1.setFrom("from from field");
            record1.setId1(11);
            record1.setId2(22);
            record1.setId5(55);
            record1.setId6(66);
            record1.setId7(77);
            record1.setSecondFirstName("fred22");
            record1.setThirdFirstName("fred33");

            mapper.insert(record1);
            Integer generatedKey = record1.getCustomerId();

            Optional<AwfulTable> returnedRecord = mapper.selectByPrimaryKey(generatedKey);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.getCustomerId()).isEqualTo(record1.getCustomerId());
                assertThat(rr.geteMail()).isEqualTo(record1.geteMail());
                assertThat(rr.getEmailaddress()).isEqualTo(record1.getEmailaddress());
                assertThat(rr.getFirstFirstName()).isEqualTo(record1.getFirstFirstName());
                assertThat(rr.getFrom()).isEqualTo(record1.getFrom());
                assertThat(rr.getId1()).isEqualTo(record1.getId1());
                assertThat(rr.getId2()).isEqualTo(record1.getId2());
                assertThat(rr.getId5()).isEqualTo(record1.getId5());
                assertThat(rr.getId6()).isEqualTo(record1.getId6());
                assertThat(rr.getId7()).isEqualTo(record1.getId7());
                assertThat(rr.getSecondFirstName()).isEqualTo(record1.getSecondFirstName());
                assertThat(rr.getThirdFirstName()).isEqualTo(record1.getThirdFirstName());
            });
        }
    }

    @Test
    public void testAwfulTableSelectByExampleLike() {
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
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.FIRST_FIRST_NAME, isLike("b%"))
                    .orderBy(AWFUL_TABLE.CUSTOMER_ID));
            assertThat(answer.size()).isEqualTo(3);
            AwfulTable returnedRecord = answer.get(0);
            assertThat(returnedRecord.getId1()).isEqualTo(1111);
            assertThat(returnedRecord.getId2()).isEqualTo(2222);
            assertThat(returnedRecord.getCustomerId()).isEqualTo(60);

            returnedRecord = answer.get(1);
            assertThat(returnedRecord.getId1()).isEqualTo(11111);
            assertThat(returnedRecord.getId2()).isEqualTo(22222);
            assertThat(returnedRecord.getCustomerId()).isEqualTo(61);

            returnedRecord = answer.get(2);
            assertThat(returnedRecord.getId1()).isEqualTo(111111);
            assertThat(returnedRecord.getId2()).isEqualTo(222222);
            assertThat(returnedRecord.getCustomerId()).isEqualTo(62);
        }
    }

    @Test
    public void testAwfulTableSelectByExampleLikeWithMultiInsert() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {

            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            List<AwfulTable> records = new ArrayList<>();

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
            records.add(record);

            record = new AwfulTable();
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            records.add(record);

            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            records.add(record);

            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            records.add(record);

            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            records.add(record);

            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            records.add(record);

            int rowsInserted = mapper.insertMultiple(records);
            assertThat(rowsInserted).isEqualTo(6);

            // check generated keys
            assertThat(records.get(0).getCustomerId()).isEqualTo(57);
            assertThat(records.get(1).getCustomerId()).isEqualTo(58);
            assertThat(records.get(2).getCustomerId()).isEqualTo(59);
            assertThat(records.get(3).getCustomerId()).isEqualTo(60);
            assertThat(records.get(4).getCustomerId()).isEqualTo(61);
            assertThat(records.get(5).getCustomerId()).isEqualTo(62);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.FIRST_FIRST_NAME, isLike("b%"))
                    .orderBy(AWFUL_TABLE.CUSTOMER_ID));
            assertThat(answer.size()).isEqualTo(3);
            AwfulTable returnedRecord = answer.get(0);
            assertThat(returnedRecord.getId1()).isEqualTo(1111);
            assertThat(returnedRecord.getId2()).isEqualTo(2222);
            assertThat(returnedRecord.getCustomerId()).isEqualTo(60);

            returnedRecord = answer.get(1);
            assertThat(returnedRecord.getId1()).isEqualTo(11111);
            assertThat(returnedRecord.getId2()).isEqualTo(22222);
            assertThat(returnedRecord.getCustomerId()).isEqualTo(61);

            returnedRecord = answer.get(2);
            assertThat(returnedRecord.getId1()).isEqualTo(111111);
            assertThat(returnedRecord.getId2()).isEqualTo(222222);
            assertThat(returnedRecord.getCustomerId()).isEqualTo(62);
        }
    }

    @Test
    public void testAwfulTableSelectByExampleNotLike() {
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
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.FIRST_FIRST_NAME, isNotLike("b%"))
                    .orderBy(AWFUL_TABLE.CUSTOMER_ID));
            assertThat(answer.size()).isEqualTo(3);
            AwfulTable returnedRecord = answer.get(0);
            assertThat(returnedRecord.getId1()).isEqualTo(1);
            assertThat(returnedRecord.getId2()).isEqualTo(2);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.getId1()).isEqualTo(11);
            assertThat(returnedRecord.getId2()).isEqualTo(22);
            returnedRecord = answer.get(2);
            assertThat(returnedRecord.getId1()).isEqualTo(111);
            assertThat(returnedRecord.getId2()).isEqualTo(222);
        }
    }

    @Test
    public void testAwfulTableSelectByExampleComplexLike() {
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
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.FIRST_FIRST_NAME, isLike("b%"), and(AWFUL_TABLE.ID2, isEqualTo(222222)))
                    .or(AWFUL_TABLE.FIRST_FIRST_NAME, isLike("wi%"))
                    .orderBy(AWFUL_TABLE.CUSTOMER_ID));

            assertThat(answer.size()).isEqualTo(2);
            AwfulTable returnedRecord = answer.get(0);
            assertThat(returnedRecord.getId1()).isEqualTo(11);
            assertThat(returnedRecord.getId2()).isEqualTo(22);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.getId1()).isEqualTo(111111);
            assertThat(returnedRecord.getId2()).isEqualTo(222222);
        }
    }

    @Test
    public void testAwfulTableSelectByExampleIn() {
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
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.ID1, isIn(1, 11))
                    .orderBy(AWFUL_TABLE.CUSTOMER_ID));

            assertThat(answer.size()).isEqualTo(2);
            AwfulTable returnedRecord = answer.get(0);
            assertThat(returnedRecord.getId1()).isEqualTo(1);
            assertThat(returnedRecord.getId2()).isEqualTo(2);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.getId1()).isEqualTo(11);
            assertThat(returnedRecord.getId2()).isEqualTo(22);
        }
    }

    @Test
    public void testAwfulTableSelectByExampleBetween() {
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
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);

            List<AwfulTable> answer = mapper.select(dsl ->
                    dsl.where(AWFUL_TABLE.ID1, isBetween(1).and(1000)));
            assertThat(answer.size()).isEqualTo(3);
        }
    }

    @Test
    public void testAwfulTableSelectByExampleNoCriteria() {
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
            record.seteMail("wilma@wilma.com");
            record.setEmailaddress("alsoWilma@wilma.com");
            record.setFirstFirstName("wilma1");
            record.setFrom("from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("wilma2");
            record.setThirdFirstName("wilma3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("pebbles@pebbles.com");
            record.setEmailaddress("alsoPebbles@pebbles.com");
            record.setFirstFirstName("pebbles1");
            record.setFrom("from field");
            record.setId1(111);
            record.setId2(222);
            record.setId5(555);
            record.setId6(666);
            record.setId7(777);
            record.setSecondFirstName("pebbles2");
            record.setThirdFirstName("pebbles3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("barney@barney.com");
            record.setEmailaddress("alsoBarney@barney.com");
            record.setFirstFirstName("barney1");
            record.setFrom("from field");
            record.setId1(1111);
            record.setId2(2222);
            record.setId5(5555);
            record.setId6(6666);
            record.setId7(7777);
            record.setSecondFirstName("barney2");
            record.setThirdFirstName("barney3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("betty@betty.com");
            record.setEmailaddress("alsoBetty@betty.com");
            record.setFirstFirstName("betty1");
            record.setFrom("from field");
            record.setId1(11111);
            record.setId2(22222);
            record.setId5(55555);
            record.setId6(66666);
            record.setId7(77777);
            record.setSecondFirstName("betty2");
            record.setThirdFirstName("betty3");
            mapper.insert(record);

            record = new AwfulTable();
            record.seteMail("bammbamm@bammbamm.com");
            record.setEmailaddress("alsoBammbamm@bammbamm.com");
            record.setFirstFirstName("bammbamm1");
            record.setFrom("from field");
            record.setId1(111111);
            record.setId2(222222);
            record.setId5(555555);
            record.setId6(666666);
            record.setId7(777777);
            record.setSecondFirstName("bammbamm2");
            record.setThirdFirstName("bammbamm3");
            mapper.insert(record);

            List<AwfulTable> answer = mapper.select(
                    SelectDSLCompleter.allRowsOrderedBy(AWFUL_TABLE.CUSTOMER_ID.descending()));
            assertThat(answer.size()).isEqualTo(6);
            AwfulTable returnedRecord = answer.get(0);
            assertThat(returnedRecord.getId1()).isEqualTo(111111);
            returnedRecord = answer.get(1);
            assertThat(returnedRecord.getId1()).isEqualTo(11111);
            returnedRecord = answer.get(2);
            assertThat(returnedRecord.getId1()).isEqualTo(1111);
            returnedRecord = answer.get(3);
            assertThat(returnedRecord.getId1()).isEqualTo(111);
            returnedRecord = answer.get(4);
            assertThat(returnedRecord.getId1()).isEqualTo(11);
            returnedRecord = answer.get(5);
            assertThat(returnedRecord.getId1()).isEqualTo(1);
        }
    }

    @Test
    public void testAwfulTablecount() {
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

            long rows = mapper.count(dsl ->
                    dsl.where(AWFUL_TABLE.E_MAIL, isLike("fred@%")));
            assertThat(rows).isEqualTo(1);

            rows = mapper.count(dsl -> dsl);
            assertThat(rows).isEqualTo(2);
        }
    }

    @Test
    public void testTranslationTable() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            TranslationMapper mapper = sqlSession.getMapper(TranslationMapper.class);

            Translation t1 = new Translation(1, "Spanish");
            mapper.insert(t1);

            Translation t2 = new Translation(2, "French");
            mapper.insert(t2);

            Optional<Translation> returnedRecord = mapper.selectByPrimaryKey(2);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.id()).isEqualTo(t2.id());
                assertThat(rr.translation()).isEqualTo(t2.translation());
            });

            Translation t3 = new Translation(2, "Italian");
            mapper.updateByPrimaryKey(t3);

            returnedRecord = mapper.selectByPrimaryKey(2);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.id()).isEqualTo(t3.id());
                assertThat(rr.translation()).isEqualTo(t3.translation());
            });
        }
    }

    @Test
    public void testIdTable() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            IdMapper mapper = sqlSession.getMapper(IdMapper.class);

            Id idSpanish = new Id();
            idSpanish.setId(1);
            idSpanish.setDescription("Spanish");
            mapper.insert(idSpanish);

            Id idFrench = new Id();
            idFrench.setId(2);
            idFrench.setDescription("French");
            mapper.insert(idFrench);

            Optional<Id> returnedRecord = mapper.selectByPrimaryKey(2);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.getId()).isEqualTo(idFrench.getId());
                assertThat(rr.getDescription()).isEqualTo(idFrench.getDescription());
            });

            Id idItalian = new Id();
            idItalian.setId(2);
            idItalian.setDescription("Italian");
            mapper.updateByPrimaryKey(idItalian);

            returnedRecord = mapper.selectByPrimaryKey(2);

            assertThat(returnedRecord).hasValueSatisfying(rr -> {
                assertThat(rr.getId()).isEqualTo(idItalian.getId());
                assertThat(rr.getDescription()).isEqualTo(idItalian.getDescription());
            });

            List<Id> allIds = mapper.select(SelectDSLCompleter.allRows());
            assertThat(allIds).containsExactlyInAnyOrder(idSpanish, idItalian);
        }
    }
}
