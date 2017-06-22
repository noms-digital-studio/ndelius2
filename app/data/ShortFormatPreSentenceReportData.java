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
    @JsonProperty("DELIUS_CRN")
    private String crn;

    @JsonProperty("PNC_ID")
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

    @JsonProperty("INTERVIEW_INFORMATION_SOURCE")
    private boolean interviewInformationSource;

    @JsonProperty("SERVICE_RECORDS_INFORMATION_SOURCE")
    private boolean serviceRecordsInformationSource;

    @JsonProperty("CPS_SUMMARY_INFORMATION_SOURCE")
    private boolean cpsSummaryInformationSource;

    @JsonProperty("OASYS_ASSESSMENTS_INFORMATION_SOURCE")
    private boolean oasysAssessmentsInformationSource;

    @JsonProperty("PREVIOUS_CONVICTIONS_INFORMATION_SOURCE")
    private boolean previousConvictionsInformationSource;

    @JsonProperty("VICTIM_STATEMENT_INFORMATION_SOURCE")
    private boolean victimStatementInformationSource;

    @JsonProperty("CHILDREN_SERVICES_INFORMATION_SOURCE")
    private boolean childrenServicesInformationSource;

    @JsonProperty("POLICE_INFORMATION_SOURCE")
    private boolean policeInformationSource;

    @JsonProperty("OTHER_INFORMATION_SOURCE")
    private boolean otherInformationSource;

    @RequiredOnPage(3)
    @SpellCheck(overrideField = "ignoreOtherInformationDetailsSpelling")
    @JsonProperty("OTHER_INFORMATION_DETAILS")
    private String otherInformationDetails;

    @JsonIgnore
    private boolean ignoreOtherInformationDetailsSpelling;

}
