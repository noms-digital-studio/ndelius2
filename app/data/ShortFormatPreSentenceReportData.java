package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredOnPage;
import data.base.ReportGeneratorWizardData;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @OnPage(2)
    @JsonProperty("_ADDRESS_")
    private String address;

    @JsonIgnore
    private boolean addressSupplied;

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
    @JsonProperty("OTHER_INFORMATION_DETAILS")
    private String otherInformationDetails;


    // Page 5

    @RequiredOnPage(5)
    @JsonProperty("MAIN_OFFENCE")
    private String mainOffence;

    @OnPage(5)
    @JsonProperty("OTHER_OFFENCE")
    private String otherOffences;

    @RequiredOnPage(5)
    @JsonProperty("OFFENCE_SUMMARY")
    private String offenceSummary;


    // Page 6

    @RequiredOnPage(6)
    @JsonProperty("OFFENCE_ANALYSIS")
    private String offenceAnalysis;

    @OnPage(6)
    @JsonProperty("PATTERN_OF_OFFENDING")
    private String patternOfOffending;


    // Page 7

    @OnPage(7)
    @JsonProperty("ISSUE_ACCOMMODATION")
    private boolean issueAccommodation;

    @OnPage(7)
    @JsonProperty("ISSUE_EMPLOYMENT")
    private boolean issueEmployment;

    @OnPage(7)
    @JsonProperty("ISSUE_FINANCE")
    private boolean issueFinance;

    @OnPage(7)
    @JsonProperty("ISSUE_DRUGS")
    private boolean issueDrugs;

    @OnPage(7)
    @JsonProperty("ISSUE_ALCOHOL")
    private boolean issueAlcohol;

    @OnPage(7)
    @JsonProperty("ISSUE_HEALTH")
    private boolean issueHealth;

    @OnPage(7)
    @JsonProperty("ISSUE_BEHAVIOUR")
    private boolean issueBehaviour;

    @RequiredOnPage(7)
    @JsonProperty("OFFENDER_ASSESSMENT")
    private String offenderAssessment;


    // Page 8

    @RequiredOnPage(8)
    @JsonProperty("RISK_OF_SERIOUS_HARM")
    private String riskOfSeriousHarm;

    @RequiredOnPage(8)
    @JsonProperty("LIKELIHOOD_OF_RE_OFFENDING")
    private String likelihoodOfReOffending;

    @RequiredOnPage(8)
    @JsonProperty("PREVIOUS_SUPERVISION_RESPONSE")
    private String previousSupervisionResponse;

    @OnPage(8)
    @JsonProperty("ADDITIONAL_PREVIOUS_SUPERVISION")
    private String additionalPreviousSupervision;

    // Page 9

    @RequiredOnPage(9)
    @JsonProperty("PROPOSAL")
    private String proposal;


    // Page 10

    // Intentionally Blank


    // Page 11

    @RequiredOnPage(11)
    @JsonProperty("REPORT_AUTHOR")
    private String reportAuthor;

    @RequiredOnPage(11)
    @JsonProperty("OFFICE")
    private String office;

    @OnPage(11)
    @JsonProperty("REPORT_DATE")
    private String reportDate;

    @OnPage(11)
    @JsonProperty("COURT_OFFICE_PHONE_NUMBER")
    private String courtOfficePhoneNumber;

    @OnPage(11)
    @JsonProperty("COUNTER_SIGNATURE")
    private String counterSignature;

    @RequiredOnPage(11)
    @Encrypted
    @JsonProperty("START_DATE")
    private String startDate;


    @Override
    public List<ValidationError> validate() {

        if (Strings.isNullOrEmpty(reportDate)) { // Fill in for the first read only page, then keep any user changes

            reportDate = new SimpleDateFormat("dd/MM/yyy").format(new Date());

        }

        if (Strings.isNullOrEmpty(startDate)) { // Fill in for the first read only page, then keep any user changes

            startDate = new SimpleDateFormat("dd/MM/yyy").format(new Date());

        }

        return super.validate();
    }
}
