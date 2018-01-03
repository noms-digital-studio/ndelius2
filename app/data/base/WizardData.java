package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredGroupOnPage;
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
                field.isAnnotationPresent(RequiredOnPage.class) ?
                    field.getAnnotation(RequiredOnPage.class).value() :
                    field.getAnnotation(RequiredGroupOnPage.class).value();
    }

    protected List<Function<Map<String, Object>, Stream<ValidationError>>> validators() {    // Overridable in derived Data classes

        return ImmutableList.of(
                this::mandatoryErrors,
                this::mandatoryGroupErrors
        );
    }

    private Stream<ValidationError> mandatoryErrors(Map<String, Object> options) {

        // Default to required is enforced if no onlyIfField
        return requiredFields().filter(field -> {       // exists, otherwise use onlyIfField current boolean

            val onlyIfName = field.getAnnotation(RequiredOnPage.class).onlyIfField();
            val requiredEnforced = getField(onlyIfName).flatMap(this::getBooleanValue).orElse(true);

            return requiredEnforced &&
                    (mustValidateField(options, field)) &&
                    Strings.isNullOrEmpty(getStringValue(field).orElse(null));

        }).map(field -> new ValidationError(field.getName(), RequiredValidator.message));
    }
    private Stream<ValidationError> mandatoryGroupErrors(Map<String, Object> options) {

        return requiredGroupFields().
                filter(field -> mustValidateField(options, field) && noFieldInPageGroupSelected(field)).
                map(field -> new ValidationError(field.getName(), RequiredValidator.message));
    }

    private boolean shouldCheckAll(Map<String, Object> options) {
        return Boolean.parseBoolean(options.getOrDefault("checkAll", false).toString());
    }

    private boolean mustValidateField(Map<String, Object> options, Field field) {
        // Check all pages if on last page and clicking next
        // Check current page if clicking next and not jumping
        // If jumping don't perform any validation
        return shouldCheckAll(options) || ((isFieldOnThisPage(field) || hasFinishedWizard()) && !isJumping());
    }

    private boolean isFieldOnThisPage(Field field) {
        return pageNumber.equals(fieldPage(field));
    }

    private boolean hasFinishedWizard() {
        return pageNumber.equals(totalPages());
    }

    private boolean noFieldInPageGroupSelected(Field field) {
        String group = field.getAnnotation(RequiredGroupOnPage.class).group();
        int pageNumber = field.getAnnotation(RequiredGroupOnPage.class).value();
        return requiredGroupFields().
                filter(anotherField -> isInSameGroup(anotherField, pageNumber, group)).
                noneMatch(fieldInGroup -> getBooleanValue(fieldInGroup).orElse(Boolean.FALSE));
    }

    private boolean isInSameGroup(Field field, int pageNumber, String group) {
        return field.getAnnotation(RequiredGroupOnPage.class).value() == pageNumber &&
                field.getAnnotation(RequiredGroupOnPage.class).group().equals(group);
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

    private Stream<Field> requiredGroupFields() {

        return annotatedFields(RequiredGroupOnPage.class);
    }

    private Stream<Field> onPageFields() {
        return Stream.of(annotatedFields(OnPage.class), requiredFields(), requiredGroupFields())
                .reduce(Stream::concat)
                .orElseGet(Stream::empty);
    }
}
