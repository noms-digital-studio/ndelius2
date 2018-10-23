package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.annotations.DateOnPage;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredDateOnPage;
import data.annotations.RequiredGroupOnPage;
import data.annotations.RequiredOnPage;
import lombok.Data;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Validate
public abstract class WizardData implements Validatable<List<ValidationError>> {

    @Encrypted
    @RequiredOnPage(1)
    @JsonIgnore
    private String onBehalfOfUser;

    @Required
    @JsonIgnore
    private Integer pageNumber;

    @JsonIgnore
    private Integer jumpNumber;

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
                    field.isAnnotationPresent(RequiredGroupOnPage.class) ?
                        field.getAnnotation(RequiredGroupOnPage.class).value() :
                        field.isAnnotationPresent(RequiredDateOnPage.class) ?
                            field.getAnnotation(RequiredDateOnPage.class).value() :
                            field.getAnnotation(DateOnPage.class).value();
    }

    private List<Function<Map<String, Object>, Stream<ValidationError>>> validators() {    // Overridable in derived Data classes

        List<Function<Map<String, Object>, Stream<ValidationError>>> baseValidators = ImmutableList.of(
                this::mandatoryErrors,
                this::mandatoryDateErrors,
                this::partialRequiredDateErrors,
                this::invalidRequiredDateErrors,
                this::partialDateErrors,
                this::invalidDateErrors,
                this::mandatoryGroupErrors
        );

        List<Function<Map<String, Object>, Stream<ValidationError>>> specificValidators =
                reportSpecificValidators();

        return ImmutableList.<Function<Map<String, Object>, Stream<ValidationError>>>builder()
                .addAll(baseValidators)
                .addAll(specificValidators)
                .build();
    }

    protected abstract List<Function<Map<String, Object>, Stream<ValidationError>>> reportSpecificValidators();

    private Stream<ValidationError> mandatoryErrors(Map<String, Object> options) {

        // Default to required is enforced if no onlyIfField
        return requiredFields().
                filter(this::requiredMandatoryFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> Strings.isNullOrEmpty(getStringValue(field).orElse(null))).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredOnPage.class).message()));
    }

    private Stream<ValidationError> mandatoryGroupErrors(Map<String, Object> options) {

        return requiredGroupFields().
                filter(this::noFieldInPageGroupSelected).
                filter(field -> mustValidateField(options, field)).
                filter(field -> field.getAnnotation(RequiredGroupOnPage.class).errorWhenInvalid()).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredGroupOnPage.class).message()));
    }
    private Stream<ValidationError> mandatoryDateErrors(Map<String, Object> options) {

        return requiredDateFields().
                filter(this::requiredDateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(this::allDateFieldsAreEmpty).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredDateOnPage.class).message()));
    }

    private Stream<ValidationError> partialRequiredDateErrors(Map<String, Object> options) {

        return requiredDateFields().
                filter(this::requiredDateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(this::someDateFieldsAreEmpty).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredDateOnPage.class).incompleteMessage()));
    }

    private Stream<ValidationError> partialDateErrors(Map<String, Object> options) {

        return dateFields().
                filter(this::dateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(this::someDateFieldsAreEmpty).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(DateOnPage.class).incompleteMessage()));
    }

    private Stream<ValidationError> invalidRequiredDateErrors(Map<String, Object> options) {

        return requiredDateFields().
                filter(this::requiredDateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && composedDateBitsAreInvalid(field)).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredDateOnPage.class).invalidMessage()));

    }

    private Stream<ValidationError> invalidDateErrors(Map<String, Object> options) {

        return dateFields().
                filter(this::dateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && composedDateBitsAreInvalid(field)).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(DateOnPage.class).invalidMessage()));

    }

    private boolean requiredMandatoryFieldEnforced(Field field) {
        return requiredEnforced(field.getAnnotation(RequiredOnPage.class).onlyIfField(), field.getAnnotation(RequiredOnPage.class).onlyIfFieldMatchValue());
    }

    protected boolean requiredDateFieldEnforced(Field field) {
        return requiredEnforced(field.getAnnotation(RequiredDateOnPage.class).onlyIfField(), field.getAnnotation(RequiredDateOnPage.class).onlyIfFieldMatchValue());
    }

    protected boolean dateFieldEnforced(Field field) {
        return requiredEnforced(field.getAnnotation(DateOnPage.class).onlyIfField(), field.getAnnotation(DateOnPage.class).onlyIfFieldMatchValue());
    }

    private boolean requiredEnforced(String onlyIfName, String onlyIfFieldMatchValue) {
        val matcher = Optional.of(onlyIfFieldMatchValue)
                .filter(StringUtils::isNotBlank)
                .map(matchValue -> (Function<Field, Optional<Boolean>>) onlyIfField -> Optional.of(this.getStringValue(onlyIfField).map(value -> value.equals(matchValue)).orElse(false)))
                .orElse(this::getBooleanValue);
        return  getField(onlyIfName).flatMap(matcher).orElse(true);
    }

    private boolean shouldCheckAll(Map<String, Object> options) {
        return Boolean.parseBoolean(options.getOrDefault("checkAll", false).toString());
    }

    protected boolean mustValidateField(Map<String, Object> options, Field field) {
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

    private boolean allDateFieldsAreEmpty(Field field) {
        return dateFieldValues(field)
                .allMatch(StringUtils::isBlank);

    }
    protected boolean allDateFieldsAreSupplied(Field field) {
        return dateFieldValues(field)
                .allMatch(StringUtils::isNotBlank);

    }
    private boolean someDateFieldsAreEmpty(Field field) {
        return !allDateFieldsAreEmpty(field) && !allDateFieldsAreSupplied(field);
    }

    protected SimpleDateFormat getValidatorDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        dateFormat.setLenient(false);
        return dateFormat;
    }

    protected boolean composedDateBitsAreInvalid(Field field) {
        try {
            String formattedDate = dateFieldValues(field).collect(Collectors.joining("/"));
            SimpleDateFormat dateFormat = getValidatorDateFormat();
            dateFormat.parse(formattedDate);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    protected String formattedDateFromDateParts(String fieldName) {
        return formattedDateFromDateParts(fieldForName(fieldName));
    }

    private String formattedDateFromDateParts(Field field) {
        return composedDateBitsAreInvalid(field) ?
                "" :
                dateFieldValues(field).collect(Collectors.joining("/"));
    }

    protected Stream<String> dateFieldValues(Field field) {
        return Stream.of("day", "month", "year")
                .map(postifx -> String.format("%s_%s", field.getName(), postifx))
                .map(this::fieldForName)
                .map(this::getStringValue)
                .map(value -> value.orElse(""));
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

    protected Stream<Field> requiredDateFields() {

        return annotatedFields(RequiredDateOnPage.class);
    }

    protected Stream<Field> dateFields() {

        return annotatedFields(DateOnPage.class);
    }

    private Stream<Field> requiredGroupFields() {

        return annotatedFields(RequiredGroupOnPage.class);
    }

    private Field fieldForName(String name) {
        return FieldUtils.getField(this.getClass(), name, true);
    }

    private Stream<Field> onPageFields() {
        return Stream.of(annotatedFields(OnPage.class), requiredFields(), requiredGroupFields())
                .reduce(Stream::concat)
                .orElseGet(Stream::empty);
    }
}
