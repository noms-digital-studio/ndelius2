package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
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
import lombok.*;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import play.data.validation.Constraints.*;
import play.data.validation.ValidationError;

@Data
public class WizardData {

    @Required
    @JsonIgnore
    private Integer pageNumber;

    @JsonIgnore
    private Integer jumpNumber;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient final JLanguageTool spellChecker = new JLanguageTool(new BritishEnglish());

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient final List<Supplier<List<ValidationError>>> validators;

    protected WizardData()
    {
        val wizardData = this;

        validators = new ArrayList<Supplier<List<ValidationError>>>() {
            {
                add(wizardData::spellingErrors);
                add(wizardData::mandatoryErrors);
            }
        };
    }

    public List<ValidationError> validate() {   // validate() is called by Play Form submission bindFromRequest()

        return validators.stream().map(Supplier::get).flatMap(List::stream).collect(Collectors.toList());
    }

    public Integer totalPages() {

        return onPageFields().mapToInt(WizardData::fieldPage).max().orElse(0);
    }

    public Optional<Field> getField(String name) {

        return allFields().filter(field -> field.getName().equals(name)).findAny();
    }

    public static Integer fieldPage(Field field) {

        return field.isAnnotationPresent(OnPage.class) ?
                field.getAnnotation(OnPage.class).value() :
                field.getAnnotation(RequiredOnPage.class).value();
    }

    private List<ValidationError> spellingErrors() {

        final Optional<String> optionalString = Optional.empty();

        return spellCheckFields().collect(Collectors.toMap(Field::getName, field -> {

            val overrideName = field.getAnnotation(SpellCheck.class).overrideField();
            val overrideEnabled = getField(overrideName).flatMap(this::getBooleanValue).orElse(false);
            val textToCheck = (overrideEnabled ? optionalString : getStringValue(field)).orElse(null);

            return Strings.isNullOrEmpty(textToCheck) ? new ArrayList<String>() : checkSpelling(textToCheck).stream().
                    map(mistake -> String.format(
                            "'%s' could be %s",
                            textToCheck.substring(mistake.getFromPos(), mistake.getToPos()),
                            suggestions(mistake)
                    )).
                    collect(Collectors.toList());

        })).entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).
                flatMap(entry -> entry.getValue().stream().map(message -> new ValidationError(entry.getKey(), message))).
                collect(Collectors.toList());
    }

    private List<ValidationError> mandatoryErrors() {
                                                        // Default to required is enforced if no onlyIfField
        return requiredFields().filter(field -> {       // exists, otherwise use onlyIfField current boolean

            val onlyIfName = field.getAnnotation(RequiredOnPage.class).onlyIfField();
            val requiredEnforced = getField(onlyIfName).flatMap(this::getBooleanValue).orElse(true);
            val fieldOnThisPage = pageNumber.equals(fieldPage(field));
            val finishedWizard = pageNumber.equals(totalPages()) && !Optional.ofNullable(jumpNumber).isPresent();
            val notBackwards = Optional.ofNullable(jumpNumber).orElse(pageNumber) >= pageNumber;

            return requiredEnforced &&                                               // Check all pages if on last page and clicking next
                    (finishedWizard || (fieldOnThisPage && notBackwards)) &&         // Check current page if clicking next or jumping forwards
                    Strings.isNullOrEmpty(getStringValue(field).orElse(null)); // If jumping back don't perform any validation

        }).map(field -> new ValidationError(field.getName(), RequiredValidator.message)).collect(Collectors.toList());
    }

    private static String suggestions(RuleMatch mistake) {

        return String.join(" or ", mistake.getSuggestedReplacements().stream().
                map(replacement -> String.format("'%s'", replacement)).collect(Collectors.toList()));
    }

    private Optional<String> getStringValue(Field field) {

        field.setAccessible(true);

        try {
            return Optional.ofNullable(field.get(this)).map(Object::toString);
        }
        catch (IllegalAccessException ex) {
            return Optional.empty();
        }
    }

    private Optional<Boolean> getBooleanValue(Field field) {

        return getStringValue(field).map(Boolean::parseBoolean);
    }

    private List<RuleMatch> checkSpelling(String text) {

        try {
            return spellChecker.check(text);
        }
        catch (IOException ex) {
            return new ArrayList<>();
        }
    }

    private Stream<Field> allFields() {

        return Arrays.stream(this.getClass().getDeclaredFields());
    }

    private Stream<Field> annotatedFields(Class<? extends Annotation> annotationClass) {

        return allFields().filter(field -> field.isAnnotationPresent(annotationClass));
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
}
