package org.apache.avro.io;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class BinaryDataEncodeIntTest {
  private final int n;
  private final byte[] buf;
  private final int pos;
  private final Class<? extends Exception> expectedException;
  private final int expectedReturn;

  public BinaryDataEncodeIntTest(int n, byte[] buf, int pos, Class<? extends Exception> expectedException, int expectedReturn) {
    this.n = n;
    this.buf = buf;
    this.pos = pos;
    this.expectedException = expectedException;
    this.expectedReturn = expectedReturn;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> parameters() {
    return Arrays.asList(new Object[][]{

        // n, buf, pos, expectedException, expectedReturn

        // Casi con buffer null
        {-1, null, 0, NullPointerException.class, 0},
        {0, null, 0, NullPointerException.class, 0},
        {1, null, 0, NullPointerException.class, 0},
        {Integer.MAX_VALUE, null, 0, NullPointerException.class, 0},

        // Casi con buffer vuoto (capacità 0)
        {-1, new byte[0], 0, ArrayIndexOutOfBoundsException.class, 0},
        {0, new byte[0], 0, ArrayIndexOutOfBoundsException.class, 0},
        {1, new byte[0], 0, ArrayIndexOutOfBoundsException.class, 0},
        {Integer.MAX_VALUE, new byte[0], 0, ArrayIndexOutOfBoundsException.class, 0},

        // Buffer di capacità 5
        {-1, new byte[5], -1, ArrayIndexOutOfBoundsException.class, 0},
        {0, new byte[5], -1, ArrayIndexOutOfBoundsException.class, 0},
        {1, new byte[5], -1, ArrayIndexOutOfBoundsException.class, 0},
        {Integer.MAX_VALUE, new byte[5], -1, ArrayIndexOutOfBoundsException.class, 0},

        {-1, new byte[5], 0, null, 1}, // Supponendo che -1 codifichi 1 byte
        {0, new byte[5], 0, null, 1},  // Supponendo che 0 codifichi 1 byte
        {1, new byte[5], 0, null, 1},  // Supponendo che 1 codifichi 1 byte
        {Integer.MAX_VALUE, new byte[5], 0, null, 5}, // MAX_INT codificato in 5 byte

        {-1, new byte[5], 1, null, 1}, // Valori validi da posizioni > 0
        {0, new byte[5], 1, null, 1},
        {1, new byte[5], 1, null, 1},
        {Integer.MAX_VALUE, new byte[5], 1, ArrayIndexOutOfBoundsException.class, 0},

        {-1, new byte[5], 4, null, 1}, // Test al limite del buffer (pos = 4)
        {0, new byte[5], 4, null, 1},
        {1, new byte[5], 4, null, 1},
        {Integer.MAX_VALUE, new byte[5], 4, ArrayIndexOutOfBoundsException.class, 0},

        {-1, new byte[5], 5, ArrayIndexOutOfBoundsException.class, 0}, // Oltre il limite del buffer
        {0, new byte[5], 5, ArrayIndexOutOfBoundsException.class, 0},
        {1, new byte[5], 5, ArrayIndexOutOfBoundsException.class, 0},
        {Integer.MAX_VALUE, new byte[5], 5, ArrayIndexOutOfBoundsException.class, 0},

    });
  }


  @Test
  public void testEncodeInt() {
    try {
      int result = BinaryData.encodeInt(n, buf, pos);

      if (expectedException != null) {
        Assert.fail("Expected exception " + expectedException.getName() + " not thrown");
      }

      Assert.assertEquals(expectedReturn, result);

    } catch (Exception e) {
      Assert.assertEquals(expectedException, e.getClass());
    }
  }
}
