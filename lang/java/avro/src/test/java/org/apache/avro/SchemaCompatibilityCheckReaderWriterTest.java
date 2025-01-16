package org.apache.avro;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.avro.Schema.*;
import static org.apache.avro.Schema.Type.*;
import static org.apache.avro.SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
import static org.apache.avro.SchemaCompatibility.SchemaCompatibilityType.INCOMPATIBLE;
import static org.apache.avro.SchemaCompatibility.SchemaIncompatibilityType.*;
import static org.apache.avro.SchemaCompatibility.checkReaderWriterCompatibility;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class SchemaCompatibilityCheckReaderWriterTest {

  private Schema reader;
  private Schema writer;
  private SchemaCompatibility.SchemaCompatibilityType expectedCompatibilityType;
  private SchemaCompatibility.SchemaIncompatibilityType expectedIncompatibilityType;
  private final Class<? extends Exception> expectedException;

  public SchemaCompatibilityCheckReaderWriterTest(Schema reader, Schema writer, SchemaCompatibility.SchemaCompatibilityType expectedCompatibilityType,
                                                  SchemaCompatibility.SchemaIncompatibilityType incompatibilityType, Class<? extends Exception> expectedException) {
    this.reader = reader;
    this.writer = writer;
    this.expectedCompatibilityType = expectedCompatibilityType;
    this.expectedIncompatibilityType = incompatibilityType;
    this.expectedException = expectedException;
  }


  @Parameterized.Parameters
  public static Collection<Object[]> parameters() {
    return Arrays.asList(new Object[][]{

        //{null, null, null, NullPointerException.class},

        //TEST COMPATIBILI

        {Schema.create(NULL), Schema.create(NULL), COMPATIBLE, null, null},
        {Schema.create(BYTES), Schema.create(BYTES), COMPATIBLE, null, null},
        {Schema.create(INT), Schema.create(INT), COMPATIBLE, null, null},
        {Schema.create(LONG), Schema.create(LONG), COMPATIBLE, null, null},
        {Schema.create(FLOAT), Schema.create(FLOAT), COMPATIBLE, null, null},
        {Schema.create(DOUBLE), Schema.create(DOUBLE), COMPATIBLE, null, null},
        {Schema.create(BOOLEAN), Schema.create(BOOLEAN), COMPATIBLE, null, null},
        {getRecord("Record"), getRecord("Record"), COMPATIBLE, null, null},
        {getEnum("Example", 3), getEnum("Example", 3), COMPATIBLE, null, null},
        {getEnum("Example", 4), getEnum("Example", 3), COMPATIBLE, null, null},
        {Schema.createArray(Schema.create(Schema.Type.STRING)), Schema.createArray(Schema.create(Schema.Type.STRING)), COMPATIBLE, null, null},
        {createMap(Schema.create(Schema.Type.INT)), createMap(Schema.create(Schema.Type.INT)), COMPATIBLE, null, null},
        {createUnion(Schema.create(NULL), Schema.create(Schema.Type.STRING)), createUnion(Schema.create(NULL), Schema.create(Schema.Type.STRING)), COMPATIBLE, null, null},
        {getFixed("FixedBytes", 16), getFixed("FixedBytes", 16), COMPATIBLE, null, null},
        {Schema.create(STRING), Schema.create(STRING), COMPATIBLE, null, null},

        {Schema.create(LONG), Schema.create(INT), COMPATIBLE, null, null},
        {Schema.create(FLOAT), Schema.create(INT), COMPATIBLE, null, null},
        {Schema.create(DOUBLE), Schema.create(INT), COMPATIBLE, null, null},
        {Schema.create(FLOAT), Schema.create(LONG), COMPATIBLE, null, null},
        {Schema.create(DOUBLE), Schema.create(FLOAT), COMPATIBLE, null, null},
        {Schema.create(BYTES), Schema.create(STRING), COMPATIBLE, null, null},
        {Schema.create(STRING), Schema.create(BYTES), COMPATIBLE, null, null},
        {Schema.createArray(Schema.create(Schema.Type.STRING)), Schema.createArray(Schema.create(BYTES)), COMPATIBLE, null, null},

        //TEST INCOMPATIBILI
        {Schema.create(NULL), Schema.create(STRING), INCOMPATIBLE, TYPE_MISMATCH, null},
        {Schema.create(BYTES), Schema.create(INT), INCOMPATIBLE, TYPE_MISMATCH, null},
        {Schema.create(INT), Schema.create(LONG), INCOMPATIBLE, TYPE_MISMATCH, null},
        {Schema.create(LONG), Schema.create(FLOAT), INCOMPATIBLE, TYPE_MISMATCH, null},
        {Schema.create(STRING), Schema.create(INT), INCOMPATIBLE, TYPE_MISMATCH, null},
        {Schema.create(FLOAT), Schema.create(DOUBLE), INCOMPATIBLE, TYPE_MISMATCH, null},
        {Schema.createArray(Schema.create(Schema.Type.STRING)), Schema.createArray(Schema.create(Schema.Type.INT)), INCOMPATIBLE, TYPE_MISMATCH, null},
        {createMap(Schema.create(LONG)), createMap(Schema.create(FLOAT)), INCOMPATIBLE, TYPE_MISMATCH, null},
        {getFixed("FixedBytes", 16), getFixed("FixedBytes", 15), INCOMPATIBLE, FIXED_SIZE_MISMATCH, null},
        {getFixed("FixedBytes", 16), getFixed("FixedMultipleByte", 16), INCOMPATIBLE, NAME_MISMATCH, null},
        {getEnum("Example", 2), getEnum("Example", 3), INCOMPATIBLE, MISSING_ENUM_SYMBOLS, null},
        {getIncompatibleReaderUnion(), getIncompatibleWriterUnion(), INCOMPATIBLE, MISSING_UNION_BRANCH, null},
        {getIncompatibleReaderRecord(), getIncompatibleWriterRecord(), INCOMPATIBLE, READER_FIELD_MISSING_DEFAULT_VALUE, null},

        //INVALID
        {getInvalidSchema(), Schema.create(STRING), null, null, RuntimeException.class},
        {Schema.create(STRING), getInvalidSchema(), null, null, RuntimeException.class},


    });
  }

  public static Schema getInvalidSchema() {

    Schema mockSchema = mock(Schema.class);
    when(mockSchema.getType()).thenThrow(new RuntimeException("Invalid schema: getType is not supported"));
    return mockSchema;
  }

  public static Schema getIncompatibleWriterRecord() {
    Schema intSchema = Schema.create(INT);

    Schema writerRecord = Schema.createRecord("Person", null, null, false);
    List<Schema.Field> writerFields = new ArrayList<>();
    writerFields.add(new Schema.Field("age", intSchema, null, null));
    writerRecord.setFields(writerFields);
    return writerRecord;
  }

  public static Schema getIncompatibleReaderRecord() {
    Schema stringSchema = Schema.create(STRING);
    Schema intSchema = Schema.create(INT);

    Schema readerRecord = Schema.createRecord("Person", null, null, false);
    readerRecord.setFields(Arrays.asList(
        new Schema.Field("age", intSchema, null, null),
        new Schema.Field("name", stringSchema, null, null)
    ));

    return readerRecord;
  }


  private static Schema getIncompatibleWriterUnion() {
    Schema stringSchema = Schema.create(Schema.Type.STRING);
    Schema intSchema = Schema.create(Schema.Type.INT);

    Schema recordSchema = Schema.createRecord("Person", null, null, false);
    recordSchema.setFields(Arrays.asList(
        new Schema.Field("name", stringSchema, null, null)
    ));

    return Schema.createUnion(Arrays.asList(stringSchema, intSchema, recordSchema));
  }

  private static Schema getIncompatibleReaderUnion() {
    Schema intSchema = Schema.create(Schema.Type.INT);

    Schema recordSchema = Schema.createRecord("Person", null, null, false);
    recordSchema.setFields(Arrays.asList(
        new Schema.Field("name", Schema.create(Schema.Type.STRING), null, null),
        new Schema.Field("age", Schema.create(Schema.Type.INT), null, null) // Field added in reader
    ));

    return Schema.createUnion(Arrays.asList(intSchema, recordSchema));
  }


  public static Schema getEnum(String name, int numberElement) {
    if (numberElement <= 0) {
      throw new IllegalArgumentException("numberElement deve essere maggiore di 0");
    }
    List<String> symbols = new ArrayList<>();
    for (int i = 1; i <= numberElement; i++) {
      symbols.add("VALUE_" + i);
    }

    return Schema.createEnum(name, null, null, symbols);
  }

  public static Schema getRecord(String name) {

    Schema longSchema = Schema.create(LONG);

    Schema.Field field = new Schema.Field("value", longSchema, null, null);
    List<Schema.Field> fields = new ArrayList<>();
    fields.add(field);

    Schema schema = Schema.createRecord(name, null, null, false, fields);
    schema.addAlias("oldRecord");
    return schema;
  }

  public static Schema getFixed(String name, int size){
    return Schema.createFixed(name, null, null, size);
  }


  @Test
  public void testCheckReaderWriter() {
    try {

      SchemaCompatibility.SchemaPairCompatibility schemaPairCompatibility = checkReaderWriterCompatibility(reader, writer);

      if (expectedCompatibilityType == INCOMPATIBLE) {
        Assert.assertEquals(expectedIncompatibilityType, schemaPairCompatibility.getResult().getIncompatibilities().get(0).getType());
      }

      if (expectedException != null) {
        Assert.fail("Expected exception: " + expectedException.getName() + " but none was thrown.");
      }

      Assert.assertNotNull(schemaPairCompatibility);
      Assert.assertEquals(expectedCompatibilityType, schemaPairCompatibility.getType());

    } catch (Exception e) {
      Assert.assertEquals(expectedException, e.getClass());
    }
  }
}
