package bait;

import java.io.IOException;

import bait.algorithm.Runner;
import bait.automata.AutomatonParser;
import bait.utility.Args;
import bait.utility.ArgsParser;

public class Main {

    public static void main(String[] args) {
        try {
            Args arguments = ArgsParser.parseArgs(args);
            Runner algorithmRunner = new Runner();
            algorithmRunner.inclusionHolds(arguments);
        } catch (AutomatonParser.ParseError | IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

}
