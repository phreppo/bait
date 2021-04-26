package bait.utility;

import java.nio.file.Path;

public final class ArgsParser {

    public static Args parseArgs(String[] args) {
        Args parsedArgs = new Args();
        boolean aSpecified = false;
        boolean bSpecified = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case Settings.HELP_FLAG_NAME:
                case Settings.HELP_FLAG_NAME_EXTENDED:
                    printHelp();
                    System.exit(0);
                    break;
                case Settings.DEBUG_FLAG_NAME:
                    if (!thereIsAFollowingArgument(args, i))
                        printMessageAndHelpAndFail(
                                "Must provide a debug level between 0 and " + Args.DEBUG_LEVEL_VERY_VERBOSE);
                    String nextArg = args[++i];
                    if (!strIsInt(nextArg))
                        printMessageAndHelpAndFail("The argument after " + Settings.DEBUG_FLAG_NAME
                                + " must be an integer between 0 and " + Args.DEBUG_LEVEL_VERY_VERBOSE);
                    int debugLevel = Integer.parseInt(nextArg);
                    if (!Args.debugLevelIsValid(debugLevel))
                        printMessageAndHelpAndFail("Invalid debug level: debug level must be an integer between 0 and "
                                + Args.DEBUG_LEVEL_VERY_VERBOSE);
                    parsedArgs.setDebugLevel(debugLevel);
                    break;
                case Settings.FIRST_AUTOMATON_FLAG_NAME:
                    if (!thereIsAFollowingArgument(args, i))
                        printMessageAndHelpAndFail("After " + Settings.FIRST_AUTOMATON_FLAG_NAME
                                + " you must provide the path to the first automaton");
                    aSpecified = true;
                    parsedArgs.setFirstAutomatonPath(Path.of(args[++i]));
                    if (!isAutomataFormat(parsedArgs.firstAutomatonPath().toString()))
                        printMessageAndHelpAndFail("Automata must be provided in '.ba' format");
                    break;
                case Settings.SECOND_AUTOMATON_FLAG_NAME:
                    if (!thereIsAFollowingArgument(args, i))
                        printMessageAndHelpAndFail("After " + Settings.SECOND_AUTOMATON_FLAG_NAME
                                + " you must provide the path to the second automaton");
                    bSpecified = true;
                    parsedArgs.setSecondAutomatonPath(Path.of(args[++i]));
                    if (!isAutomataFormat(parsedArgs.secondAutomatonPath().toString()))
                        printMessageAndHelpAndFail("Automata must be provided in '.ba' format");
                    break;
                default:
                    System.out.println("No such option: " + args[i]);
                    printHelp();
                    System.exit(1);
            }
        }
        if (!aSpecified || !bSpecified)
            printMessageAndHelpAndFail("Specify both automata using " + Settings.FIRST_AUTOMATON_FLAG_NAME + " and "
                    + Settings.SECOND_AUTOMATON_FLAG_NAME + " options");
        return parsedArgs;
    }

    private static void printHelp() {
        System.out.println("Usage: java -jar bait.jar " + Settings.FIRST_AUTOMATON_FLAG_NAME + " {pathToFirstAutomaton} "
                + Settings.SECOND_AUTOMATON_FLAG_NAME + " {pathToSecondAutomaton}");
        System.out.println(
                "Computes whether the language of the first automaton is containted in the language of the second.");
        System.out.println("The automata must be specified in '.ba' format.");
        System.out.println("Optional arguments:");
        System.out.println(Settings.HELP_FLAG_NAME + ", " + Settings.HELP_FLAG_NAME_EXTENDED + "\tPrints help");
        System.out.println(Settings.DEBUG_FLAG_NAME
                + "\t\tPrints debug information. Must also specify also a debug level between 0 and "
                + Args.DEBUG_LEVEL_VERY_VERBOSE + ". By default is 0, silent");
        System.out.println();
        System.out.println("Example: java -jar bait.jar -a path/to/A.ba -b path/to/B.ba");
    }

    private static void printMessageAndHelpAndFail(String message) {
        System.out.println(message);
        System.out.println();
        printHelp();
        System.exit(1);
    }

    private static boolean thereIsAFollowingArgument(String[] args, int currentIndex) {
        return currentIndex + 1 < args.length;
    }

    private static boolean strIsInt(String str) {
        return str.matches("\\d+");
    }

    private static boolean isAutomataFormat(String s) {
        return s.toLowerCase().endsWith(Settings.AUTOMATA_FORMAT_SUFFIX.toLowerCase());
    }

    private ArgsParser() {
    }

}
