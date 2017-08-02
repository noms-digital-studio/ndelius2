package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredOnPage;
import data.annotations.SpellCheck;
import data.base.ReportGeneratorWizardData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import play.data.validation.ValidationError;

@Data
@EqualsAndHashCode(callSuper=false)
public class ShortFormatPreSentenceReportData extends ReportGeneratorWizardData {

    // Page 2

    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("_NAME_")
    private String name;

    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("_DATE_OF_BIRTH_")
    private String dateOfBirth;

    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("_AGE_")
    private Integer age;

    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("_ADDRESS_")
    private String address;

    @Encrypted
    @OnPage(2)
    @JsonProperty("_PNC_ID_")
    private String pnc;

    @JsonIgnore
    private boolean pncSupplied;


    // Page 3

    @Encrypted
    @RequiredOnPage(3)
    @JsonProperty("_COURT_")
    private String court;

    @Encrypted
    @RequiredOnPage(3)
    @JsonProperty("_DATE_OF_HEARING_")
    private String dateOfHearing;

    @Encrypted
    @RequiredOnPage(3)
    @JsonProperty("_LOCAL_JUSTICE_AREA_")
    private String localJusticeArea;


    // Page 4

    @OnPage(4)
    @JsonProperty("INTERVIEW_INFORMATION_SOURCE")
    private boolean interviewInformationSource;

    @OnPage(4)
    @JsonProperty("SERVICE_RECORDS_INFORMATION_SOURCE")
    private boolean serviceRecordsInformationSource;

    @OnPage(4)
    @JsonProperty("CPS_SUMMARY_INFORMATION_SOURCE")
    private boolean cpsSummaryInformationSource;

    @OnPage(4)
    @JsonProperty("OASYS_ASSESSMENTS_INFORMATION_SOURCE")
    private boolean oasysAssessmentsInformationSource;

    @OnPage(4)
    @JsonProperty("PREVIOUS_CONVICTIONS_INFORMATION_SOURCE")
    private boolean previousConvictionsInformationSource;

    @OnPage(4)
    @JsonProperty("VICTIM_STATEMENT_INFORMATION_SOURCE")
    private boolean victimStatementInformationSource;

    @OnPage(4)
    @JsonProperty("CHILDREN_SERVICES_INFORMATION_SOURCE")
    private boolean childrenServicesInformationSource;

    @OnPage(4)
    @JsonProperty("POLICE_INFORMATION_SOURCE")
    private boolean policeInformationSource;

    @OnPage(4)
    @JsonProperty("OTHER_INFORMATION_SOURCE")
    private boolean otherInformationSource;

    @RequiredOnPage(value = 4, onlyIfField = "otherInformationSource")
    @SpellCheck(overrideField = "ignoreOtherInformationDetailsSpelling")
    @JsonProperty("OTHER_INFORMATION_DETAILS")
    private String otherInformationDetails;

    @JsonIgnore
    private boolean ignoreOtherInformationDetailsSpelling;


    // Page 5

    @RequiredOnPage(5)
    @SpellCheck(overrideField = "ignoreMainOffenceSpelling")
    @JsonProperty("MAIN_OFFENCE")
    private String mainOffence;

    @JsonIgnore
    private boolean ignoreMainOffenceSpelling;

    @RequiredOnPage(5)
    @SpellCheck(overrideField = "ignoreOffenceSummarySpelling")
    @JsonProperty("OFFENCE_SUMMARY")
    private String offenceSummary;

    @JsonIgnore
    private boolean ignoreOffenceSummarySpelling;

    @RequiredOnPage(5)
    @SpellCheck(overrideField = "ignoreOffenceAnalysisSpelling")
    @JsonProperty("OFFENCE_ANALYSIS")
    private String offenceAnalysis;

    @JsonIgnore
    private boolean ignoreOffenceAnalysisSpelling;


    // Page 6

    @OnPage(6)
    @JsonProperty("ISSUE_ACCOMMODATION")
    private boolean issueAccommodation;

    @OnPage(6)
    @JsonProperty("ISSUE_EMPLOYMENT")
    private boolean issueEmployment;

    @OnPage(6)
    @JsonProperty("ISSUE_FINANCE")
    private boolean issueFinance;

    @OnPage(6)
    @JsonProperty("ISSUE_DRUGS")
    private boolean issueDrugs;

    @OnPage(6)
    @JsonProperty("ISSUE_ALCOHOL")
    private boolean issueAlcohol;

    @OnPage(6)
    @JsonProperty("ISSUE_HEALTH")
    private boolean issueHealth;

    @OnPage(6)
    @JsonProperty("ISSUE_BEHAVIOUR")
    private boolean issueBehaviour;


    // Page 7

    @RequiredOnPage(7)
    @SpellCheck(overrideField = "ignoreOffenderAssessmentSpelling")
    @JsonProperty("OFFENDER_ASSESSMENT")
    private String offenderAssessment;

    @JsonIgnore
    private boolean ignoreOffenderAssessmentSpelling;


    // Page 8

    @RequiredOnPage(8)
    @SpellCheck(overrideField = "ignorePatternOfOffendingSpelling")
    @JsonProperty("PATTERN_OF_OFFENDING")
    private String patternOfOffending;

    @JsonIgnore
    private boolean ignorePatternOfOffendingSpelling;


    // Page 9

    @RequiredOnPage(9)
    @JsonProperty("PREVIOUS_SUPERVISION_RESPONSE")
    private String previousSupervisionResponse;         // Radio button enum, so no spelling check needed

    @OnPage(9)
    @SpellCheck(overrideField = "ignoreAdditionalPreviousSupervisionSpelling")
    @JsonProperty("ADDITIONAL_PREVIOUS_SUPERVISION")
    private String additionalPreviousSupervision;

    @JsonIgnore
    private boolean ignoreAdditionalPreviousSupervisionSpelling;

    @RequiredOnPage(9)
    @SpellCheck(overrideField = "ignoreLikelihoodOfReOffendingSpelling")
    @JsonProperty("LIKELIHOOD_OF_RE_OFFENDING")
    private String likelihoodOfReOffending;

    @JsonIgnore
    private boolean ignoreLikelihoodOfReOffendingSpelling;


    // Page 10

    @RequiredOnPage(10)
    @SpellCheck(overrideField = "ignoreRiskOfSeriousHarmSpelling")
    @JsonProperty("RISK_OF_SERIOUS_HARM")
    private String riskOfSeriousHarm;

    @JsonIgnore
    private boolean ignoreRiskOfSeriousHarmSpelling;


    // Page 11

    @RequiredOnPage(11)
    @SpellCheck(overrideField = "ignoreProposalSpelling")
    @JsonProperty("PROPOSAL")
    private String proposal;

    @JsonIgnore
    private boolean ignoreProposalSpelling;


    // Page 12

    @RequiredOnPage(12)
    @JsonProperty("REPORT_AUTHOR")
    private String reportAuthor;

    @RequiredOnPage(12)
    @JsonProperty("OFFICE")
    private String office;

    @JsonProperty("REPORT_DATE")
    private String reportDate;


    @Override
    public List<ValidationError> validate() {

        if (Strings.isNullOrEmpty(reportDate)) { // Fill in for the first read only page, then keep any user changes

            reportDate = new SimpleDateFormat("dd/MM/yyy").format(new Date());
        }

        return super.validate();
    }
}
