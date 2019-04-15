package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.annotations.*;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Validate
public abstract class WizardData implements Validatable<List<ValidationError>> {

    private static final String VALID_DATE_FORMAT = "d/M/yyyy";

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

    protected List<Function<Map<String, Object>, Stream<ValidationError>>> validators() {    // Overridable in derived Data classes

        return ImmutableList.of(
                this::mandatoryErrors,
                this::mandatoryDateErrors,
                this::partialRequiredDateErrors,
                this::invalidRequiredDateErrors,
                this::notWithinRangeRequiredDateErrors,
                this::beforeEarliestRequiredDateErrors,
                this::partialDateErrors,
                this::invalidDateErrors,
                this::notWithinRangeDateErrors,
                this::beforeEarliestDateErrors,
                this::mandatoryGroupErrors
        );
    }

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

    private boolean requiredDateFieldEnforced(Field field) {
        return requiredEnforced(field.getAnnotation(RequiredDateOnPage.class).onlyIfField(), field.getAnnotation(RequiredDateOnPage.class).onlyIfFieldMatchValue());
    }

    private boolean dateFieldEnforced(Field field) {
        return requiredEnforced(field.getAnnotation(DateOnPage.class).onlyIfField(), field.getAnnotation(DateOnPage.class).onlyIfFieldMatchValue());
    }

    private Stream<ValidationError> notWithinRangeRequiredDateErrors(Map<String, Object> options) {

        return requiredDateFields().
                filter(this::requiredDateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && !composedDateBitsAreInvalid(field) && suppliedDateNotWithinRange(field, field.getAnnotation(RequiredDateOnPage.class).minDate(), field.getAnnotation(RequiredDateOnPage.class).maxDate())).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredDateOnPage.class).outOfRangeMessage()));

    }

    private Stream<ValidationError> notWithinRangeDateErrors(Map<String, Object> options) {

        return dateFields().
                filter(this::dateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && !composedDateBitsAreInvalid(field) && suppliedDateNotWithinRange(field, field.getAnnotation(DateOnPage.class).minDate(), field.getAnnotation(DateOnPage.class).maxDate())).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(DateOnPage.class).outOfRangeMessage()));

    }

    private Stream<ValidationError> beforeEarliestRequiredDateErrors(Map<String, Object> options) {

        return requiredDateFields().
                filter(this::requiredDateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && !composedDateBitsAreInvalid(field) && !suppliedDateNotWithinRange(field, field.getAnnotation(RequiredDateOnPage.class).minDate(), field.getAnnotation(RequiredDateOnPage.class).maxDate()) && suppliedDateBeforeEarliestDate(field, field.getAnnotation(RequiredDateOnPage.class).earliestDateField())).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredDateOnPage.class).beforeEarliestDateMessage()));

    }

    private Stream<ValidationError> beforeEarliestDateErrors(Map<String, Object> options) {

        return dateFields().
                filter(this::dateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && !composedDateBitsAreInvalid(field) && !suppliedDateNotWithinRange(field, field.getAnnotation(DateOnPage.class).minDate(), field.getAnnotation(DateOnPage.class).maxDate()) && suppliedDateBeforeEarliestDate(field, field.getAnnotation(DateOnPage.class).earliestDateField())).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(DateOnPage.class).beforeEarliestDateMessage()));

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
    private boolean allDateFieldsAreSupplied(Field field) {
        return dateFieldValues(field)
                .allMatch(StringUtils::isNotBlank);

    }
    private boolean someDateFieldsAreEmpty(Field field) {
        return !allDateFieldsAreEmpty(field) && !allDateFieldsAreSupplied(field);
    }

    private SimpleDateFormat getValidatorDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(VALID_DATE_FORMAT);
        dateFormat.setLenient(false);
        return dateFormat;
    }

    private boolean composedDateBitsAreInvalid(Field field) {
        String dateString = dateStringFromFieldValuesOf(field);

        if (dateString.substring(dateString.lastIndexOf("/") + 1).length() < 4) {
            return true;
        }

        try {
            getValidatorDateFormat().parse(dateString);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    private String dateStringFromFieldValuesOf(Field field) {
        return dateFieldValues(field).collect(Collectors.joining("/"));
    }

    /**
     * Assumes field must contain a parsable date
     * @param field
     * @return LocalDate
     */
    private LocalDate dateFromFieldValuesOf(Field field) {
        try {
            return getValidatorDateFormat()
                    .parse(dateFieldValues(field).collect(Collectors.joining("/")))
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (ParseException e) {
            throw new RuntimeException("Should be a valid date", e);
        }
    }

    private boolean suppliedDateNotWithinRange(Field field, String minDate, String maxDate) {

        if (minDate.isEmpty() && maxDate.isEmpty()) {
            return false;
        }

        LocalDate parsedDate = dateFromFieldValuesOf(field);

        if (!minDate.isEmpty() && parsedDate.isBefore(getRequiredDate(minDate))) {
            return true;
        }

        return !maxDate.isEmpty() && parsedDate.isAfter(getRequiredDate(maxDate));
    }

    private boolean suppliedDateBeforeEarliestDate(Field field, String earliestDateField) {

        Field earliestField = this.getField(earliestDateField).orElse(null);

        if (earliestField == null) {
            return false;
        }

        String earliestDateValue = this.getStringValue(earliestField).orElse("");

        if (earliestDateValue.isEmpty()) {
            return false;
        }

        LocalDate fieldDate = dateFromFieldValuesOf(field);
        LocalDate earliestDate = LocalDate.parse(earliestDateValue, DateTimeFormatter.ofPattern(VALID_DATE_FORMAT));

        return fieldDate.isBefore(earliestDate);
    }

    private LocalDate getRequiredDate(String sequence) {
        String[] parts = sequence.split(" ");

        if (parts.length == 2) {
            int value = Integer.parseInt(parts[0]);
            switch (parts[1].toUpperCase()) {
                case "DAY":
                case "DAYS":
                    return LocalDate.now().plusDays(value);
                case "YEAR":
                case "YEARS":
                    return LocalDate.now().plusYears(value);
            }
        }

        return LocalDate.now();
    }

    protected String formattedDateFromDatePartsDefaultToday(String fieldName) {
        String storedDate = formattedDateFromDateParts(fieldForName(fieldName));
        return Strings.isNullOrEmpty(storedDate) ? new SimpleDateFormat("dd/MM/yyyy").format(new Date()) : storedDate;
    }

    protected String formattedDateFromDateParts(String fieldName) {
        return formattedDateFromDateParts(fieldForName(fieldName));
    }

    private String formattedDateFromDateParts(Field field) {
        return composedDateBitsAreInvalid(field) ? "" : dateStringFromFieldValuesOf(field);
    }

    private Stream<String> dateFieldValues(Field field) {
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

    private Stream<Field> requiredDateFields() {

        return annotatedFields(RequiredDateOnPage.class);
    }

    private Stream<Field> dateFields() {

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
