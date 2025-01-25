package org.apache.avro.io;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class BinaryDataCompareBytesTest {

  private byte[] b1;
  private int s1;
  private int l1;
  private byte[] b2;
  private int s2;
  private int l2;
  private Class<? extends Exception> expectedException;
  private int expectedResult;

  public BinaryDataCompareBytesTest(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2, Class<? extends Exception> expectedException, int expectedReturn) {
    this.b1 = b1;
    this.s1 = s1;
    this.l1 = l1;
    this.b2 = b2;
    this.s2 = s2;
    this.l2 = l2;
    this.expectedException = expectedException;
    this.expectedResult = expectedReturn;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> parameters() {
    return Arrays.asList(new Object[][]{

        // b1, s1, l1, b2, s2, l2, expectedException, expectedResult

        // Caso: b1 o b2 null
        {null, 0, 1, new byte[]{1, 2, 3, 4, 5}, 0, 1, NullPointerException.class, 0},
        {new byte[]{1, 2, 3, 4, 5}, 0, 1, null, 0, 1, NullPointerException.class, 0},

        // Caso: b1 o b2 vuoti
        {new byte[0], 0, 1, new byte[]{1, 2, 3, 4, 5}, 0, 1, ArrayIndexOutOfBoundsException.class, 0},
        {new byte[]{1, 2, 3, 4, 5}, 0, 1, new byte[0], 0, 1, ArrayIndexOutOfBoundsException.class, 0},

        // Caso: s1 o s2 = -1
        {new byte[]{1, 2, 3, 4, 5}, -1, 1, new byte[]{1, 2, 3, 4, 5}, 0, 1, ArrayIndexOutOfBoundsException.class, 0},
        {new byte[]{1, 2, 3, 4, 5}, 0, 1, new byte[]{1, 2, 3, 4, 5}, -1, 1, ArrayIndexOutOfBoundsException.class, 0},

        // Caso: l1 o l2 = -1
        //{new byte[]{1, 2, 3, 4, 5}, 0, -1, new byte[]{1, 2, 3, 4, 5}, 0, 1, ArrayIndexOutOfBoundsException.class, 0}, --> Non lancia eccezioni
        //{new byte[]{1, 2, 3, 4, 5}, 0, 1, new byte[]{1, 2, 3, 4, 5}, 0, -1, ArrayIndexOutOfBoundsException.class, 0}, --> Non lancia eccezioni

        // Caso: s1 o s2 uguali alla lunghezza
        {new byte[]{1, 2, 3, 4, 5}, 5, 1, new byte[]{1, 2, 3, 4, 5}, 0, 1, ArrayIndexOutOfBoundsException.class, 0},
        {new byte[]{1, 2, 3, 4, 5}, 0, 1, new byte[]{1, 2, 3, 4, 5}, 5, 1, ArrayIndexOutOfBoundsException.class, 0},

        // Caso: l1 o l2 eccedono la lunghezza massima
        //{new byte[]{1, 2, 3, 4, 5}, 0, 6, new byte[]{1, 2, 3, 4, 5}, 0, 1, ArrayIndexOutOfBoundsException.class, 0}, --> Nessuna eccezione sollevata
        //{new byte[]{1, 2, 3, 4, 5}, 0, 1, new byte[]{1, 2, 3, 4, 5}, 0, 6, ArrayIndexOutOfBoundsException.class, 0}, --> Nessuna eccezione sollevata

        {new byte[]{1, 2, 3, 4, 5}, 1, 3, new byte[]{1, 2, 3, 4, 5}, 1, 3, null, 0}, // Segmenti uguali

        // Caso: segmenti di lunghezza diversa
        {new byte[]{1, 2, 3, 4, 5}, 0, 4, new byte[]{1, 2, 3, 4, 5, 6, 7}, 0, 5, null, -1}, // Primo più corto
        {new byte[]{1, 2, 3, 4, 5, 6, 7}, 0, 5, new byte[]{1, 2, 3, 4, 5}, 0, 4, null, 1}, // Secondo più corto

        //after JaCoCo
        {new byte[]{1, 2, 3, 4, 5}, 0, 5, new byte[]{1, 2, 3, 4, 8}, 0, 5, null, -3},

        // Primo test pit
        {new byte[]{1, 2, 3, 4, 5, 6}, 0, 6, new byte[]{1, 2, 3, 4, 5, 6, 7}, 0, 7, null, -1}, // Primo array più corto

        // Secondo test pit
        {new byte[]{1, 2, 3, 4, 5, 6, 7}, 0, 7, new byte[]{1, 2, 3, 4, 5, 6}, 0, 6, null, 1}, // Secondo array più corto

    });
  }

  @Test
  public void testCompareBytes() {

    try {
        int result = BinaryData.compareBytes(b1, s1, l1, b2, s2, l2);

        if (expectedException != null) {
          Assert.fail("Expected exception " + expectedException.getName() + " not thrown");
        }

        Assert.assertEquals(expectedResult, result);

    } catch (Exception e) {
        Assert.assertEquals(expectedException, e.getClass());
      }
    }
}
