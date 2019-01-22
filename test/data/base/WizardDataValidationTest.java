package data.base;

import data.annotations.DateOnPage;
import data.annotations.OnPage;
import data.annotations.RequiredDateOnPage;
import data.annotations.RequiredGroupOnPage;
import data.annotations.RequiredOnPage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import play.data.validation.ValidationError;

import java.time.LocalDate;

import static data.base.WizardData.fieldPage;
import static org.assertj.core.api.Assertions.assertThat;

public class WizardDataValidationTest {
    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class MyTestData extends WizardData {
        @OnPage(1)
        private String optionalField;

        @RequiredOnPage(2)
        private String requiredMessageLessField;

        @RequiredOnPage(value = 3, message = "my validation message")
        private String requiredWithMessageField;

        @OnPage(4)
        private String ifOnlyBooleanField;
        @RequiredOnPage(value = 4, onlyIfField = "ifOnlyBooleanField")
        private String requiredIfOnlyBooleanField;

        @OnPage(5)
        private String ifOnlyStringField;
        @RequiredOnPage(value = 5, onlyIfField = "ifOnlyStringField", onlyIfFieldMatchValue = "matched")
        private String requiredIfOnlyStringField;

        @RequiredGroupOnPage(value = 6, message = "my group validation message")
        private boolean groupedField1;

        @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
        private boolean groupedField2;

        @RequiredGroupOnPage(value = 7)
        private boolean otherGroupedField1;

        @RequiredGroupOnPage(value = 7)
        private boolean otherGroupedField2;

        public String getDob() {
            return formattedDateFromDateParts("dob");
        }

        @RequiredDateOnPage(value = 8, message = "my mandatory date message", incompleteMessage = "my incomplete date message", invalidMessage = "my invalid date message")
        private String dob;
        private String dob_day;
        private String dob_month;
        private String dob_year;

        @DateOnPage(value = 9, incompleteMessage = "my incomplete date 2 message", invalidMessage = "my invalid date 2 message")
        private String dob2;
        private String dob2_day;
        private String dob2_month;
        private String dob2_year;

        @OnPage(10)
        private String page10Field;
        @RequiredOnPage(11)
        private String page11Field;
        @RequiredGroupOnPage(12)
        private String page12Field;
        @RequiredDateOnPage(13)
        private String page13Field;

        @RequiredDateOnPage(value = 14, onlyIfField = "ifOnlyDobField", onlyIfFieldMatchValue = "matched")
        private String conditionalDob;
        private String conditionalDob_day;
        private String conditionalDob_month;
        private String conditionalDob_year;
        @OnPage(14)
        private String ifOnlyDobField;

        @RequiredDateOnPage(value = 15, onlyIfField = "ifOnlyDob3Field", onlyIfFieldMatchValue = "matched")
        private String dob3;
        private String dob3_day;
        private String dob3_month;
        private String dob3_year;
        @OnPage(15)
        private String ifOnlyDob3Field;

        @RequiredDateOnPage(value = 16,
                            minDate = "-1 Years",
                            maxDate = "+1 Years",
                            outOfRangeMessage = "Out of range message. Both limits in years")
        private String page16;
        private String page16_day;
        private String page16_month;
        private String page16_year;

        @RequiredDateOnPage(value = 17,
                            minDate = "-1 Year",
                            outOfRangeMessage = "Out of range message. Min limit only in years")
        private String page17;
        private String page17_day;
        private String page17_month;
        private String page17_year;

        @RequiredDateOnPage(value = 18,
                            maxDate = "+1 Year",
                            outOfRangeMessage = "Out of range message. Max limit only years")
        private String page18;
        private String page18_day;
        private String page18_month;
        private String page18_year;

        @RequiredDateOnPage(value = 19,
                            minDate = "-1 Days",
                            maxDate = "+1 Days",
                            outOfRangeMessage = "Out of range message. Both limits in days")
        private String page19;
        private String page19_day;
        private String page19_month;
        private String page19_year;

        @RequiredDateOnPage(value = 20,
                            minDate = "-1 Day",
                            outOfRangeMessage = "Out of range message. Min limit only in days")
        private String page20;
        private String page20_day;
        private String page20_month;
        private String page20_year;

        @RequiredDateOnPage(value = 21,
                            maxDate = "+1 Day",
                            outOfRangeMessage = "Out of range message. Max limit only days")
        private String page21;
        private String page21_day;
        private String page21_month;
        private String page21_year;

        @RequiredDateOnPage(value = 22,
                            minDate = "Today",
                            maxDate = "Today",
                            outOfRangeMessage = "Out of range message. Both limits Today")
        private String page22;
        private String page22_day;
        private String page22_month;
        private String page22_year;

        @RequiredDateOnPage(value = 23,
                            minDate = "Today",
                            outOfRangeMessage = "Out of range message. Min limit Today")
        private String page23;
        private String page23_day;
        private String page23_month;
        private String page23_year;

        @RequiredDateOnPage(value = 24,
                            maxDate = "Today",
                            outOfRangeMessage = "Out of range message. Max limit Today")
        private String page24;
        private String page24_day;
        private String page24_month;
        private String page24_year;

        @OnPage(99)
        private String dummyFinalPageField;

    }

    private MyTestData data;

    @Before
    public void setup() {
        data = new MyTestData();
        data.setOnBehalfOfUser("whatever");
    }

    @Test
    public void fieldPageNumberReadFromAllPageAnnotations() {
        assertThat(fieldPage(FieldUtils.getField(MyTestData.class, "page10Field", true))).isEqualTo(10);
        assertThat(fieldPage(FieldUtils.getField(MyTestData.class, "page11Field", true))).isEqualTo(11);
        assertThat(fieldPage(FieldUtils.getField(MyTestData.class, "page12Field", true))).isEqualTo(12);
        assertThat(fieldPage(FieldUtils.getField(MyTestData.class, "page13Field", true))).isEqualTo(13);
    }

    @Test
    public void optionalFieldsCanBeLeftNull() {
        data.setPageNumber(1);
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredFieldsLeftNullAreInvalid() {
        data.setPageNumber(2);
        assertThat(data.validate()).hasSize(1);
    }

    @Test
    public void requiredFieldWithoutMessageUseDefaultRequiredValidationMessage() {
        data.setPageNumber(2);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("requiredMessageLessField", "error.required"));
    }

    @Test
    public void requiredFieldWithMessageUsesAnnotatedMessage() {
        data.setPageNumber(3);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("requiredWithMessageField", "my validation message"));
    }

    @Test
    public void onlyIfRequiredFieldIsValidatedWhenLinkedFieldHasBooleanTrueValue() {
        data.setPageNumber(4);

        data.setIfOnlyBooleanField("false");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyBooleanField("true");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyBooleanField("true");
        data.setRequiredIfOnlyBooleanField("this is valid");
        assertThat(data.validate()).isEmpty();
    }
    @Test
    public void onlyIfRequiredFieldWithMatchValueIsValidatedWhenLinkedFieldHasMatchingValue() {
        data.setPageNumber(5);
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyStringField("notmatched");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyStringField("matched");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyStringField("matched");
        data.setRequiredIfOnlyStringField("this is valid");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void groupedFieldsFilterItemsMarkedWithNotErrorWhenInvalid() {
        data.setPageNumber(6);
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("groupedField1", "my group validation message"));
    }

    @Test
    public void groupedFieldsIncludeAllItemsByDefault() {
        data.setPageNumber(7);
        assertThat(data.validate()).hasSize(2);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("otherGroupedField1", "error.required"));
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("otherGroupedField2", "error.required"));
    }

    @Test
    public void requiredDateFieldWithMessageUsesAnnotatedMessage() {
        data.setPageNumber(8);
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob", "my mandatory date message"));
    }

    @Test
    public void dateFieldNotRequired() {
        data.setPageNumber(9);
        assertThat(data.validate()).hasSize(0);
    }

    @Test
    public void requiredIncompleteDateFieldWithMessageUsesAnnotatedMessage() {
        data.setPageNumber(8);
        data.setDob_day("19");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob", "my incomplete date message"));
    }

    @Test
    public void incompleteDateFieldWithMessageUsesAnnotatedMessage() {
        data.setPageNumber(9);
        data.setDob2_day("19");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob2", "my incomplete date 2 message"));
    }

    @Test
    public void requiredIncompleteDateFieldWithMessageUsesAnnotatedMessageEvenWhenTwoSupplied() {
        data.setPageNumber(8);
        data.setDob_day("19");
        data.setDob_month("7");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob", "my incomplete date message"));
    }

    @Test
    public void incompleteDateFieldWithMessageUsesAnnotatedMessageEvenWhenTwoSupplied() {
        data.setPageNumber(9);
        data.setDob2_day("19");
        data.setDob2_month("7");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob2", "my incomplete date 2 message"));
    }

    @Test
    public void requiredInvalidDateFieldWithMessageUsesAnnotatedMessageWhenInvalidDate() {
        data.setPageNumber(8);
        data.setDob_day("31");
        data.setDob_month("02");
        data.setDob_year("2003");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob", "my invalid date message"));
    }

    @Test
    public void invalidDateFieldWithMessageUsesAnnotatedMessageWhenInvalidDate() {
        data.setPageNumber(9);
        data.setDob2_day("31");
        data.setDob2_month("02");
        data.setDob2_year("2003");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob2", "my invalid date 2 message"));
    }

    @Test
    public void requiredDateFieldValidWithValidDateParts() {
        data.setPageNumber(8);
        data.setDob_day("01");
        data.setDob_month("02");
        data.setDob_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void dateFieldValidWithValidDateParts() {
        data.setPageNumber(9);
        data.setDob2_day("01");
        data.setDob2_month("02");
        data.setDob2_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldValidWithValidDatePartsWithNonPaddedNumbers() {
        data.setPageNumber(8);
        data.setDob_day("1");
        data.setDob_month("2");
        data.setDob_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void dateFieldValidWithValidDatePartsWithNonPaddedNumbers() {
        data.setPageNumber(9);
        data.setDob2_day("1");
        data.setDob2_month("2");
        data.setDob2_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void onlyIfRequiredDateFieldWithMatchValueIsRequiredValidatedWhenLinkedFieldHasMatchingValue() {
        data.setPageNumber(14);
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDobField("notmatched");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDobField("matched");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyDobField("matched");
        data.setConditionalDob_day("1");
        data.setConditionalDob_month("2");
        data.setConditionalDob_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void onlyIfDateFieldWithMatchValueIsRequiredValidatedWhenLinkedFieldHasMatchingValue() {
        data.setPageNumber(15);
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDob3Field("notmatched");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDob3Field("matched");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyDob3Field("matched");
        data.setDob3_day("1");
        data.setDob3_month("2");
        data.setDob3_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void onlyIfRequiredDateFieldWithMatchValueIsIncompleteValidatedWhenLinkedFieldHasMatchingValue() {
        data.setPageNumber(14);
        data.setConditionalDob_day("1");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDobField("notmatched");
        data.setConditionalDob_day("1");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDobField("matched");
        data.setConditionalDob_day("1");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyDobField("matched");
        data.setConditionalDob_day("1");
        data.setConditionalDob_month("2");
        data.setConditionalDob_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void onlyIfDateFieldWithMatchValueIsIncompleteValidatedWhenLinkedFieldHasMatchingValue() {
        data.setPageNumber(15);
        data.setDob3_day("1");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDob3Field("notmatched");
        data.setDob3_day("1");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDob3Field("matched");
        data.setDob3_day("1");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyDob3Field("matched");
        data.setDob3_day("1");
        data.setDob3_month("2");
        data.setDob3_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void onlyIfRequiredDateFieldWithMatchValueIsDateValidatedWhenLinkedFieldHasMatchingValue() {
        data.setPageNumber(14);
        data.setConditionalDob_day("31");
        data.setConditionalDob_month("2");
        data.setConditionalDob_year("2003");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDobField("notmatched");
        data.setConditionalDob_day("31");
        data.setConditionalDob_month("2");
        data.setConditionalDob_year("2003");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDobField("matched");
        data.setConditionalDob_day("31");
        data.setConditionalDob_month("2");
        data.setConditionalDob_year("2003");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyDobField("matched");
        data.setConditionalDob_day("1");
        data.setConditionalDob_month("2");
        data.setConditionalDob_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void onlyIfDateFieldWithMatchValueIsDateValidatedWhenLinkedFieldHasMatchingValue() {
        data.setPageNumber(15);
        data.setDob3_day("31");
        data.setDob3_month("2");
        data.setDob3_year("2003");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDob3Field("notmatched");
        data.setDob3_day("31");
        data.setDob3_month("2");
        data.setDob3_year("2003");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyDob3Field("matched");
        data.setDob3_day("31");
        data.setDob3_month("2");
        data.setDob3_year("2003");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyDob3Field("matched");
        data.setDob3_day("1");
        data.setDob3_month("2");
        data.setDob3_year("2003");
        assertThat(data.validate()).isEmpty();
    }

    // --- Date range tests ---

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsBelowMinYearsDateAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgoAndOneDay = now.minusYears(1).minusDays(1);

        data.setPageNumber(16);
        data.setPage16_day(String.valueOf(oneYearAgoAndOneDay.getDayOfMonth()));
        data.setPage16_month(String.valueOf(oneYearAgoAndOneDay.getMonthValue()));
        data.setPage16_year(String.valueOf(oneYearAgoAndOneDay.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page16", "Out of range message. Both limits in years"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsBelowMinDaysDateAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate twoDaysAgo = now.minusDays(2);

        data.setPageNumber(19);
        data.setPage19_day(String.valueOf(twoDaysAgo.getDayOfMonth()));
        data.setPage19_month(String.valueOf(twoDaysAgo.getMonthValue()));
        data.setPage19_year(String.valueOf(twoDaysAgo.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page19", "Out of range message. Both limits in days"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsBelowTodayDateAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        data.setPageNumber(22);
        data.setPage22_day(String.valueOf(yesterday.getDayOfMonth()));
        data.setPage22_month(String.valueOf(yesterday.getMonthValue()));
        data.setPage22_year(String.valueOf(yesterday.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page22", "Out of range message. Both limits Today"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsAboveMaxYearsDateAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAndOneDayInFuture = now.plusYears(1).plusDays(1);

        data.setPageNumber(16);
        data.setPage16_day(String.valueOf(oneYearAndOneDayInFuture.getDayOfMonth()));
        data.setPage16_month(String.valueOf(oneYearAndOneDayInFuture.getMonthValue()));
        data.setPage16_year(String.valueOf(oneYearAndOneDayInFuture.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page16", "Out of range message. Both limits in years"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsAboveMaxDaysDateAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate twoDaysInTheFuture = now.plusDays(2);

        data.setPageNumber(19);
        data.setPage19_day(String.valueOf(twoDaysInTheFuture.getDayOfMonth()));
        data.setPage19_month(String.valueOf(twoDaysInTheFuture.getMonthValue()));
        data.setPage19_year(String.valueOf(twoDaysInTheFuture.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page19", "Out of range message. Both limits in days"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsAboveTodayDateAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);

        data.setPageNumber(22);
        data.setPage22_day(String.valueOf(tomorrow.getDayOfMonth()));
        data.setPage22_month(String.valueOf(tomorrow.getMonthValue()));
        data.setPage22_year(String.valueOf(tomorrow.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page22", "Out of range message. Both limits Today"));
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnLowerBoundAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = now.minusYears(1);

        data.setPageNumber(16);
        data.setPage16_day(String.valueOf(oneYearAgo.getDayOfMonth()));
        data.setPage16_month(String.valueOf(oneYearAgo.getMonthValue()));
        data.setPage16_year(String.valueOf(oneYearAgo.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnLowerBoundAndBothBoundsAreSetForDays() {
        LocalDate now = LocalDate.now();
        LocalDate oneDayAgo = now.minusDays(1);

        data.setPageNumber(19);
        data.setPage19_day(String.valueOf(oneDayAgo.getDayOfMonth()));
        data.setPage19_month(String.valueOf(oneDayAgo.getMonthValue()));
        data.setPage19_year(String.valueOf(oneDayAgo.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsTodayAndBothBoundsAreSetForToday() {
        LocalDate today = LocalDate.now();

        data.setPageNumber(22);
        data.setPage22_day(String.valueOf(today.getDayOfMonth()));
        data.setPage22_month(String.valueOf(today.getMonthValue()));
        data.setPage22_year(String.valueOf(today.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnUpperBoundAndBothBoundsAreSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearInFuture = now.plusYears(1);

        data.setPageNumber(16);
        data.setPage16_day(String.valueOf(oneYearInFuture.getDayOfMonth()));
        data.setPage16_month(String.valueOf(oneYearInFuture.getMonthValue()));
        data.setPage16_year(String.valueOf(oneYearInFuture.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnUpperBoundAndBothBoundsAreSetForDays() {
        LocalDate now = LocalDate.now();
        LocalDate oneDayInFuture = now.plusDays(1);

        data.setPageNumber(19);
        data.setPage19_day(String.valueOf(oneDayInFuture.getDayOfMonth()));
        data.setPage19_month(String.valueOf(oneDayInFuture.getMonthValue()));
        data.setPage19_year(String.valueOf(oneDayInFuture.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsBelowMinYearsDateAndOnlyMinLimitSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgoAndOneDay = now.minusYears(1).minusDays(1);

        data.setPageNumber(17);
        data.setPage17_day(String.valueOf(oneYearAgoAndOneDay.getDayOfMonth()));
        data.setPage17_month(String.valueOf(oneYearAgoAndOneDay.getMonthValue()));
        data.setPage17_year(String.valueOf(oneYearAgoAndOneDay.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page17", "Out of range message. Min limit only in years"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsBelowMinDaysDateAndOnlyMinLimitSet() {
        LocalDate now = LocalDate.now();
        LocalDate twoDaysAgo = now.minusDays(2);

        data.setPageNumber(20);
        data.setPage20_day(String.valueOf(twoDaysAgo.getDayOfMonth()));
        data.setPage20_month(String.valueOf(twoDaysAgo.getMonthValue()));
        data.setPage20_year(String.valueOf(twoDaysAgo.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page20", "Out of range message. Min limit only in days"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsBelowTodayDateAndOnlyMinLimitSetToToday() {
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        data.setPageNumber(23);
        data.setPage23_day(String.valueOf(yesterday.getDayOfMonth()));
        data.setPage23_month(String.valueOf(yesterday.getMonthValue()));
        data.setPage23_year(String.valueOf(yesterday.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page23", "Out of range message. Min limit Today"));
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnLowerBoundAndOnlyMinLimitSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = now.minusYears(1);

        data.setPageNumber(17);
        data.setPage17_day(String.valueOf(oneYearAgo.getDayOfMonth()));
        data.setPage17_month(String.valueOf(oneYearAgo.getMonthValue()));
        data.setPage17_year(String.valueOf(oneYearAgo.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnLowerBoundAndOnlyMinLimitSetForDays() {
        LocalDate now = LocalDate.now();
        LocalDate oneDayAgo = now.minusDays(1);

        data.setPageNumber(20);
        data.setPage20_day(String.valueOf(oneDayAgo.getDayOfMonth()));
        data.setPage20_month(String.valueOf(oneDayAgo.getMonthValue()));
        data.setPage20_year(String.valueOf(oneDayAgo.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsTodayAndOnlyMinLimitSetForToday() {
        LocalDate today = LocalDate.now();

        data.setPageNumber(23);
        data.setPage23_day(String.valueOf(today.getDayOfMonth()));
        data.setPage23_month(String.valueOf(today.getMonthValue()));
        data.setPage23_year(String.valueOf(today.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsAboveMaxYearsDateAndOnlyMaxLimitSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAndOneDayInFuture = now.plusYears(1).plusDays(1);

        data.setPageNumber(18);
        data.setPage18_day(String.valueOf(oneYearAndOneDayInFuture.getDayOfMonth()));
        data.setPage18_month(String.valueOf(oneYearAndOneDayInFuture.getMonthValue()));
        data.setPage18_year(String.valueOf(oneYearAndOneDayInFuture.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page18", "Out of range message. Max limit only years"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsAboveMaxDaysDateAndOnlyMaxLimitSet() {
        LocalDate now = LocalDate.now();
        LocalDate twoDayInFuture = now.plusDays(2);

        data.setPageNumber(21);
        data.setPage21_day(String.valueOf(twoDayInFuture.getDayOfMonth()));
        data.setPage21_month(String.valueOf(twoDayInFuture.getMonthValue()));
        data.setPage21_year(String.valueOf(twoDayInFuture.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page21", "Out of range message. Max limit only days"));
    }

    @Test
    public void requiredDateFieldUsesMessageWhenDateIsAboveMaxDaysDateAndOnlyMaxLimitSetToToday() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);

        data.setPageNumber(24);
        data.setPage24_day(String.valueOf(tomorrow.getDayOfMonth()));
        data.setPage24_month(String.valueOf(tomorrow.getMonthValue()));
        data.setPage24_year(String.valueOf(tomorrow.getYear()));
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page24", "Out of range message. Max limit Today"));
    }

    @Test
    public void outOfRangeValidationWorksWithSpaceAtEndOfDates() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);

        data.setPageNumber(24);
        data.setPage24_day(String.valueOf(tomorrow.getDayOfMonth()));
        data.setPage24_month(String.valueOf(tomorrow.getMonthValue()));
        data.setPage24_year(tomorrow.getYear() + " ");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("page24", "Out of range message. Max limit Today"));
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnUpperBoundAndOnlyMaxLimitSet() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearInFuture = now.plusYears(1);

        data.setPageNumber(18);
        data.setPage18_day(String.valueOf(oneYearInFuture.getDayOfMonth()));
        data.setPage18_month(String.valueOf(oneYearInFuture.getMonthValue()));
        data.setPage18_year(String.valueOf(oneYearInFuture.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsOnUpperBoundAndOnlyMaxLimitSetForDays() {
        LocalDate now = LocalDate.now();
        LocalDate oneDayInFuture = now.plusDays(1);

        data.setPageNumber(21);
        data.setPage21_day(String.valueOf(oneDayInFuture.getDayOfMonth()));
        data.setPage21_month(String.valueOf(oneDayInFuture.getMonthValue()));
        data.setPage21_year(String.valueOf(oneDayInFuture.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void requiredDateFieldNoErrorsWhenDateIsTodayAndOnlyMaxLimitSetForToday() {
        LocalDate today = LocalDate.now();

        data.setPageNumber(24);
        data.setPage24_day(String.valueOf(today.getDayOfMonth()));
        data.setPage24_month(String.valueOf(today.getMonthValue()));
        data.setPage24_year(String.valueOf(today.getYear()));
        assertThat(data.validate()).isEmpty();
    }

    @Test
    public void itCombinesTheDateFieldsCorrectly() {
        data.setPageNumber(8);
        data.setDob_day("24");
        data.setDob_month("09");
        data.setDob_year("1982");
        assertThat(data.getDob()).isEqualTo("24/09/1982");
    }


}