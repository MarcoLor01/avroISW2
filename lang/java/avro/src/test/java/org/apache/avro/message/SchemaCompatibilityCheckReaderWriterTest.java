package org.apache.avro.message;

import org.apache.avro.Schema;
import org.apache.avro.SchemaCompatibility;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.avro.Schema.Type.*;
import static org.apache.avro.SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE;
import static org.apache.avro.SchemaCompatibility.checkReaderWriterCompatibility;

@RunWith(Parameterized.class)
public class SchemaCompatibilityCheckReaderWriterTest {

  private Schema reader;
  private Schema writer;
  private SchemaCompatibility.SchemaCompatibilityType expectedCompatibilityType;
  private final Class<? extends Exception> expectedException;

  public SchemaCompatibilityCheckReaderWriterTest(Schema reader, Schema writer, SchemaCompatibility.SchemaCompatibilityType expectedCompatibilityType, Class<? extends Exception> expectedException) {
    this.reader = reader;
    this.writer = writer;
    this.expectedCompatibilityType = expectedCompatibilityType;
    this.expectedException = expectedException;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> parameters() {
    return Arrays.asList(new Object[][]{

        //{null, null, null, NullPointerException.class},
        {Schema.create(NULL), Schema.create(NULL), COMPATIBLE, null},
        {Schema.create(BYTES), Schema.create(BYTES), COMPATIBLE, null},
        {Schema.create(INT), Schema.create(INT), COMPATIBLE, null},
        {Schema.create(LONG), Schema.create(LONG), COMPATIBLE, null},
        {Schema.create(FLOAT), Schema.create(FLOAT), COMPATIBLE, null},
        //{Schema.create(DOUBLE), Schema.create(DOUBLE), COMPATIBLE, null},
        //{Schema.create(BOOLEAN), Schema.create(BOOLEAN), COMPATIBLE, null},
        //{Schema.create(RECORD), Schema.create(RECORD), COMPATIBLE, null},
        //{Schema.create(ENUM), Schema.create(ENUM), COMPATIBLE, null},
        //{Schema.create(ARRAY), Schema.create(ARRAY), COMPATIBLE, null},
        //{Schema.create(MAP), Schema.create(MAP), COMPATIBLE, null},
        //{Schema.create(UNION), Schema.create(UNION), COMPATIBLE, null},
        //{Schema.create(FIXED), Schema.create(FIXED), COMPATIBLE, null},
        //{Schema.create(STRING), Schema.create(STRING), COMPATIBLE, null},
        //{Schema.create(LONG), Schema.create(INT), COMPATIBLE, null},
        //{Schema.create(FLOAT), Schema.create(INT), COMPATIBLE, null},
        //{Schema.create(DOUBLE), Schema.create(INT), COMPATIBLE, null},
        //{Schema.create(FLOAT), Schema.create(LONG), COMPATIBLE, null},
        //{Schema.create(DOUBLE), Schema.create(FLOAT), COMPATIBLE, null},
        //{Schema.create(BYTES), Schema.create(STRING), COMPATIBLE, null},
        //{Schema.create(STRING), Schema.create(BYTES), COMPATIBLE, null},
    });
  }


  @Test
  public void testCheckReaderWriter() {
    try {
      SchemaCompatibility.SchemaPairCompatibility schemaPairCompatibility = checkReaderWriterCompatibility(reader, writer);

      if (expectedException != null) {
        Assert.fail("Expected exception: " + expectedException.getName() + " but none was thrown.");
      }

      Assert.assertNotNull(schemaPairCompatibility);
      Assert.assertEquals(schemaPairCompatibility.getType(), expectedCompatibilityType);
    } catch (Exception e) {
      Assert.assertEquals(expectedException, e.getClass());
    }
  }

}
