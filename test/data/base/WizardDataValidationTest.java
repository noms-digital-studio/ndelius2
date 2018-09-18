package data.base;

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

        @RequiredDateOnPage(value = 8, message = "my mandatory date message", incompleteMessage = "my incomplete date message", invalidMessage = "my invalid date message")
        private String dob;
        private String dob_day;
        private String dob_month;
        private String dob_year;

        @OnPage(9)
        private String dummyFinalPageField;

        @OnPage(10)
        private String page10Field;
        @RequiredOnPage(11)
        private String page11Field;
        @RequiredGroupOnPage(12)
        private String page12Field;
        @RequiredDateOnPage(13)
        private String page13Field;

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
    public void requiredIncompleteDateFieldWithMessageUsesAnnotatedMessage() {
        data.setPageNumber(8);
        data.setDob_day("19");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob", "my incomplete date message"));
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
    public void requiredInvalidDateFieldWithMessageUsesAnnotatedMessageWhenInvalidDate() {
        data.setPageNumber(8);
        data.setDob_day("31");
        data.setDob_month("02");
        data.setDob_year("2003");
        assertThat(data.validate()).hasSize(1);
        assertThat(data.validate()).usingFieldByFieldElementComparator().contains(new ValidationError("dob", "my invalid date message"));
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
    public void requiredDateFieldValidWithValidDatePartsWithNonPaddedNumbers() {
        data.setPageNumber(8);
        data.setDob_day("1");
        data.setDob_month("2");
        data.setDob_year("2003");
        assertThat(data.validate()).isEmpty();
    }

}