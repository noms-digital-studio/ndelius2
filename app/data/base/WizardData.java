package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import data.annotations.RequiredOnPage;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import data.annotations.SpellCheck;
import lombok.Data;
import lombok.val;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import play.data.validation.Constraints.*;
import play.data.validation.ValidationError;

@Data
public class WizardData {

    @Required
    @JsonIgnore
    private Integer pageNumber;

    public List<ValidationError> validate() {   // validate() is called by Play Form submission bindFromRequest()

        val spelling = new JLanguageTool(new BritishEnglish());

        val validationErrors = spellCheckFields().collect(Collectors.toMap(Field::getName, field -> {

            field.setAccessible(true);

            val overrideName = field.getAnnotation(SpellCheck.class).overrideField();

            val overrideEnabled = !Strings.isNullOrEmpty(overrideName) &&
                    allFields().filter(overrideField -> overrideField.getName().equals(overrideName)).findAny().flatMap(overrideField -> {

                overrideField.setAccessible(true);

                try {
                    return Optional.ofNullable(overrideField.get(this)).map(value -> Boolean.parseBoolean(value.toString()));
                }
                catch (IllegalAccessException ex) {
                    return Optional.empty();
                }
            }).orElse(false);

            if (overrideEnabled) {
                return new ArrayList<String>();
            }

            try {
                val value = Optional.ofNullable(field.get(this)).map(Object::toString).orElse("");
                val matches = spelling.check(value);

                return matches.stream().map(match -> "'" + value.substring(match.getFromPos(), match.getToPos()) + "' could be " +
                        String.join(" or ", match.getSuggestedReplacements().stream().
                                map(r -> String.format("'%s'", r)).collect(Collectors.toList()))).collect(Collectors.toList());
            }
            catch (IllegalAccessException | IOException ex) {
                return new ArrayList<String>();
            }

        })).entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).
                flatMap(entry -> entry.getValue().stream().map(s -> new ValidationError(entry.getKey(), s))).collect(Collectors.toList());

        validationErrors.addAll(requiredFields().filter(field -> {

            field.setAccessible(true);

            val onlyIfName = field.getAnnotation(RequiredOnPage.class).onlyIfField();

            val requiredEnforced = !Strings.isNullOrEmpty(onlyIfName) &&
                    allFields().filter(onlyIfField -> onlyIfField.getName().equals(onlyIfName)).findAny().flatMap(onlyIfField -> {

                        onlyIfField.setAccessible(true);

                        try {
                            return Optional.ofNullable(onlyIfField.get(this)).map(value -> Boolean.parseBoolean(value.toString()));
                        }
                        catch (IllegalAccessException ex) {
                            return Optional.empty();
                        }
                    }).orElse(true); // Default to required is enforced if no onlyIfField exists, otherwise use onlyIfField value

            if (!requiredEnforced) {
                return false;
            }

            try {
                return field.getAnnotation(RequiredOnPage.class).value() <= pageNumber &&
                        Strings.isNullOrEmpty(Optional.ofNullable(field.get(this)).map(Object::toString).orElse(null));
            }
            catch (IllegalAccessException ex) {
                return true;
            }

        }).map(field -> new ValidationError(field.getName(), RequiredValidator.message)).collect(Collectors.toList()));

        return validationErrors;
    }

    public Integer totalPages() {

        return requiredFields().mapToInt(field -> field.getAnnotation(RequiredOnPage.class).value()).max().orElse(0);
    }

    private Stream<Field> requiredFields() {

        return annotatedFields(RequiredOnPage.class);
    }

    private Stream<Field> spellCheckFields() {

        return annotatedFields(SpellCheck.class);
    }

    private Stream<Field> annotatedFields(Class<? extends Annotation> annotationClass) {

        return allFields().filter(field -> field.isAnnotationPresent(annotationClass));
    }

    private Stream<Field> allFields() {

        return Arrays.stream(this.getClass().getDeclaredFields());
    }
}
