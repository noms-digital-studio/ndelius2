package services;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class SpellcheckServiceTest {
    private SpellcheckService spellcheckService;

    @Before
    public void setUp() {
        spellcheckService= new SpellcheckService();
    }

    @Test
    public void testThatMisspelledWordsReturnSuggestions() {
        String[] wordsToCheck = Arrays.asList("speeling", "misteke").toArray(new String[0]);
        String spellcheckSuggestionsString = spellcheckService.getSpellcheckSuggestionsString(wordsToCheck);

        String expectedSuggestions = "{ \"result\" : { \"words\" : { \"speeling\" : [\"sleeping\", \"peeling\", \"peelings\", \"spieling\", \"steeling\", \"spelling\", \"speeding\", \"s peeling\", \"splining\"],\"misteke\" : [\"mistake\", \"mist eke\", \"mist-eke\"] } } }";
        assertThat(spellcheckSuggestionsString).isEqualTo(expectedSuggestions);
    }

    @Test
    public void testThatWhenNoMisspelledWordsReturnsEmptySuggestions() {
        String[] wordsToCheck = Arrays.asList("spelling", "mistake").toArray(new String[0]);
        String spellcheckSuggestionsString = spellcheckService.getSpellcheckSuggestionsString(wordsToCheck);

        String expectedSuggestions = "{ \"result\" : {}}";
        assertThat(spellcheckSuggestionsString).isEqualTo(expectedSuggestions);
    }
}
