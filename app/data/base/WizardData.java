package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredOnPage;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.languagetool.rules.RuleMatch;
import play.data.validation.Constraints.*;
import play.data.validation.ValidationError;

@Data
@Validate
public class WizardData implements Validatable<List<ValidationError>> {

    @Required
    @JsonIgnore
    private Integer pageNumber;

    @JsonIgnore
    private Integer jumpNumber;

    @JsonIgnore
    private String feedback;

    @JsonIgnore
    private String visitedPages;

    @Override
    public List<ValidationError> validate() {   // validate() is called by Play Form submission bindFromRequest()

        return validateWithOptions(ImmutableMap.of());
    }

    public List<ValidationError> validateAll() {

        return validateWithOptions(ImmutableMap.of("checkAll", true));
    }

    private List<ValidationError> validateWithOptions(Map<String, Object> options) {

        return validators().stream().flatMap(validator -> validator.apply(options)).collect(Collectors.toList());
    }

    public Integer totalPages() {

        return onPageFields().mapToInt(WizardData::fieldPage).max().orElse(0);
    }

    public Optional<Field> getField(String name) {

        return allFields().filter(field -> field.getName().equals(name)).findAny();
    }

    public Stream<Field> encryptedFields() {

        return annotatedFields(Encrypted.class);
    }

    public static Integer fieldPage(Field field) {

        return field.isAnnotationPresent(OnPage.class) ?
                field.getAnnotation(OnPage.class).value() :
                field.getAnnotation(RequiredOnPage.class).value();
    }

    protected List<Function<Map<String, Object>, Stream<ValidationError>>> validators() {    // Overridable in derived Data classes

        return ImmutableList.of(
                this::mandatoryErrors
        );
    }

    private Stream<ValidationError> mandatoryErrors(Map<String, Object> options) {

        val checkAll = Boolean.parseBoolean(options.getOrDefault("checkAll", false).toString());
                                                        // Default to required is enforced if no onlyIfField
        return requiredFields().filter(field -> {       // exists, otherwise use onlyIfField current boolean

            val onlyIfName = field.getAnnotation(RequiredOnPage.class).onlyIfField();
            val requiredEnforced = getField(onlyIfName).flatMap(this::getBooleanValue).orElse(true);
            val fieldOnThisPage = pageNumber.equals(fieldPage(field));
            val finishedWizard = pageNumber.equals(totalPages());

            return requiredEnforced &&                                                      // Check all pages if on last page and clicking next
                    (checkAll || ((fieldOnThisPage || finishedWizard) && !isJumping())) &&  // Check current page if clicking next and not jumping
                    Strings.isNullOrEmpty(getStringValue(field).orElse(null));        // If jumping don't perform any validation

        }).map(field -> new ValidationError(field.getName(), RequiredValidator.message));
    }

    private static String suggestions(RuleMatch mistake) {

        return String.join(" or ", mistake.getSuggestedReplacements().stream().
                map(replacement -> String.format("'%s'", replacement)).collect(Collectors.toList()));
    }

    private boolean isJumping() {

        return Optional.ofNullable(jumpNumber).isPresent();
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

    private Stream<Field> allFields() {

        return FieldUtils.getAllFieldsList(this.getClass()).stream();
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
}
