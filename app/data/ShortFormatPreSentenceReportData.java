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
public class ShortFormatPreSentenceReportData extends WizardData {

    @RequiredOnPage(1)
    @JsonProperty("NAME")
    private String name;

    @RequiredOnPage(1)
    @JsonProperty("DATE_OF_BIRTH")
    private String dateOfBirth;

    @RequiredOnPage(1)
    @JsonProperty("AGE")
    private Integer age;

    @RequiredOnPage(1)
    @JsonProperty("ADDRESS")
    private String address;

    @RequiredOnPage(1)
    @JsonProperty("CRN")
    private String crn;

    @JsonProperty("PNC")
    private String pnc;

    @RequiredOnPage(2)
    @JsonProperty("COURT")
    private String court;

    @RequiredOnPage(2)
    @JsonProperty("DATE_OF_HEARING")
    private String dateOfHearing;

    @RequiredOnPage(2)
    @JsonProperty("LOCAL_JUSTICE_AREA")
    private String localJusticeArea;

    @RequiredOnPage(3)
    @SpellCheck(overrideField = "ignoreOtherInformationSourceSpelling")
    @JsonProperty("OTHER_INFORMATION_SOURCE")
    private String otherInformationSource;

    @JsonIgnore
    private Boolean ignoreOtherInformationSourceSpelling;

}
