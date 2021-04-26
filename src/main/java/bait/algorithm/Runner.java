package bait.algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import bait.automata.Alphabet;
import bait.automata.AutomatonParser;
import bait.automata.BuchiAutomaton;
import bait.automata.AutomatonParser.ParseError;
import bait.utility.Args;

public final class Runner {

    public boolean inclusionHolds(Args arguments) throws IOException, ParseError {
        assertNeededFilesExist(arguments);
        String firstAutomatonSource = new String(Files.readAllBytes(arguments.firstAutomatonPath()));
        String secondAutomatonSource = new String(Files.readAllBytes(arguments.secondAutomatonPath()));
        Alphabet alphabet = AutomatonParser.parseAlphabet(firstAutomatonSource, secondAutomatonSource);
        BuchiAutomaton a = AutomatonParser.parse(firstAutomatonSource, alphabet);
        BuchiAutomaton b = AutomatonParser.parse(secondAutomatonSource, alphabet);
        if (arguments.veryVerboseDebug()) {
            System.out.println(a);
            System.out.println(b);
            System.out.println("Parsing complete");
        }
        System.out.println("Running bait");
        System.out.println("Computing the language inclusion between " + arguments.firstAutomatonPath() + " and "
                + arguments.secondAutomatonPath());

        BAInc inclusionAlgorithm = new BAInc(arguments);
        boolean included = inclusionAlgorithm.run(a, b);
        printResults(inclusionAlgorithm, included, arguments);
        return included;
    }

    private static void assertNeededFilesExist(Args arguments) {
        assertFileExists(arguments.firstAutomatonPath().toFile());
        assertFileExists(arguments.secondAutomatonPath().toFile());
    }

    private static void assertFileExists(File f) {
        if (!f.exists()) {
            System.err.println("File " + f.toString() + " does not exist");
            System.exit(1);
        }
    }

    private static void printResults(BAInc inclusionAlgorithm, boolean included, Args arguments) {
        if (arguments.minimalDebug()) {
            System.out.println("Number of iterations to compute X: " + inclusionAlgorithm.getXIterations());
            System.out.println("Total number of elements in the antichains in X: " + inclusionAlgorithm.getXSize());
            System.out.println("Total number of iterations to compute Ys: " + inclusionAlgorithm.getYTotalIterations());
            System.out.println(
                    "Average number of iterations to compute Ys: " + inclusionAlgorithm.getYAverageIterations());
            System.out.println(
                    "Average number of elements in the antichains in the Ys: " + inclusionAlgorithm.getYAverageSize());
        }
        System.out.println("Inclusion holds: " + included);
        System.out.println("Time to run bait(ms): " + inclusionAlgorithm.getRuntime());
    }
}
