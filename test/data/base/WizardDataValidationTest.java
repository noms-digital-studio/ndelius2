package data.base;

import data.annotations.OnPage;
import data.annotations.RequiredOnPage;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import play.data.validation.ValidationError;

import static org.assertj.core.api.Assertions.assertThat;

public class WizardDataValidationTest {
    @Data
    static class MyTestData extends WizardData {
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

        @OnPage(6)
        private String dummyFinalPageField;

    }

    private MyTestData data;

    @Before
    public void setup() {
        data = new MyTestData();
        data.setOnBehalfOfUser("whatever");
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

        data.setIfOnlyStringField("notmatched");
        assertThat(data.validate()).isEmpty();

        data.setIfOnlyStringField("matched");
        assertThat(data.validate()).hasSize(1);

        data.setIfOnlyStringField("matched");
        data.setRequiredIfOnlyStringField("this is valid");
        assertThat(data.validate()).isEmpty();
    }
}