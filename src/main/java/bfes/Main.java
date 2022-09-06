package bfes;

import java.util.Random;

public class Main {
  public static Random random = new Random(1337);
  public static final int ITERATIONS = 1000;

  public static void main(String[] args) {
    int dim = 512;
    int k = 10;
    BFES bfes = getBfes(dim);
    float[] vec = getRandomVector(dim);
    long start = System.currentTimeMillis();
    for (int i = 0; i < ITERATIONS; i++) {
      bfes.search(vec, k);
    }
    long end = System.currentTimeMillis();
    long diff = end - start;
    System.out.println((double) (diff) / ITERATIONS + " ms per search");
  }

  static float[] getRandomVector(int dim) {
    float[] vec = new float[dim];
    for (int i = 0; i < dim; i++) {
      vec[i] = random.nextFloat();
    }
    return vec;
  }

  static BFES getBfes(int dim) {
    BFES bfes = new BFES(dim);
    for (int i = 0; i < 100_000; i++) {
      bfes.add(getRandomVector(dim));
    }
    return bfes;
  }
}
