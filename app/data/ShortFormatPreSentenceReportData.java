package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredOnPage;
import data.annotations.SpellCheck;
import data.base.WizardData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import play.data.validation.ValidationError;

@Data
@EqualsAndHashCode(callSuper=false)
public class ShortFormatPreSentenceReportData extends WizardData {

    @Encrypted
    @RequiredOnPage(1)
    @JsonIgnore
    private String onBehalfOfUser;

    @Encrypted
    @RequiredOnPage(1)
    @JsonIgnore
    private Long entityId;

    @Encrypted
    @JsonIgnore
    private String documentId;


    @Encrypted
    @RequiredOnPage(1)
    @JsonProperty("_NAME_")
    private String name;

    @Encrypted
    @RequiredOnPage(1)
    @JsonProperty("_DATE_OF_BIRTH_")
    private String dateOfBirth;

    @Encrypted
    @RequiredOnPage(1)
    @JsonProperty("_AGE_")
    private Integer age;

    @Encrypted
    @RequiredOnPage(1)
    @JsonProperty("_ADDRESS_")
    private String address;

    @Encrypted
    @RequiredOnPage(1)
    @JsonProperty("_DELIUS_CRN_")
    private String crn;

    @Encrypted
    @OnPage(1)
    @JsonProperty("_PNC_ID_")
    private String pnc;

    @JsonIgnore
    private boolean pncSupplied;


    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("_COURT_")
    private String court;

    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("_DATE_OF_HEARING_")
    private String dateOfHearing;

    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("_LOCAL_JUSTICE_AREA_")
    private String localJusticeArea;


    @OnPage(3)
    @JsonProperty("INTERVIEW_INFORMATION_SOURCE")
    private boolean interviewInformationSource;

    @OnPage(3)
    @JsonProperty("SERVICE_RECORDS_INFORMATION_SOURCE")
    private boolean serviceRecordsInformationSource;

    @OnPage(3)
    @JsonProperty("CPS_SUMMARY_INFORMATION_SOURCE")
    private boolean cpsSummaryInformationSource;

    @OnPage(3)
    @JsonProperty("OASYS_ASSESSMENTS_INFORMATION_SOURCE")
    private boolean oasysAssessmentsInformationSource;

    @OnPage(3)
    @JsonProperty("PREVIOUS_CONVICTIONS_INFORMATION_SOURCE")
    private boolean previousConvictionsInformationSource;

    @OnPage(3)
    @JsonProperty("VICTIM_STATEMENT_INFORMATION_SOURCE")
    private boolean victimStatementInformationSource;

    @OnPage(3)
    @JsonProperty("CHILDREN_SERVICES_INFORMATION_SOURCE")
    private boolean childrenServicesInformationSource;

    @OnPage(3)
    @JsonProperty("POLICE_INFORMATION_SOURCE")
    private boolean policeInformationSource;

    @OnPage(3)
    @JsonProperty("OTHER_INFORMATION_SOURCE")
    private boolean otherInformationSource;

    @RequiredOnPage(value = 3, onlyIfField = "otherInformationSource")
    @SpellCheck(overrideField = "ignoreOtherInformationDetailsSpelling")
    @JsonProperty("OTHER_INFORMATION_DETAILS")
    private String otherInformationDetails;

    @JsonIgnore
    private boolean ignoreOtherInformationDetailsSpelling;


    @RequiredOnPage(4)
    @SpellCheck(overrideField = "ignoreMainOffenceSpelling")
    @JsonProperty("MAIN_OFFENCE")
    private String mainOffence;

    @JsonIgnore
    private boolean ignoreMainOffenceSpelling;

    @RequiredOnPage(4)
    @SpellCheck(overrideField = "ignoreOffenceSummarySpelling")
    @JsonProperty("OFFENCE_SUMMARY")
    private String offenceSummary;

    @JsonIgnore
    private boolean ignoreOffenceSummarySpelling;

    @RequiredOnPage(4)
    @SpellCheck(overrideField = "ignoreOffenceAnalysisSpelling")
    @JsonProperty("OFFENCE_ANALYSIS")
    private String offenceAnalysis;

    @JsonIgnore
    private boolean ignoreOffenceAnalysisSpelling;


    @RequiredOnPage(5)
    @SpellCheck(overrideField = "ignoreOffenderAssessmentSpelling")
    @JsonProperty("OFFENDER_ASSESSMENT")
    private String offenderAssessment;

    @JsonIgnore
    private boolean ignoreOffenderAssessmentSpelling;


    @OnPage(6)
    @SpellCheck(overrideField = "ignorePatternOfOffendingSpelling")
    @JsonProperty("PATTERN_OF_OFFENDING")
    private String patternOfOffending;

    @JsonIgnore
    private boolean ignorePatternOfOffendingSpelling;

    @OnPage(7)
    @SpellCheck(overrideField = "ignorePreviousSupervisionResponseSpelling")
    @JsonProperty("PREVIOUS_SUPERVISION_RESPONSE")
    private String previousSupervisionResponse;

    @JsonIgnore
    private boolean ignorePreviousSupervisionResponseSpelling;

    @RequiredOnPage(7)
    @SpellCheck(overrideField = "ignoreLikelihoodOfReOffendingSpelling")
    @JsonProperty("LIKELIHOOD_OF_RE_OFFENDING")
    private String likelihoodOfReOffending;

    @JsonIgnore
    private boolean ignoreLikelihoodOfReOffendingSpelling;


    @RequiredOnPage(8)
    @SpellCheck(overrideField = "ignoreRiskOfSeriousHarmSpelling")
    @JsonProperty("RISK_OF_SERIOUS_HARM")
    private String riskOfSeriousHarm;

    @JsonIgnore
    private boolean ignoreRiskOfSeriousHarmSpelling;


    @RequiredOnPage(9)
    @SpellCheck(overrideField = "ignoreProposalSpelling")
    @JsonProperty("PROPOSAL")
    private String proposal;

    @JsonIgnore
    private boolean ignoreProposalSpelling;


    @RequiredOnPage(10)
    @JsonProperty("REPORT_AUTHOR")
    private String reportAuthor;

    @RequiredOnPage(10)
    @JsonProperty("OFFICE")
    private String office;

    @JsonProperty("REPORT_DATE")
    private String reportDate;


    @Override
    public List<ValidationError> validate() {

        if (Strings.isNullOrEmpty(reportDate)) { // Fill in for the first read only page, then keep any user changes

            reportDate = new SimpleDateFormat("dd MMMM yyy").format(new Date());
        }

        return super.validate();
    }
}
