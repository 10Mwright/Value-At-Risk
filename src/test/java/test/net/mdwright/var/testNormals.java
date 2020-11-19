package test.net.mdwright.var;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.mdwright.var.objects.Normals;
import org.junit.jupiter.api.Test;

public class testNormals {

  @Test
  public static void testNormSinV() {

    assertEquals(2.326347874, Normals.getNormSinV(0.99));
    assertEquals(-2.326347874, Normals.getNormSinV(0.01));
  }
}
