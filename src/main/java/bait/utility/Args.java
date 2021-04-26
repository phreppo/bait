package bait.utility;

import java.nio.file.Path;

public final class Args {

    public static final int DEBUG_LEVEL_MINIMAL = 1;
    public static final int DEBUG_LEVEL_VERBOSE = 2;
    public static final int DEBUG_LEVEL_VERY_VERBOSE = 3;

    private Path firstAutomatonPath;
    private Path secondAutomatonPath;
    private int debugLevel = 0; // values allowed: 0 to 3

    public static Args of(String firstAutomatonPath, String secondAutomatonPath) {
        Args a = new Args();
        a.firstAutomatonPath = Path.of(firstAutomatonPath);
        a.secondAutomatonPath = Path.of(secondAutomatonPath);
        return a;
    }

    public Path firstAutomatonPath() {
        return firstAutomatonPath;
    }

    public void setFirstAutomatonPath(Path firstAutomatonPath) {
        this.firstAutomatonPath = firstAutomatonPath;
    }

    public Path secondAutomatonPath() {
        return secondAutomatonPath;
    }

    public void setSecondAutomatonPath(Path secondAutomatonPath) {
        this.secondAutomatonPath = secondAutomatonPath;
    }

    public int debugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    public static boolean debugLevelIsValid(int dl) {
        return 0 <= dl && dl <= DEBUG_LEVEL_VERY_VERBOSE;
    }

    public boolean minimalDebug() {
        return debugLevel >= DEBUG_LEVEL_MINIMAL;
    }

    public boolean verboseDebug() {
        return debugLevel >= DEBUG_LEVEL_VERBOSE;
    }

    public boolean veryVerboseDebug() {
        return debugLevel >= DEBUG_LEVEL_VERY_VERBOSE;
    }

}
