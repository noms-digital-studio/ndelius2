package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import data.annotations.OnPage;
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

    @JsonIgnore
    private Integer jumpNumber;

    public List<ValidationError> validate() {   // validate() is called by Play Form submission bindFromRequest()

        val spelling = new JLanguageTool(new BritishEnglish());

        val validationErrors = spellCheckFields().collect(Collectors.toMap(Field::getName, field -> {

            field.setAccessible(true);

            val overrideName = field.getAnnotation(SpellCheck.class).overrideField();

            val overrideEnabled = !Strings.isNullOrEmpty(overrideName) && getField(overrideName).flatMap(overrideField -> {

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

            val requiredEnforced = getField(onlyIfName).flatMap(onlyIfField -> {

                        onlyIfField.setAccessible(true); // Don't perform required check if a control boolean exists and is false

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
                    // Check all pages if on the last page and clicking next. Check current page only if clicking next or
            try {   // jumping forwards. If jumping back don't perform any validation

                return (pageNumber == totalPages().intValue() && !Optional.ofNullable(jumpNumber).isPresent() ||
                        (Optional.ofNullable(jumpNumber).orElse(pageNumber) >= pageNumber) &&
                                pageNumber == field.getAnnotation(RequiredOnPage.class).value()) &&
                        Strings.isNullOrEmpty(Optional.ofNullable(field.get(this)).map(Object::toString).orElse(null));
            }
            catch (IllegalAccessException ex) {
                return true;
            }

        }).map(field -> new ValidationError(field.getName(), RequiredValidator.message)).collect(Collectors.toList()));

        return validationErrors;
    }

    public Integer totalPages() {

        return onPageFields().mapToInt(WizardData::fieldPage).max().orElse(0);
    }

    public static Integer fieldPage(Field field) {

        return field.isAnnotationPresent(OnPage.class) ?
                field.getAnnotation(OnPage.class).value() :
                field.getAnnotation(RequiredOnPage.class).value();
    }

    public Optional<Field> getField(String name) {

        return allFields().filter(field -> field.getName().equals(name)).findAny();
    }

    private Stream<Field> requiredFields() {

        return annotatedFields(RequiredOnPage.class);
    }

    private Stream<Field> onPageFields() {

        return Stream.concat(annotatedFields(OnPage.class), requiredFields());
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
