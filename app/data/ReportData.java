package data;

import com.fasterxml.jackson.annotation.JsonProperty;
import data.annotations.RequiredOnPage;
import data.base.WizardData;
import lombok.Data;

@Data
public class ReportData extends WizardData {

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

    @JsonProperty("DD_MMM_YYYY")
    private String reportDate;
}
