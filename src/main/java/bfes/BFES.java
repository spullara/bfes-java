package bfes;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BFES {
  private final List<float[]> index = new ArrayList<>();
  private final int dim;

  public BFES(int dim) {
    this.dim = dim;
  }

  public void add(float[] vec) {
    assert vec.length == dim;
    float unitFactor = (float) Math.sqrt(dot(vec, vec));
    float[] clone = vec.clone();
    for (int i = 0; i < dim; i++) {
      clone[i] = clone[i] / unitFactor;
    }
    index.add(clone);
  }

  public List<Score> search(float[] query, int k) {
    assert query.length == dim;
    float unitFactor = (float) (1.0 / Math.sqrt(dot(query, query)));
    return IntStream.range(0, index.size())
            .parallel()
            .mapToObj(i -> new Score(i, dot(query, index.get(i))))
            .collect(ConcurrentSkipListMap<Score, float[]>::new,
                    (map, score) -> {
                      if (map.size() == k) {
                        Score key = map.lastEntry().getKey();
                        if (score.score() < key.score()) {
                          return;
                        }
                        map.remove(key);
                      }
                      map.put(score, query);
                    }, AbstractMap::putAll)
            .keySet()
            .stream()
            .limit(k)
            .map(s -> new Score(s.id(), s.score() * unitFactor))
            .collect(Collectors.toList());
  }

  public float dot(float[] a, float[] b) {
    float res = 0f;
    /*
     * If length of vector is larger than 8, we use unrolled dot product to accelerate the
     * calculation.
     */
    int i;
    for (i = 0; i < a.length % 8; i++) {
      res += b[i] * a[i];
    }
    if (a.length < 8) {
      return res;
    }
    for (; i + 31 < a.length; i += 32) {
      res +=
              b[i + 0] * a[i + 0]
                      + b[i + 1] * a[i + 1]
                      + b[i + 2] * a[i + 2]
                      + b[i + 3] * a[i + 3]
                      + b[i + 4] * a[i + 4]
                      + b[i + 5] * a[i + 5]
                      + b[i + 6] * a[i + 6]
                      + b[i + 7] * a[i + 7];
      res +=
              b[i + 8] * a[i + 8]
                      + b[i + 9] * a[i + 9]
                      + b[i + 10] * a[i + 10]
                      + b[i + 11] * a[i + 11]
                      + b[i + 12] * a[i + 12]
                      + b[i + 13] * a[i + 13]
                      + b[i + 14] * a[i + 14]
                      + b[i + 15] * a[i + 15];
      res +=
              b[i + 16] * a[i + 16]
                      + b[i + 17] * a[i + 17]
                      + b[i + 18] * a[i + 18]
                      + b[i + 19] * a[i + 19]
                      + b[i + 20] * a[i + 20]
                      + b[i + 21] * a[i + 21]
                      + b[i + 22] * a[i + 22]
                      + b[i + 23] * a[i + 23];
      res +=
              b[i + 24] * a[i + 24]
                      + b[i + 25] * a[i + 25]
                      + b[i + 26] * a[i + 26]
                      + b[i + 27] * a[i + 27]
                      + b[i + 28] * a[i + 28]
                      + b[i + 29] * a[i + 29]
                      + b[i + 30] * a[i + 30]
                      + b[i + 31] * a[i + 31];
    }
    for (; i + 7 < a.length; i += 8) {
      res +=
              b[i + 0] * a[i + 0]
                      + b[i + 1] * a[i + 1]
                      + b[i + 2] * a[i + 2]
                      + b[i + 3] * a[i + 3]
                      + b[i + 4] * a[i + 4]
                      + b[i + 5] * a[i + 5]
                      + b[i + 6] * a[i + 6]
                      + b[i + 7] * a[i + 7];
    }
    return res;
  }

}
