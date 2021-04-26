import org.junit.jupiter.api.Test;

import bait.algorithm.Runner;
import bait.automata.AutomatonParser.ParseError;
import bait.utility.Args;

import java.io.IOException;

/**
 * These tests verify that the language inclusion algorithm, took as a black
 * box, is correct. To add a new test, create a new function that has the same
 * name as the sample that you are considering. The functions
 * `inclusionShouldHold` and `inclusionShouldNotHold` are useful utilities: when
 * writing one test, just call one of them with the path to the automata and the
 * arguments and they will make the proper assertions, eventually printing on
 * `stderr` what went wrong.
 *
 * Optionally, if the test is obtained switching two automata A and B already
 * present in another test, add `_reversed` before the comparator description.
 * For example, if one test called `test1` checks L(A) ⊆ L(B) and you add the
 * test L(B) ⊆ L(A), the new one should be called `test1_reverse`. Since we have
 * many examples in which the inclusion holds, it is useful to add the reversed
 * version to check also when the inclusion *doesn't* hold.
 *
 * To run the tests: `./gradlew test`
 */
class LanguageInclusionTest {


    /******************************************************************************************************************/
    /* Inclusion holds */
    /******************************************************************************************************************/

    @Test
    void Specal_factors_are_unique() {
        Args args = Args.of("test-automata/Specal_factors_are_unique_sub.autfilt.ba",
                "test-automata/Specal_factors_are_unique_sup.autfilt.aligned.ba");
        inclusionShouldHold(args);
    }

    @Test
    void The_lazy_Ostrowski_representation_is_unique() {
        Args args = Args.of("test-automata/The_lazy_Ostrowski_representation_is_unique_sub.autfilt.ba",
                "test-automata/The_lazy_Ostrowski_representation_is_unique_sup.autfilt.ba");
        inclusionShouldHold(args);
    }

    @Test
    void Sturmian_words_start_with_arbitarily_long_palindrome() {
        Args args = Args.of(
                "test-automata/Sturmian_words_start_with_arbitarily_long_palindromes_sub.autfilt.ba",
                "test-automata/Sturmian_words_start_with_arbitarily_long_palindromes_sup.autfilt.ba");
        inclusionShouldHold(args);
    }

    @Test
    void Sucessors_are_unique() {
        Args args = Args.of("test-automata/Sucessors_are_unique_sub.autfilt.ba",
                "test-automata/Sucessors_are_unique_sup.autfilt.aligned.ba");
        inclusionShouldHold(args);
    }

    @Test
    void All_factors_of_Sturmian_words_are_recurrent() {
        Args args = Args.of("test-automata/All_factors_of_Sturmian_words_are_recurrent_sub.autfilt.ba",
                "test-automata/All_factors_of_Sturmian_words_are_recurrent_sup.autfilt.ba");
        inclusionShouldHold(args);
    }

    @Test
    void Addition_of_Ostrowski_a_representations_is_a_function() {
        Args args = Args.of(
                "test-automata/Addition_of_Ostrowski-a_representations_is_a_function_(ie,_there_is_an_output_for_every_input)_sub.autfilt.ba",
                "test-automata/Addition_of_Ostrowski-a_representations_is_a_function_(ie,_there_is_an_output_for_every_input)_sup.autfilt.ba");
        inclusionShouldHold(args);
    }

    @Test
    void All_natural_numbers_other_than_0_have_a_predecessor() {
        Args args = Args.of(
                "test-automata/All_natural_numbers_other_than_0_have_a_predecessor_sub.autfilt.ba",
                "test-automata/All_natural_numbers_other_than_0_have_a_predecessor_sup.autfilt.aligned.ba");
        inclusionShouldHold(args);
    }

    @Test
    void All_Sturmian_words_start_with_arbitrarily_long_squares() {
        Args args = Args.of(
                "test-automata/All_Sturmian_words_start_with_arbitrarily_long_squares_sub.autfilt.ba",
                "test-automata/All_Sturmian_words_start_with_arbitrarily_long_squares_sup.autfilt.ba");
        inclusionShouldHold(args);
    }

    @Test
    void All_Sturmian_words_contain_cubes() {
        Args args = Args.of("test-automata/All_Sturmian_words_contain_cubes_sub.autfilt.ba",
                "test-automata/All_Sturmian_words_contain_cubes_sup.autfilt.ba");
        inclusionShouldHold(args);
    }

    @Test
    void concur11Fig1() {
        Args args = Args.of("test-automata/concur11A.ba", "test-automata/concur11B.ba");
        inclusionShouldHold(args);
    }

    @Test
    void peterson() {
        Args args = Args.of("test-automata/petersonA.ba", "test-automata/petersonB.ba");
        inclusionShouldHold(args);
    }

    @Test
    void emptyIntoSomething() {
        Args args = Args.of("test-automata/empty-into-somethingA.ba",
                "test-automata/empty-into-somethingB.ba");
        inclusionShouldHold(args);
    }

    @Test
    void emptyIntoEmpty() {
        Args args = Args.of("test-automata/empty-into-emptyA.ba", "test-automata/empty-into-emptyB.ba");
        inclusionShouldHold(args);
    }

    @Test
    void deadStates() {
        Args args = Args.of("test-automata/dead-statesA.ba", "test-automata/dead-statesB.ba");
        inclusionShouldHold(args);
    }

    @Test
    void identity1() {
        Args args = Args.of("test-automata/identityA.ba", "test-automata/identityB.ba");
        inclusionShouldHold(args);
    }

    @Test
    void identity2() {
        Args args = Args.of("test-automata/identity2A.ba", "test-automata/identity2B.ba");
        inclusionShouldHold(args);
    }

    /******************************************************************************************************************/
    /* Inclusion doesn't hold */
    /******************************************************************************************************************/

    @Test
    void philsv3() {
        Args args = Args.of("test-automata/philsV3A.ba", "test-automata/philsV3B.ba");
        inclusionShouldNotHold(args);
    }

    @Test
    void philsv2() {
        Args args = Args.of("test-automata/philsV2A.ba", "test-automata/philsV2B.ba");
        inclusionShouldNotHold(args);
    }

    @Test
    void thesis() {
        Args args = Args.of("test-automata/thesisA.ba", "test-automata/thesisB.ba");
        inclusionShouldNotHold(args);
    }

    @Test
    void somethingIntoEmpty() {
        Args args = Args.of("test-automata/something-into-emptyA.ba",
                "test-automata/something-into-emptyB.ba");
        inclusionShouldNotHold(args);
    }

    @Test
    void Specal_factors_are_unique_reversed() {
        Args args = Args.of("test-automata/Specal_factors_are_unique_sup.autfilt.aligned.ba",
                "test-automata/Specal_factors_are_unique_sub.autfilt.ba");
        inclusionShouldNotHold(args);
    }

    @Test
    void The_lazy_Ostrowski_representation_is_unique_reversed() {
        Args args = Args.of("test-automata/The_lazy_Ostrowski_representation_is_unique_sup.autfilt.ba",
                "test-automata/The_lazy_Ostrowski_representation_is_unique_sub.autfilt.ba");
        inclusionShouldNotHold(args);
    }

    @Test
    void Sturmian_words_start_with_arbitarily_long_palindrome_reversed() {
        Args args = Args.of(
                "test-automata/Sturmian_words_start_with_arbitarily_long_palindromes_sup.autfilt.ba",
                "test-automata/Sturmian_words_start_with_arbitarily_long_palindromes_sub.autfilt.ba");
        inclusionShouldNotHold(args);
    }

    /******************************************************************************************************************/
    /* Utility to run the tests */
    /******************************************************************************************************************/

    static void inclusionShouldHold(Args arguments) {
        try {
            boolean included = inclusionHolds(arguments);
            if (!included) {
                System.err.println("Inclusion between " + arguments.firstAutomatonPath() + " and "
                        + arguments.secondAutomatonPath() + " should hold, but the algorithm returned false");
                assert (false);
            }
        } catch (Exception e) {
            System.err.println(e);
            assert (false);
        }
    }

    static void inclusionShouldNotHold(Args arguments) {
        try {
            boolean included = inclusionHolds(arguments);
            if (included) {
                System.err.println("Inclusion between " + arguments.firstAutomatonPath() + " and "
                        + arguments.secondAutomatonPath() + " should NOT hold, but the algorithm returned true");
                assert (false);
            }
        } catch (Exception e) {
            System.err.println(e);
            assert (false);
        }
    }

    static boolean inclusionHolds(Args arguments) throws IOException, ParseError {
        Runner algorithmRunner = new Runner();
        return algorithmRunner.inclusionHolds(arguments);
    }

}
