package org.apache.avro;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.avro.Schema.Type.LONG;
import static org.apache.avro.SchemaCompatibility.lookupWriterField;
import static org.apache.avro.util.UtilsMethods.*;

@RunWith(Parameterized.class)
public class SchemaCompatibilityLookupWriterFieldTest {
  private Schema writerSchema;
  private Schema.Field readerField;
  private Result expectedResult;
  private Class<? extends Exception> expectedException;
  private static final Logger LOG = LoggerFactory.getLogger(SchemaCompatibilityLookupWriterFieldTest.class);


  public SchemaCompatibilityLookupWriterFieldTest(Schema writerSchema, Schema.Field readerField, Result expectedResult, Class<? extends Exception> expectedException) {
    this.writerSchema = writerSchema;
    this.readerField = readerField;
    this.expectedResult = expectedResult;
    this.expectedException = expectedException;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> parameters() {
    return Arrays.asList(new Object[][]{
        {null, getRecord("Record1").getFields().get(0), null, NullPointerException.class},
        {getRecord("Record1"), null, null, NullPointerException.class},
        {getCompleteRecord("Record1", Arrays.asList("field1", "field2")), getField("field1", null), Result.CORRECT_FIELD, null},
        {getCompleteRecord("Record1", Arrays.asList("field1", "field2")), getField("field3", Arrays.asList("field1")), Result.ALIAS_FIELD, null},
        {getCompleteRecord("Record1", Arrays.asList("field1", "field2")), getField("field3", Arrays.asList("field4")), Result.INCORRECT_FIELD, null},
        //{getInvalidSchema(), getField("field1", null), null, RuntimeException.class}, --> Problemi PIT
        {getCompleteRecord("Record1", Arrays.asList("field1", "field2")), getInvalidField(), null, RuntimeException.class},

        // After JaCoCo
        //{Schema.create(STRING), getField("field1", null), null, AssertionError.class}, --> Problemi PIT
        {getCompleteRecord("Record1", Arrays.asList("field1", "field2")), getField("field3", Arrays.asList("field1", "field2")), null, AvroRuntimeException.class},
    });
  }

  public static Schema.Field getField(String fieldName, List<String> aliasName) {
    Schema longSchema = Schema.create(LONG);
    Schema.Field field = new Schema.Field(fieldName, longSchema, null, null);

    if (aliasName != null) {
      for (String alias : aliasName) {
        field.addAlias(alias);
      }
    }
    return field;
  }


  public static Schema getCompleteRecord(String recordName, List<String> fieldsName) {

    List<Schema.Field> fields = new ArrayList<>();

    for (String fieldName : fieldsName) {
      Schema.Field field = getField(fieldName, null);
      fields.add(field);
    }

    Schema schema = Schema.createRecord(recordName, null, null, false, fields);
    schema.addAlias("oldRecord");
    return schema;
  }

  @Test
  public void testCheckReaderWriter() {
    try {
      LOG.info("------ NEW TEST --------");
      Schema.Field resultField = lookupWriterField(writerSchema, readerField);


      if (expectedException != null) {
        Assert.fail("Expected exception: " + expectedException.getName() + " but none was thrown.");
      }

      if (expectedResult == Result.CORRECT_FIELD) {
        Assert.assertEquals(readerField, resultField);

      } else if (expectedResult == Result.ALIAS_FIELD) {
        Assert.assertTrue(
            "Il nome del campo di risultato non è un alias valido del campo del lettore.",
            readerField.aliases().contains(resultField.name()));
      } else {
        Assert.assertNull(resultField);
      }

    } catch (AssertionError | Exception ae) {
      Assert.assertEquals(expectedException, ae.getClass());
    }
  }

  public enum Result {
    CORRECT_FIELD, INCORRECT_FIELD, ALIAS_FIELD,
  }
}
