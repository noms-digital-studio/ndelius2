package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import data.annotations.*;
import data.base.ReportGeneratorWizardData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import play.data.validation.ValidationError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ToString(of = {"name"}, callSuper = true)
@Data
@EqualsAndHashCode(callSuper=false)
public class OralReportData extends ReportGeneratorWizardData {

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
    @JsonIgnore
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
    @RequiredOnPage(value = 3, message = "Enter the court")
    @JsonProperty("_COURT_")
    private String court;

    @Encrypted
    @RequiredOnPage(value = 3, message = "Enter the local justice area")
    @JsonProperty("_LOCAL_JUSTICE_AREA_")
    private String localJusticeArea;

    @JsonProperty("_DATE_OF_HEARING_")
    public String getDateOfHearing() {
        return formattedDateFromDatePartsDefaultToday("dateOfHearing");
    }

    @Encrypted
    @RequiredDateOnPage(value = 3,
            message = "Enter the date of hearing",
            incompleteMessage = "Enter the date of hearing and include a day, month and year",
            invalidMessage = "Enter a real date of hearing")
    private String dateOfHearing;
    private String dateOfHearing_day;
    private String dateOfHearing_month;
    private String dateOfHearing_year;

    // Page 4

    @RequiredOnPage(value = 4, message = "Enter the main offence and date")
    @JsonProperty("MAIN_OFFENCE")
    private String mainOffence;

    @OnPage(4)
    @JsonProperty("OTHER_OFFENCE")
    private String otherOffences;


    // Page 5

    @RequiredOnPage(value = 5, message = "Enter your analysis of the offence")
    @JsonProperty("OFFENCE_ANALYSIS")
    private String offenceAnalysis;

    @RequiredOnPage(value = 5, message = "Is current offending part of a pattern of offending behaviour?")
    @JsonProperty("PATTERN_OF_OFFENDING")
    private String patternOfOffending;

    @RequiredOnPage(value = 5, message = "Does current offending represent an escalation in seriousness?")
    @JsonProperty("ESCALATION_IN_SERIOUSNESS")
    private String escalationInSeriousness;


    // Page 6

    @RequiredGroupOnPage(value = 6, message = "Select underlying issues from the options below")
    @JsonProperty("ISSUE_ACCOMMODATION")
    private boolean issueAccommodation;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_ACCOMMODATION_DETAILS")
    private String issueAccommodationDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_EMPLOYMENT")
    private boolean issueEmployment;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_EMPLOYMENT_DETAILS")
    private String issueEmploymentDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_FINANCE")
    private boolean issueFinance;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_FINANCE_DETAILS")
    private String issueFinanceDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_RELATIONSHIPS")
    private boolean issueRelationships;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_RELATIONSHIPS_DETAILS")
    private String issueRelationshipsDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_SUBSTANCE_MISUSE")
    private boolean issueSubstanceMisuse;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_SUBSTANCE_MISUSE_DETAILS")
    private String issueSubstanceMisuseDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_HEALTH")
    private boolean issueHealth;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_HEALTH_DETAILS")
    private String issueHealthDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_BEHAVIOUR")
    private boolean issueBehaviour;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_BEHAVIOUR_DETAILS")
    private String issueBehaviourDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_MATURITY")
    private boolean issueMaturity;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_MATURITY_DETAILS")
    private String issueMaturityDetails;

    @RequiredGroupOnPage(value = 6, errorWhenInvalid = false)
    @JsonProperty("ISSUE_OTHER")
    private boolean issueOther;

    @OnPage(value = 6)
    @JsonProperty("ISSUE_OTHER_DETAILS")
    private String issueOtherDetails;



    // Page 7

    @RequiredOnPage(value = 7, message = "Tool")
    @JsonProperty("LIKELIHOOD_TOOL_1")
    private String likelihoodTool1;

    @RequiredOnPage(value = 7, message = "Assessment level")
    @JsonProperty("LIKELIHOOD_LEVEL_1")
    private String likelihoodLevel1;

    @OnPage(value = 7)
    @JsonProperty("LIKELIHOOD_TOOL_2")
    private String likelihoodTool2;

    @OnPage(value = 7)
    @JsonProperty("LIKELIHOOD_LEVEL_2")
    private String likelihoodLevel2;

    @OnPage(value = 7)
    @JsonProperty("LIKELIHOOD_TOOL_3")
    private String likelihoodTool3;

    @OnPage(value = 7)
    @JsonProperty("LIKELIHOOD_LEVEL_3")
    private String likelihoodLevel3;

    @RequiredOnPage(value = 7, message = "Enter the risk of serious harm")
    @JsonProperty("ROSH_LEVEL")
    private String riskOfSeriousHarm;

    @RequiredOnPage(value = 7, message = "Enter the evidence for Risk Assessment level")
    @JsonProperty("ROSH_EVIDENCE")
    private String roshEvidence;

    @RequiredOnPage(value = 7, message = "Enter the response to previous supervision")
    @JsonProperty("PREVIOUS_SUPERVISION_RESPONSE")
    private String previousSupervisionResponse;

    // Page 8

    @RequiredOnPage(value = 8, message = "Enter your proposed sentence")
    @JsonProperty("_PROPOSAL_")
    private String proposal;

    @RequiredOnPage(value = 8, message = "Confirm that you have considered equality and diversity information")
    @JsonProperty("CONFIRM_EIF")
    private String confirmEIF;


    // Page 9

    @OnPage(9)
    @JsonProperty("INTERVIEW_INFORMATION_SOURCE")
    private boolean interviewInformationSource;

    @OnPage(9)
    @JsonProperty("SERVICE_RECORDS_INFORMATION_SOURCE")
    private boolean serviceRecordsInformationSource;

    @OnPage(9)
    @JsonProperty("CPS_SUMMARY_INFORMATION_SOURCE")
    private boolean cpsSummaryInformationSource;

    @OnPage(9)
    @JsonProperty("OASYS_ASSESSMENTS_INFORMATION_SOURCE")
    private boolean oasysAssessmentsInformationSource;

    @OnPage(9)
    @JsonProperty("PREVIOUS_CONVICTIONS_INFORMATION_SOURCE")
    private boolean previousConvictionsInformationSource;

    @OnPage(9)
    @JsonProperty("VICTIM_STATEMENT_INFORMATION_SOURCE")
    private boolean victimStatementInformationSource;

    @OnPage(9)
    @JsonProperty("CHILDREN_SERVICES_INFORMATION_SOURCE")
    private boolean childrenServicesInformationSource;

    @OnPage(9)
    @JsonProperty("POLICE_INFORMATION_SOURCE")
    private boolean policeInformationSource;

    @OnPage(9)
    @JsonProperty("SENTENCING_GUIDELINES_INFORMATION_SOURCE")
    private boolean sentencingGuidelinesInformationSource;

    @OnPage(9)
    @JsonProperty("DOMESTIC_ABUSE_INFORMATION_SOURCE")
    private boolean domesticAbuseInformationSource;

    @OnPage(9)
    @JsonProperty("EQUALITY_INFORMATION_FORM_INFORMATION_SOURCE")
    private boolean equalityInformationFormInformationSource;

    @OnPage(9)
    @JsonProperty("OTHER_INFORMATION_SOURCE")
    private boolean otherInformationSource;

    @RequiredOnPage(value = 9, message = "Enter the other information source details", onlyIfField = "otherInformationSource")
    @JsonProperty("OTHER_INFORMATION_DETAILS")
    private String otherInformationDetails;

    // Page 10

    // Intentionally Blank


    // Page 11

    @RequiredOnPage(value = 11, message = "Enter the report author")
    @JsonProperty("REPORT_AUTHOR")
    private String reportAuthor;

    @RequiredOnPage(value = 11, message = "Enter the office")
    @JsonProperty("_OFFICE_")
    private String office;

    @OnPage(11)
    @JsonProperty("COURT_OFFICE_PHONE_NUMBER")
    private String courtOfficePhoneNumber;

    @OnPage(11)
    @JsonProperty("COUNTER_SIGNATURE")
    private String counterSignature;

    @JsonProperty("REPORT_DATE")
    public String getReportDate() {
        return formattedDateFromDatePartsDefaultToday("reportDate");
    }

    @RequiredDateOnPage(value = 11,
            message = "Enter the report completion date",
            incompleteMessage = "Enter the report completion date and include a day, month and year",
            invalidMessage = "Enter a real report completion date")
    private String reportDate;
    private String reportDate_day;
    private String reportDate_month;
    private String reportDate_year;

    @JsonProperty("ADDRESS_LINES")
    public List<String> addressLines() {
        return Optional.ofNullable(address)
                .map(addressLines ->
                        ImmutableList.copyOf(addressLines.split("\n"))
                                .stream()
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toList()))
                .orElse(ImmutableList.of());
    }

    @Override
    public List<ValidationError> validate() {

        if (!Strings.isNullOrEmpty(dateOfHearing) && Strings.isNullOrEmpty(dateOfHearing_day) && Strings.isNullOrEmpty(dateOfHearing_month) && Strings.isNullOrEmpty(dateOfHearing_year)) {
            String[] dateOfHeartingSplit = dateOfHearing.split("/");
            dateOfHearing_day = dateOfHeartingSplit[0];
            dateOfHearing_month = dateOfHeartingSplit[1];
            dateOfHearing_year = dateOfHeartingSplit[2];
        }

        if (!Strings.isNullOrEmpty(reportDate) && Strings.isNullOrEmpty(reportDate_day) && Strings.isNullOrEmpty(reportDate_month) && Strings.isNullOrEmpty(reportDate_year)) {
            String[] reportDateSplit = reportDate.split("/");
            reportDate_day = reportDateSplit[0];
            reportDate_month = reportDateSplit[1];
            reportDate_year = reportDateSplit[2];
        }

        return super.validate();
    }
}
