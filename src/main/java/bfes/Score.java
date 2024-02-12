package bfes;

public record Score(int id, float score) implements Comparable<Score> {
    @Override
    public int compareTo(Score o) {
        // Higher is better
        return Float.compare(o.score, score);
    }
}
