package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import data.annotations.RequiredOnPage;
import data.annotations.SpellCheck;
import data.base.WizardData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SampleReportData extends WizardData {

    @JsonIgnore
    private String identifier;

    @RequiredOnPage(1)
    @JsonProperty("SALUTATION")
    private String salutation;

    @RequiredOnPage(1)
    @JsonProperty("FORENAME")
    private String forename;

    @RequiredOnPage(1)
    @JsonProperty("SURNAME")
    private String surname;

    @RequiredOnPage(2)
    @JsonProperty("ADDRESS_LINE_1")
    private String address1;

    @RequiredOnPage(2)
    @JsonProperty("ADDRESS_LINE_2")
    private String address2;

    @JsonProperty("ADDRESS_LINE_3")
    private String address3;

    @RequiredOnPage(3)
    @JsonProperty("CASE_NUMBER")
    private String caseNumber;

    @SpellCheck(overrideField = "ignoreNotesErrors")
    @JsonProperty("LETTER_NOTES")
    private String letterNotes;

    @JsonIgnore
    private Boolean ignoreNotesErrors;

    @JsonProperty("DD_MMM_YYYY")
    private String reportDate;
}
