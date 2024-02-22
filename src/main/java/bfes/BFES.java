package bfes;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

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
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must have the same length");
        }

        VectorSpecies<Float> species = FloatVector.SPECIES_PREFERRED; // Best available species

        int i = 0;
        int limit = a.length - a.length % species.length();

        // Vectorized calculation
        float result = 0.0f;
        for (; i < limit; i += species.length()) {
            FloatVector va = FloatVector.fromArray(species, a, i);
            FloatVector vb = FloatVector.fromArray(species, b, i);
            result += va.mul(vb).reduceLanes(VectorOperators.ADD);
        }

        // Process remaining elements (if any)
        for (; i < a.length; i++) {
            result += a[i] * b[i];
        }

        return result;
    }
}
