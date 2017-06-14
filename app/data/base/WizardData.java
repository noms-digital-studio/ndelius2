package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import data.annotations.RequiredOnPage;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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

            try {
                val matches = spelling.check(Optional.ofNullable(field.get(this)).map(Object::toString).orElse(""));

                return String.join(" | ", matches.stream().map(match -> String.join(", ", match.getSuggestedReplacements())).collect(Collectors.toList()));
            }
            catch (IllegalAccessException | IOException ex) {
                return "";
            }

        })).entrySet().stream().filter(entry -> !Strings.isNullOrEmpty(entry.getValue())).
                map(entry -> new ValidationError(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        validationErrors.addAll(requiredFields().filter(field -> {

            field.setAccessible(true);

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

        return Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotationClass));
    }
}
