package org.apache.avro.io;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class BinaryDataEncodeIntTest {
  private final int n;
  private final byte[] buf;
  private final int pos;
  private final Class<? extends Exception> expectedException;
  private final int expectedReturn;
  private static final Logger LOG = LoggerFactory.getLogger(BinaryDataEncodeIntTest.class);


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
        {0, null, 0, NullPointerException.class, 0},

        // Casi con buffer capacità 0
        {0, new byte[0], 0, ArrayIndexOutOfBoundsException.class, 0},
        {1, new byte[0], 0, ArrayIndexOutOfBoundsException.class, 0},

        {0, new byte[5], -1, ArrayIndexOutOfBoundsException.class, 0},

        {-1, new byte[5], 0, null, 1},
        {0, new byte[5], 0, null, 1},
        {1, new byte[5], 0, null, 1},
        {Integer.MAX_VALUE, new byte[5], 0, null, 5},

        {-1, new byte[5], 1, null, 1},
        {0, new byte[5], 1, null, 1},
        {1, new byte[5], 1, null, 1},
        {Integer.MAX_VALUE, new byte[5], 1, ArrayIndexOutOfBoundsException.class, 0},

        {-1, new byte[5], 4, null, 1},
        {0, new byte[5], 4, null, 1},
        {1, new byte[5], 4, null, 1},
        {Integer.MAX_VALUE, new byte[5], 4, ArrayIndexOutOfBoundsException.class, 0},

        {0, new byte[5], 5, ArrayIndexOutOfBoundsException.class, 0},

        //After JaCoCo
        {129, new byte[5], 0, null, 2},
        {32000, new byte[5], 0, null, 3},
        {2097153, new byte[5], 0, null, 4},

    });
  }


  @Test
  public void testEncodeInt() {
    try {
      LOG.info("------------------- New Test --------------------\n");

      int result = BinaryData.encodeInt(n, buf, pos);

      if (expectedException != null) {
        Assert.fail("Expected exception " + expectedException.getName() + " not thrown");
      }

      Assert.assertEquals(expectedReturn, result);

      // After pit

      byte[] bufExpected;

      if ((n == Integer.MAX_VALUE || n == 1 || n == 32000 || n == 2097152 || n == 0 || n == -1 || n == 129)) {
        if (n == Integer.MAX_VALUE) {
          bufExpected = new byte[]{-2, -1, -1, -1, 15};
        } else if (n == 1) {
          bufExpected = new byte[]{2, 0, 0, 0, 0};
        } else if (n == 0) {
          bufExpected = new byte[]{0, 0, 0, 0, 0};
        } else if (n == -1) {
          bufExpected = new byte[]{1, 0, 0, 0, 0};
        } else if (n == 32000) {
          bufExpected = new byte[]{-128, -12, 3, 0, 0};
        } else if (n == 129) {
          bufExpected = new byte[]{-126, 2, 0, 0, 0};
        } else {
          bufExpected = new byte[]{-128, -128, -128, 2, 0};
        }

        byte[] bufShifted = shiftBufValues(bufExpected, pos);
        String actualBufToString = Arrays.toString(buf);
        String bufExpectedToString = Arrays.toString(bufShifted);
        Assert.assertEquals(bufExpectedToString, actualBufToString);

      }
    } catch (Exception e) {
      Assert.assertEquals(expectedException, e.getClass());
    }
  }

  private byte[] shiftBufValues(byte[] buf, int pos) {

    byte[] shiftedBuf = new byte[buf.length];

    for (int i = 0; i < buf.length; i++) {
      int newPos = i + pos;
      if (newPos >= 0 && newPos < buf.length) {
        shiftedBuf[newPos] = buf[i];
      }
    }
    return shiftedBuf;
  }
}

