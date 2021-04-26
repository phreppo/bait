package bait.utility;

public final class Utility {

    public static String removeSuffix(final String s, final String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix))
            return s.substring(0, s.length() - suffix.length());
        return s;
    }

    private Utility() {
    }
}
