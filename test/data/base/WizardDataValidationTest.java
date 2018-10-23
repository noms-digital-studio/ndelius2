package data.base;

import com.google.common.collect.ImmutableList;
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

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

        @OnPage(99)
        private String dummyFinalPageField;

        protected List<Function<Map<String, Object>, Stream<ValidationError>>> reportSpecificValidators() {
            return ImmutableList.of();
        }
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

}