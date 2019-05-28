package services;

import com.google.inject.Inject;
import dk.dren.hunspell.Hunspell;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SpellcheckService {

    private Hunspell.Dictionary dictionary;
    private final static Logger LOGGER = LoggerFactory.getLogger(SpellcheckService.class);

    @Inject
    public SpellcheckService() {
        dictionary = loadDictionaries();
    }

    public String getSpellcheckSuggestionsString(String[] wordsToCheck) {
        StringBuilder spellingMistakes = new StringBuilder();

        Arrays.stream(wordsToCheck).forEach(word -> {
                if (dictionary.misspelled(word)) {
                    spellingMistakes.append("\"").append(word).append("\" : ");
                    spellingMistakes.append(dictionary.suggest(word).stream().map(str -> "\"" + str + "\"").collect(Collectors.toList()));
                    spellingMistakes.append(",");
                }
            }
        );

        return constructResponse(spellingMistakes);
    }

    private String constructResponse(StringBuilder spellingMistakes) {
        val spellingMistakesValue = spellingMistakes.toString();
        if(StringUtils.isNotBlank(spellingMistakesValue)) {
            val spellingMistakesNoEndComma = spellingMistakesValue.substring(0, spellingMistakesValue.lastIndexOf(","));
            return String.format("{ \"result\" : { \"words\" : { %s } } }", spellingMistakesNoEndComma);
        }
        return "{ \"result\" : {}}";
    }


    private Hunspell.Dictionary loadDictionaries() {
        try {
            return Hunspell.getInstance().getDictionary(getDictionaryPath());
        } catch (FileNotFoundException e) {
            LOGGER.error("Dictionary can not be loaded", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Unsupported Encoding Exception", e);
        }
        return null;
    }

    private String getDictionaryPath() {
        val dictionaryPath = getClass().getClassLoader().getResource("en_GB.dic").getPath();
        return dictionaryPath.substring(0, dictionaryPath.lastIndexOf(".dic"));
    }
}