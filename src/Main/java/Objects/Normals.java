package Objects;

/**
 * Object for retrieving NormSinV values
 * Written with influence from: https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.104.5525&rep=rep1&type=pdf
 */
public class Normals {

  public static double[] normSinV = {-2.326347874, -2.053748911, -1.880793608, -1.750686071, -1.644853627, -1.554773595, -1.475791028, -1.40507156,
      -1.340755034, -1.281551566, -1.22652812, -1.174986792, -1.126391129, -1.080319341, -1.036433389, -0.994457883, -0.954165253, -0.915365088, -0.877896295,
      -0.841621234, -0.806421247, -0.772193214, -0.738846849, -0.706302563, -0.67448975, -0.643345405, -0.612812991, -0.582841507, -0.55338472, -0.524400513,
      -0.495850347, -0.467698799, -0.439913166, -0.412463129, -0.385320466, -0.358458793, -0.331853346, -0.305480788, -0.279319034, -0.253347103, -0.227544977,
      -0.201893479, -0.176374165, -0.150969215, -0.125661347, -0.100433721, -0.075269862, -0.050153583, -0.025068908, 0, 0.02506890, 0.050153583, 0.075269862,
      0.100433721, 0.125661347, 0.150969215, 0.176374165, 0.201893479, 0.227544977, 0.253347103, 0.279319034, 0.305480788, 0.331853346, 0.358458793,
      0.385320466, 0.412463129, 0.439913166, 0.467698799, 0.495850347, 0.524400513, 0.55338472, 0.582841507, 0.612812991, 0.643345405, 0.67448975,
      0.706302563, 0.738846849, 0.772193214, 0.806421247, 0.841621234, 0.877896295, 0.915365088, 0.954165253, 0.994457883, 1.036433389, 1.080319341,
      1.126391129, 1.174986792, 1.22652812, 1.281551566, 1.340755034, 1.40507156, 1.475791028, 1.554773595, 1.644853627, 1.750686071, 1.880793608,
      2.053748911, 2.326347874};

  public Normals() {
  }

  public static double getNormSinV(double value) {
    //Must format value to array index
    // e.g. value = 0.99, array index must equal 98

    value *= 100;
    value -= 1;

    return normSinV[(int) value];
  }

}