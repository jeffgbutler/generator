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
package mbg.test.mb3.mixed.flat;

import static mbg.test.common.util.TestUtilities.blobsAreEqual;
import static mbg.test.common.util.TestUtilities.generateRandomBlob;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import mbg.test.mb3.generated.mixed.flat.mapper.AwfulTableMapper;
import mbg.test.mb3.generated.mixed.flat.mapper.FieldsblobsMapper;
import mbg.test.mb3.generated.mixed.flat.mapper.FieldsonlyMapper;
import mbg.test.mb3.generated.mixed.flat.mapper.PkblobsMapper;
import mbg.test.mb3.generated.mixed.flat.mapper.PkfieldsMapper;
import mbg.test.mb3.generated.mixed.flat.mapper.PkfieldsblobsMapper;
import mbg.test.mb3.generated.mixed.flat.mapper.PkonlyMapper;
import mbg.test.mb3.generated.mixed.flat.model.AwfulTable;
import mbg.test.mb3.generated.mixed.flat.model.AwfulTableExample;
import mbg.test.mb3.generated.mixed.flat.model.Fieldsblobs;
import mbg.test.mb3.generated.mixed.flat.model.FieldsblobsExample;
import mbg.test.mb3.generated.mixed.flat.model.Fieldsonly;
import mbg.test.mb3.generated.mixed.flat.model.FieldsonlyExample;
import mbg.test.mb3.generated.mixed.flat.model.Pkblobs;
import mbg.test.mb3.generated.mixed.flat.model.PkblobsExample;
import mbg.test.mb3.generated.mixed.flat.model.Pkfields;
import mbg.test.mb3.generated.mixed.flat.model.PkfieldsExample;
import mbg.test.mb3.generated.mixed.flat.model.Pkfieldsblobs;
import mbg.test.mb3.generated.mixed.flat.model.PkfieldsblobsExample;
import mbg.test.mb3.generated.mixed.flat.model.Pkonly;
import mbg.test.mb3.generated.mixed.flat.model.PkonlyExample;

/**
 * @author Jeff Butler
 */
public class UpdateByExampleTest extends AbstractMixedFlatTest {

    @Test
    public void testFieldsOnlyUpdateByExampleSelective() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);

            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);

            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);

            record = new Fieldsonly();
            record.setDoublefield(99d);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldGreaterThan(5);

            int rows = mapper.updateByExampleSelective(record, example);
            assertEquals(2, rows);

            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(5);
            List<Fieldsonly> answer = mapper.selectByExample(example);
            assertEquals(1, answer.size());
            record = answer.get(0);
            assertEquals(11.22, record.getDoublefield(), 0.001);
            assertEquals(33.44, record.getFloatfield(), 0.001);
            assertEquals(5, record.getIntegerfield().intValue());

            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(8);
            answer = mapper.selectByExample(example);
            assertEquals(1, answer.size());
            record = answer.get(0);
            assertEquals(99d, record.getDoublefield(), 0.001);
            assertEquals(66.77, record.getFloatfield(), 0.001);
            assertEquals(8, record.getIntegerfield().intValue());

            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(9);
            answer = mapper.selectByExample(example);
            assertEquals(1, answer.size());
            record = answer.get(0);
            assertEquals(99d, record.getDoublefield(), 0.001);
            assertEquals(100.111, record.getFloatfield(), 0.001);
            assertEquals(9, record.getIntegerfield().intValue());
        }
    }

    @Test
    public void testFieldsOnlyUpdateByExample() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);

            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);

            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);

            record = new Fieldsonly();
            record.setIntegerfield(22);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldEqualTo(5);

            int rows = mapper.updateByExample(record, example);
            assertEquals(1, rows);

            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(22);
            List<Fieldsonly> answer = mapper.selectByExample(example);
            assertEquals(1, answer.size());
            record = answer.get(0);
            assertNull(record.getDoublefield());
            assertNull(record.getFloatfield());
            assertEquals(22, record.getIntegerfield().intValue());
        }
    }

    @Test
    public void testPKOnlyUpdateByExampleSelective() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);

            key = new Pkonly();
            key.setId(5);
            key.setSeqNum(6);
            mapper.insert(key);

            key = new Pkonly();
            key.setId(7);
            key.setSeqNum(8);
            mapper.insert(key);

            PkonlyExample example = new PkonlyExample();
            example.createCriteria().andIdGreaterThan(4);
            key = new Pkonly();
            key.setSeqNum(3);
            int rows = mapper.updateByExampleSelective(key, example);
            assertEquals(2, rows);

            example.clear();
            example.createCriteria()
                    .andIdEqualTo(5)
                    .andSeqNumEqualTo(3);

            long returnedRows = mapper.countByExample(example);
            assertEquals(1, returnedRows);

            example.clear();
            example.createCriteria()
                    .andIdEqualTo(7)
                    .andSeqNumEqualTo(3);

            returnedRows = mapper.countByExample(example);
            assertEquals(1, returnedRows);
        }
    }

    @Test
    public void testPKOnlyUpdateByExample() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            Pkonly key = new Pkonly();
            key.setId(1);
            key.setSeqNum(3);
            mapper.insert(key);

            key = new Pkonly();
            key.setId(5);
            key.setSeqNum(6);
            mapper.insert(key);

            key = new Pkonly();
            key.setId(7);
            key.setSeqNum(8);
            mapper.insert(key);

            PkonlyExample example = new PkonlyExample();
            example.createCriteria()
                    .andIdEqualTo(7);
            key = new Pkonly();
            key.setSeqNum(3);
            key.setId(22);
            int rows = mapper.updateByExample(key, example);
            assertEquals(1, rows);

            example.clear();
            example.createCriteria()
                    .andIdEqualTo(22)
                    .andSeqNumEqualTo(3);

            long returnedRows = mapper.countByExample(example);
            assertEquals(1, returnedRows);
        }
    }

    @Test
    public void testPKFieldsUpdateByExampleSelective() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);

            record = new Pkfields();
            record.setFirstname("Bob");
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);

            mapper.insert(record);

            record = new Pkfields();
            record.setFirstname("Fred");
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andLastnameLike("J%");
            int rows = mapper.updateByExampleSelective(record, example);
            assertEquals(1, rows);

            example.clear();
            example.createCriteria()
                    .andFirstnameEqualTo("Fred")
                    .andLastnameEqualTo("Jones")
                    .andId1EqualTo(3)
                    .andId2EqualTo(4);

            long returnedRows = mapper.countByExample(example);
            assertEquals(1, returnedRows);
        }
    }

    @Test
    public void testPKFieldsUpdateByExample() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);

            record = new Pkfields();
            record.setFirstname("Bob");
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);

            mapper.insert(record);

            record = new Pkfields();
            record.setFirstname("Fred");
            record.setId1(3);
            record.setId2(4);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria()
                    .andId1EqualTo(3)
                    .andId2EqualTo(4);

            int rows = mapper.updateByExample(record, example);
            assertEquals(1, rows);

            example.clear();
            example.createCriteria()
                    .andFirstnameEqualTo("Fred")
                    .andLastnameIsNull()
                    .andId1EqualTo(3)
                    .andId2EqualTo(4);

            long returnedRows = mapper.countByExample(example);
            assertEquals(1, returnedRows);
        }
    }

    @Test
    public void testPKBlobsUpdateByExampleSelective() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            Pkblobs newRecord = new Pkblobs();
            newRecord.setBlob1(generateRandomBlob());

            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            int rows = mapper.updateByExampleSelective(newRecord, example);
            assertEquals(1, rows);

            List<Pkblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Pkblobs returnedRecord = answer.get(0);

            assertEquals(6, returnedRecord.getId().intValue());
            assertTrue(blobsAreEqual(newRecord.getBlob1(), returnedRecord.getBlob1()));
            assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        }
    }

    @Test
    public void testPKBlobsUpdateByExampleWithoutBLOBs() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            Pkblobs newRecord = new Pkblobs();
            newRecord.setId(8);

            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            int rows = mapper.updateByExample(newRecord, example);
            assertEquals(1, rows);

            List<Pkblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Pkblobs returnedRecord = answer.get(0);

            assertEquals(8, returnedRecord.getId().intValue());
            assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        }
    }

    @Test
    public void testPKBlobsUpdateByExampleWithBLOBs() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            Pkblobs newRecord = new Pkblobs();
            newRecord.setId(8);

            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            int rows = mapper.updateByExampleWithBLOBs(newRecord, example);
            assertEquals(1, rows);

            List<Pkblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Pkblobs returnedRecord = answer.get(0);

            assertEquals(8, returnedRecord.getId().intValue());
            assertNull(returnedRecord.getBlob1());
            assertNull(returnedRecord.getBlob2());
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleSelective() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);

            Pkfieldsblobs newRecord = new Pkfieldsblobs();
            newRecord.setFirstname("Fred");
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId1NotEqualTo(3);
            int rows = mapper.updateByExampleSelective(newRecord, example);
            assertEquals(1, rows);

            List<Pkfieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Pkfieldsblobs returnedRecord = answer.get(0);

            assertEquals(record.getId1(), returnedRecord.getId1());
            assertEquals(record.getId2(), returnedRecord.getId2());
            assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            assertEquals(record.getLastname(), returnedRecord.getLastname());
            assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));

        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleWithoutBLOBs() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);

            Pkfieldsblobs newRecord = new Pkfieldsblobs();
            newRecord.setId1(5);
            newRecord.setId2(8);
            newRecord.setFirstname("Fred");
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId1EqualTo(5);
            int rows = mapper.updateByExample(newRecord, example);
            assertEquals(1, rows);

            List<Pkfieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Pkfieldsblobs returnedRecord = answer.get(0);

            assertEquals(newRecord.getId1(), returnedRecord.getId1());
            assertEquals(newRecord.getId2(), returnedRecord.getId2());
            assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            assertNull(returnedRecord.getLastname());
            assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));

        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleWithBLOBs() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);

            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);

            Pkfieldsblobs newRecord = new Pkfieldsblobs();
            newRecord.setId1(3);
            newRecord.setId2(8);
            newRecord.setFirstname("Fred");
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId1EqualTo(3);
            int rows = mapper.updateByExampleWithBLOBs(newRecord, example);
            assertEquals(1, rows);

            List<Pkfieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Pkfieldsblobs returnedRecord = answer.get(0);

            assertEquals(newRecord.getId1(), returnedRecord.getId1());
            assertEquals(newRecord.getId2(), returnedRecord.getId2());
            assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            assertNull(returnedRecord.getLastname());
            assertNull(returnedRecord.getBlob1());

        }
    }

    @Test
    public void testFieldsBlobsUpdateByExampleSelective() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            record = new Fieldsblobs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            Fieldsblobs newRecord = new Fieldsblobs();
            newRecord.setLastname("Doe");
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            int rows = mapper.updateByExampleSelective(newRecord, example);
            assertEquals(1, rows);

            List<Fieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Fieldsblobs returnedRecord = answer.get(0);

            assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExampleWithoutBLOBs() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            record = new Fieldsblobs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            Fieldsblobs newRecord = new Fieldsblobs();
            newRecord.setFirstname("Scott");
            newRecord.setLastname("Doe");
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            int rows = mapper.updateByExample(newRecord, example);
            assertEquals(1, rows);

            List<Fieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Fieldsblobs returnedRecord = answer.get(0);

            assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExampleWithBLOBs() {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            record = new Fieldsblobs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);

            Fieldsblobs newRecord = new Fieldsblobs();
            newRecord.setFirstname("Scott");
            newRecord.setLastname("Doe");
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            int rows = mapper.updateByExampleWithBLOBs(newRecord, example);
            assertEquals(1, rows);

            List<Fieldsblobs> answer = mapper.selectByExampleWithBLOBs(example);
            assertEquals(1, answer.size());

            Fieldsblobs returnedRecord = answer.get(0);

            assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            assertNull(returnedRecord.getBlob1());
            assertNull(returnedRecord.getBlob2());
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

            AwfulTable newRecord = new AwfulTable();
            newRecord.setFirstFirstName("Alonzo");
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria().andEMailLike("fred2@%");
            int rows = mapper.updateByExampleSelective(newRecord, example);
            assertEquals(1, rows);

            List<AwfulTable> answer = mapper.selectByExample(example);
            assertEquals(1, answer.size());

            AwfulTable returnedRecord = answer.get(0);

            assertEquals(record.getCustomerId(), returnedRecord.getCustomerId());
            assertEquals(record.geteMail(), returnedRecord.geteMail());
            assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
            assertEquals(newRecord.getFirstFirstName(), returnedRecord.getFirstFirstName());
            assertEquals(record.getFrom(), returnedRecord.getFrom());
            assertEquals(record.getId1(), returnedRecord.getId1());
            assertEquals(record.getId2(), returnedRecord.getId2());
            assertEquals(record.getId5(), returnedRecord.getId5());
            assertEquals(record.getId6(), returnedRecord.getId6());
            assertEquals(record.getId7(), returnedRecord.getId7());
            assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
            assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());

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

            AwfulTable newRecord = new AwfulTable();
            newRecord.setFirstFirstName("Alonzo");
            newRecord.setCustomerId(58);
            newRecord.setId1(111);
            newRecord.setId2(222);
            newRecord.setId5(555);
            newRecord.setId6(666);
            newRecord.setId7(777);
            AwfulTableExample example = new AwfulTableExample();
            example.createCriteria().andEMailLike("fred2@%");
            int rows = mapper.updateByExample(newRecord, example);
            assertEquals(1, rows);

            example.clear();
            example.createCriteria().andCustomerIdEqualTo(58);
            List<AwfulTable> answer = mapper.selectByExample(example);
            assertEquals(1, answer.size());

            AwfulTable returnedRecord = answer.get(0);

            assertEquals(newRecord.getCustomerId(), returnedRecord.getCustomerId());
            assertNull(returnedRecord.geteMail());
            assertNull(returnedRecord.getEmailaddress());
            assertEquals(newRecord.getFirstFirstName(), returnedRecord.getFirstFirstName());
            assertNull(returnedRecord.getFrom());
            assertEquals(newRecord.getId1(), returnedRecord.getId1());
            assertEquals(newRecord.getId2(), returnedRecord.getId2());
            assertEquals(newRecord.getId5(), returnedRecord.getId5());
            assertEquals(newRecord.getId6(), returnedRecord.getId6());
            assertEquals(newRecord.getId7(), returnedRecord.getId7());
            assertNull(returnedRecord.getSecondFirstName());
            assertNull(returnedRecord.getThirdFirstName());
        }
    }
}
