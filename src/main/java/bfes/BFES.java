package bfes;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

record Score(int id, float score) implements Comparable<Score> {
  @Override
  public int compareTo(Score o) {
    // Higher is better
    return Float.compare(o.score, score);
  }
}

public class BFES {
  private List<float[]> index = new ArrayList<>();
  private int dim;

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

  public List<Score> search(float[] vec, int k) {
    assert vec.length == dim;
    float unitFactor = (float) (1.0 / Math.sqrt(dot(vec, vec)));
    return IntStream.range(0, index.size())
            .parallel()
            .mapToObj(i -> new Score(i, dot(vec, index.get(i)) * unitFactor))
            .collect(ConcurrentSkipListMap<Score, float[]>::new,
                    (map, score) -> {
                      if (map.size() == k) {
                        Score key = map.lastEntry().getKey();
                        if (score.score() < key.score()) {
                          return;
                        }
                        map.remove(key);
                      }
                      map.put(score, vec);
                    }, AbstractMap::putAll)
            .keySet()
            .stream()
            .limit(k)
            .collect(Collectors.toList());
  }

  private static float dot(float[] a, float[] b) {
    float sum = 0;
    for (int i = 0; i < a.length; i++) {
      sum += a[i] * b[i];
    }
    return sum;
  }
}
