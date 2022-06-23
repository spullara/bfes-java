package bfes;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Random;

public class BFESTest {

  public static final int ITERATIONS = 100;
  public static Random random = new Random(1337);

  @Test
  public void testSearch() {
    int dim = 512;
    int k = 10;
    BFES bfes = getBfes(dim);
    float[] vec = getQuery(dim);
    List<Score> result = bfes.search(vec, k);
    System.out.println(result);
  }

  @Test
  @Ignore
  public void testBench() {
    int dim = 512;
    int k = 10;
    BFES bfes = getBfes(dim);
    float[] vec = getQuery(dim);
    long start = System.currentTimeMillis();
    for (int i = 0; i < ITERATIONS; i++) {
      bfes.search(vec, k);
    }
    long end = System.currentTimeMillis();
    long diff = end - start;
    System.out.println((double)(diff) / ITERATIONS + " ms per search");
  }

  @Test
  @Ignore
  public void testBench2() {
    testBench();
    testBench();
    testBench();
    testBench();
  }

  private float[] getQuery(int dim) {
    float[] vec = new float[dim];
    for (int i = 0; i < dim; i++) {
      vec[i] = random.nextFloat();
    }
    return vec;
  }

  private BFES getBfes(int dim) {
    BFES bfes = new BFES(dim);
    for (int i = 0; i < 100_000; i++) {
      float[] vec = new float[dim];
      for (int j = 0; j < dim; j++) {
        vec[j] = random.nextFloat();
      }
      bfes.add(vec);
    }
    return bfes;
  }
}
