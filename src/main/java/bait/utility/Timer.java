package bait.utility;

/* Simple timer that automatically updates the total measured time when the
 * stop method is called. */
public final class Timer {
    private long start;
    private long end;
    private long total;

    public Timer() {
        this.start = 0L;
        this.end = 0L;
        this.total = 0L;
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        end = System.currentTimeMillis();
        total += end - start;
    }

    public long getMeasuredTime() {
        return total;
    }
}
