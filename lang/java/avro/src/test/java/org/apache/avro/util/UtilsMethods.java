package org.apache.avro.util;

import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;

import static org.apache.avro.Schema.Type.LONG;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsMethods {

  public static Schema getRecord(String name) {

    Schema longSchema = Schema.create(LONG);

    Schema.Field field = new Schema.Field("value", longSchema, null, null);
    List<Schema.Field> fields = new ArrayList<>();
    fields.add(field);

    Schema schema = Schema.createRecord(name, null, null, false, fields);
    schema.addAlias("oldRecord");
    return schema;
  }

  public static Schema getInvalidSchema() {
    Schema mockSchema = mock(Schema.class);
    when(mockSchema.getType()).thenThrow(new RuntimeException("Invalid schema: getType is not supported"));
    return mockSchema;
  }

  public static Schema.Field getInvalidField() {
    Schema.Field mockField = mock(Schema.Field.class);
    when(mockField.name()).thenThrow(new RuntimeException("Invalid Field: name is not supported"));
    return mockField;
  }
}
