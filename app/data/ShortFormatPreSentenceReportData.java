package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @OnPage(1)
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


    @RequiredOnPage(6)
    @SpellCheck(overrideField = "ignorePatternOfOffendingSpelling")
    @JsonProperty("PATTERN_OF_OFFENDING")
    private String patternOfOffending;

    @JsonIgnore
    private boolean ignorePatternOfOffendingSpelling;


    @RequiredOnPage(7)
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

    @JsonProperty("DD_MMM_YYYY")
    private String reportDate;


    @Override
    public List<ValidationError> validate() {

        reportDate = new SimpleDateFormat("dd MMMM yyy").format(new Date());

        return super.validate();
    }
}
