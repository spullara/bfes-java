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
    BFES bfes = Main.getBfes(dim);
    float[] vec = Main.getRandomVector(dim);
    List<Score> result = bfes.search(vec, k);
    System.out.println(result);
  }

  @Test
  @Ignore
  public void testBench() {
    int dim = 512;
    int k = 10;
    BFES bfes = Main.getBfes(dim);
    float[] vec = Main.getRandomVector(dim);
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

  @Test
  @Ignore
  public void testQuadmasterXLII() {
    int dim = 128;
    int k = 1;
    BFES bfes = new BFES(dim);
    for (int i1 = 0; i1 < 1_000_000; i1++) {
      float[] vec = new float[dim];
      for (int i = 0; i < dim; i++) {
        vec[i] = Main.random.nextFloat();
      }
      bfes.add(vec);
    }
    float[] vec = new float[dim];
    for (int i1 = 0; i1 < dim; i1++) {
      vec[i1] = Main.random.nextFloat();
    }
    long start = System.currentTimeMillis();
    for (int i = 0; i < 10_000; i++) {
      bfes.search(vec, k);
    }
    long end = System.currentTimeMillis();
    long diff = end - start;
    System.out.println((double)(diff) / 10_000 + " ms per search, total " + diff + "ms");
  }

}
