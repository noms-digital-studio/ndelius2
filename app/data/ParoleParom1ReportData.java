package data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import data.annotations.DateOnPage;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredDateOnPage;
import data.annotations.RequiredGroupOnPage;
import data.annotations.RequiredOnPage;
import data.base.ReportGeneratorWizardData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import play.data.validation.ValidationError;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = false)
public class ParoleParom1ReportData extends ReportGeneratorWizardData {

    // Page 2

    @Encrypted
    @RequiredOnPage(2)
    @JsonProperty("GENDER")
    private String gender;

    @RequiredOnPage(value = 2, message = "Enter the prison or Young Offender Institution")
    @JsonProperty("PRISONER_DETAILS_PRISON_INSTITUTION")
    private String prisonerDetailsPrisonInstitution;

    @RequiredOnPage(value = 2, message = "Enter the prisoner's full name")
    @JsonProperty("PRISONER_DETAILS_PRISONERS_FULL_NAME")
    private String prisonerDetailsPrisonersFullName;

    @RequiredOnPage(value = 2, message = "Enter the prison number")
    @JsonProperty("PRISONER_DETAILS_PRISON_NUMBER")
    private String prisonerDetailsPrisonNumber;

    @RequiredOnPage(value = 2, message = "Enter the NOMIS number")
    @JsonProperty("PRISONER_DETAILS_NOMIS_NUMBER")
    private String prisonerDetailsNomisNumber;

    @RequiredOnPage(value = 2, message = "Select the current prison category")
    @JsonProperty("PRISONER_DETAILS_PRISONERS_CATEGORY")
    private String prisonerDetailsPrisonersCategory;

    @RequiredOnPage(value = 2, message = "Enter the offence")
    @JsonProperty("PRISONER_DETAILS_OFFENCE")
    private String prisonerDetailsOffence;

    @RequiredOnPage(value = 2, message = "Enter the sentence")
    @JsonProperty("PRISONER_DETAILS_SENTENCE")
    private String prisonerDetailsSentence;

    @RequiredOnPage(value = 2, message = "Select the sentence type")
    @JsonProperty("PRISONER_DETAILS_SENTENCE_TYPE")
    private String prisonerDetailsSentenceType;

    @RequiredOnPage(value = 2, message = "Enter the tariff length",
            onlyIfField = "prisonerDetailsSentenceType",
            onlyIfFieldMatchValue = "indeterminate")
    @JsonProperty("PRISONER_DETAILS_TARIFF_LENGTH")
    private String prisonerDetailsTariffLength;

    @JsonProperty("PRISONER_DETAILS_TARIFF_EXPIRY_DATE")
    public String getPrisonerDetailsTariffExpiryDate() {
        return formattedDateFromDateParts("prisonerDetailsTariffExpiryDate");
    }

    @RequiredDateOnPage(value = 2, message = "Enter the tariff expiry date",
            incompleteMessage = "Enter the tariff expiry date and include a day, month and year",
            invalidMessage = "Enter a real tariff expiry date",
            onlyIfField = "prisonerDetailsSentenceType",
            onlyIfFieldMatchValue = "indeterminate",
            minDate = "TODAY",
            outOfRangeMessage = "The tariff expiry date must be in the future")
    private String prisonerDetailsTariffExpiryDate;
    private String prisonerDetailsTariffExpiryDate_day;
    private String prisonerDetailsTariffExpiryDate_month;
    private String prisonerDetailsTariffExpiryDate_year;

    @JsonProperty("PRISONER_DETAILS_PAROLE_ELIGIBILITY_DATE")
    public String getPrisonerDetailsParoleEligibilityDate() {
        return formattedDateFromDateParts("prisonerDetailsParoleEligibilityDate");
    }

    @DateOnPage(value = 2, incompleteMessage = "Enter the parole eligibility date and include a day, month and year",
            invalidMessage = "Enter a real parole eligibility date",
            onlyIfField = "prisonerDetailsSentenceType",
            onlyIfFieldMatchValue = "determinate",
            minDate = "TODAY",
            outOfRangeMessage = "The parole eligibility date must be in the future")
    private String prisonerDetailsParoleEligibilityDate;
    private String prisonerDetailsParoleEligibilityDate_day;
    private String prisonerDetailsParoleEligibilityDate_month;
    private String prisonerDetailsParoleEligibilityDate_year;

    @JsonProperty("PRISONER_DETAILS_AUTO_RELEASE_DATE")
    public String getPrisonerDetailsAutoReleaseDate() {
        return formattedDateFromDateParts("prisonerDetailsAutoReleaseDate");
    }

    @DateOnPage(value = 2, incompleteMessage = "Enter the automatic release date and include a day, month and year",
            invalidMessage = "Enter a real automatic release date",
            onlyIfField = "prisonerDetailsSentenceType",
            onlyIfFieldMatchValue = "determinate",
            minDate = "TODAY",
            outOfRangeMessage = "The automatic release date/non parole eligibility date must be in the future")
    private String prisonerDetailsAutoReleaseDate;
    private String prisonerDetailsAutoReleaseDate_day;
    private String prisonerDetailsAutoReleaseDate_month;
    private String prisonerDetailsAutoReleaseDate_year;

    // Page 3

    @RequiredOnPage(value = 3, message = "Enter how long you have managed the prisoner, and what contact you have had with them")
    @JsonProperty("PRISONER_CONTACT_DETAIL")
    private String prisonerContactDetail;

    @RequiredOnPage(value = 3, message = "Enter what contact you have had with the prisoner's family, partners or significant others")
    @JsonProperty("PRISONER_CONTACT_FAMILY_DETAIL")
    private String prisonerContactFamilyDetail;

    @RequiredOnPage(value = 3, message = "Enter what contact you have had with other relevant agencies about the prisoner")
    @JsonProperty("PRISONER_CONTACT_AGENCIES_DETAIL")
    private String prisonerContactAgenciesDetail;

    // Page 4 - RoSH at point of sentence

    @RequiredOnPage(value = 4, message = "Specify if a RoSH assessment was completed at the point of sentence")
    @JsonProperty("ROSH_AT_POS_ASSESSMENT_COMPLETED")
    private String roshAtPosAssessmentCompleted;

    @JsonProperty("ROSH_AT_POS_DATE")
    public String getRoshAtPosDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
            val date = dateFormat.parse(formattedDateFromDateParts("roshAtPosDate"));
            SimpleDateFormat dateNoDayFormat = new SimpleDateFormat("MMM yyyy");
            return dateNoDayFormat.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    @RequiredDateOnPage(value = 4, message = "Enter the date when the RoSH assessment was completed",
            incompleteMessage = "Enter the date when the RoSH assessment was completed",
            invalidMessage = "Enter a real date when the RoSH assessment was completed",
            onlyIfField = "roshAtPosAssessmentCompleted",
            onlyIfFieldMatchValue = "yes")
    private String roshAtPosDate;
    private String roshAtPosDate_day;
    private String roshAtPosDate_month;
    private String roshAtPosDate_year;

    @RequiredOnPage(value = 4, message = "Select the risk to the public", onlyIfField = "roshAtPosAssessmentCompleted", onlyIfFieldMatchValue = "yes")
    @JsonProperty("ROSH_AT_POS_PUBLIC")
    private String roshAtPosPublic;

    @RequiredOnPage(value = 4, message = "Select the risk to any known adult", onlyIfField = "roshAtPosAssessmentCompleted", onlyIfFieldMatchValue = "yes")
    @JsonProperty("ROSH_AT_POS_KNOWN_ADULT")
    private String roshAtPosKnownAdult;

    @RequiredOnPage(value = 4, message = "Select the risk to children", onlyIfField = "roshAtPosAssessmentCompleted", onlyIfFieldMatchValue = "yes")
    @JsonProperty("ROSH_AT_POS_CHILDREN")
    private String roshAtPosChildren;

    @RequiredOnPage(value = 4, message = "Select the risk to prisoners", onlyIfField = "roshAtPosAssessmentCompleted", onlyIfFieldMatchValue = "yes")
    @JsonProperty("ROSH_AT_POS_PRISONERS")
    private String roshAtPosPrisoners;

    @RequiredOnPage(value = 4, message = "Select the risk to staff", onlyIfField = "roshAtPosAssessmentCompleted", onlyIfFieldMatchValue = "yes")
    @JsonProperty("ROSH_AT_POS_STAFF")
    private String roshAtPosStaff;

    @RequiredOnPage(value = 4, message = "Enter the prisoner's attitude to the index offence")
    @JsonProperty("ROSH_AT_POS_ATTITUDE_INDEX_OFFENCE")
    private String roshAtPosAttitudeIndexOffence;

    @RequiredOnPage(value = 4, message = "Enter the prisoner's attitude to their previous offending")
    @JsonProperty("ROSH_AT_POS_ATTITUDE_PREVIOUS_OFFENDING")
    private String roshAtPosAttitudePreviousOffending;

    // Page 5 - Victims

    @RequiredOnPage(value = 5, message = "Enter your analysis of the impact of the offence on the victims")
    @JsonProperty("VICTIMS_IMPACT_DETAILS")
    private String victimsImpactDetails;

    @JsonProperty("VICTIMS_VLO_CONTACT_DATE")
    public String getVictimsVLOContactDate() {
        return formattedDateFromDateParts("victimsVLOContactDate");
    }

    @RequiredDateOnPage(value = 5, message = "Enter the date you contacted the VLO",
            incompleteMessage = "Enter the date you contacted the VLO and include a day, month and year",
            invalidMessage = "Enter a real date you contacted the VLO",
            minDate = "LAST_YEAR",
            maxDate = "TODAY",
            outOfRangeMessage = "The VLO date must be within the last year")
    private String victimsVLOContactDate;
    private String victimsVLOContactDate_day;
    private String victimsVLOContactDate_month;
    private String victimsVLOContactDate_year;

    @RequiredOnPage(value = 5, message = "Specify if the victims are engaged with the VCS")
    @JsonProperty("VICTIMS_ENGAGED_IN_VCS")
    private String victimsEngagedInVCS;

    @RequiredOnPage(value = 5, message = "Specify if the victims wish to submit a VPS")
    @JsonProperty("VICTIMS_SUBMIT_VPS")
    private String victimsSubmitVPS;


    @RequiredOnPage(value = 6, message = "Specify if the prisoner has met OPD screening criteria and been considered for OPD pathway services")
    @JsonProperty("CONSIDERED_FOR_OPD_PATHWAY_SERVICES")
    private String consideredForOPDPathwayServices;

    // Page 7 - Behaviour in prison
    @RequiredOnPage(value = 7, message = "Enter details of the prisoner's behaviour in prison")
    @JsonProperty("BEHAVIOUR_DETAIL")
    private String behaviourDetail;

    @RequiredOnPage(value = 7, message = "Enter the RoTL summary")
    @JsonProperty("ROTL_SUMMARY")
    private String rotlSummary;

    @RequiredOnPage(value = 8, message = "Detail the interventions the prisoner has completed")
    @JsonProperty("INTERVENTIONS_DETAIL")
    private String interventionsDetail;

    @RequiredOnPage(value = 8, message = "Enter the interventions summary")
    @JsonProperty("INTERVENTIONS_SUMMARY")
    private String interventionsSummary;

    // Page 9 - Current sentence plan and response
    @RequiredOnPage(value = 9, message = "Enter the prisoner's current sentence plan objectives, including their response")
    @JsonProperty("SENTENCE_PLAN")
    private String sentencePlan;

    // Page 10 - MAPPA
    @RequiredOnPage(value = 10, message = "Specify if the prisoner is eligible for MAPPA")
    @JsonProperty("ELIGIBLE_FOR_MAPPA")
    private String eligibleForMappa;

    @JsonProperty("MAPPA_SCREENED_DATE")
    public String getMappaScreenedDate() {
        return formattedDateFromDateParts("mappaScreenedDate");
    }

    @RequiredDateOnPage(value = 10,
            message = "Enter the date when the prisoner was screened for MAPPA",
            incompleteMessage = "Enter the date when the prisoner was screened for MAPPA and include a day, month and year",
            invalidMessage = "Enter a real date when the prisoner was screened for MAPPA",
            onlyIfField = "eligibleForMappa",
            onlyIfFieldMatchValue = "yes",
            maxDate = "TODAY",
            outOfRangeMessage = "The date when the prisoner was screened for MAPPA must be in the past")
    private String mappaScreenedDate;
    private String mappaScreenedDate_day;
    private String mappaScreenedDate_month;
    private String mappaScreenedDate_year;

    @RequiredOnPage(value = 10, message = "Select the prisoner's current MAPPA category", onlyIfField = "eligibleForMappa", onlyIfFieldMatchValue = "yes")
    @JsonProperty("MAPPA_CATEGORY")
    private String mappaCategory;

    @RequiredOnPage(value = 10, message = "Select the prisoner's current MAPPA level", onlyIfField = "eligibleForMappa", onlyIfFieldMatchValue = "yes")
    @JsonProperty("MAPPA_LEVEL")
    private String mappaLevel;

    // Page 11 - Current risk assessment scores

    @RequiredOnPage(value = 11, message = "Enter the RSR score")
    @JsonProperty("RISK_ASSESSMENT_RSR_SCORE")
    private String riskAssessmentRSRScore;

    @JsonProperty("RISK_ASSESSMENT_RSR_SCORE_AS_LEVEL")
    public String getRiskAssessmentRSRScoreLevel() {
        return asBigDecimal(riskAssessmentRSRScore).map(value -> {
            if (value.compareTo(BigDecimal.valueOf(3)) < 0) {
                return "low";
            }
            if (value.compareTo(BigDecimal.valueOf(7)) < 0) {
                return "medium";
            }
            return "high";
        }).orElse("");
    }

    @RequiredOnPage(value = 11, message = "Enter the OGRS3 score")
    @JsonProperty("RISK_ASSESSMENT_OGRS3_SCORE")
    private String riskAssessmentOGRS3ReoffendingProbability;

    @JsonProperty("RISK_ASSESSMENT_OGRS3_SCORE_AS_LEVEL")
    public String getRiskAssessmentOGRS3ReoffendingProbabilityLevel() {
        return asInteger(riskAssessmentOGRS3ReoffendingProbability).map(value -> {
            if (value < 50) {
                return "low";
            }
            if (value < 75) {
                return "medium";
            }
            if (value < 90) {
                return "high";
            }
            return "very_high";
        }).orElse("");
    }

    @RequiredOnPage(value = 11, message = "Enter the OGP score")
    @JsonProperty("RISK_ASSESSMENT_OGP_SCORE")
    private String riskAssessmentOGPReoffendingProbability;

    @JsonProperty("RISK_ASSESSMENT_OGP_SCORE_AS_LEVEL")
    public String getRiskAssessmentOGPReoffendingProbabilityLevel() {
        return asInteger(riskAssessmentOGPReoffendingProbability).map(value -> {
            if (value < 34) {
                return "low";
            }
            if (value < 67) {
                return "medium";
            }
            if (value < 85) {
                return "high";
            }
            return "very_high";
        }).orElse("");
    }

    @RequiredOnPage(value = 11, message = "Enter the OVP score")
    @JsonProperty("RISK_ASSESSMENT_OVP_SCORE")
    private String riskAssessmentOVPReoffendingProbability;

    @JsonProperty("RISK_ASSESSMENT_OVP_SCORE_AS_LEVEL")
    public String getRiskAssessmentOVPReoffendingProbabilityLevel() {
        return asInteger(riskAssessmentOVPReoffendingProbability).map(value -> {
            if (value < 30) {
                return "low";
            }
            if (value < 60) {
                return "medium";
            }
            if (value < 80) {
                return "high";
            }
            return "very_high";
        }).orElse("");
    }

    @RequiredOnPage(value = 11, message = "Specify if a Risk Matrix 2000 has been completed")
    @JsonProperty("RISK_ASSESSMENT_MATRIX2000_COMPLETED")
    private String riskAssessmentMatrix2000AssessmentCompleted;

    @RequiredOnPage(value = 11, message = "Select the Risk Matrix 2000 score", onlyIfField = "riskAssessmentMatrix2000AssessmentCompleted", onlyIfFieldMatchValue = "yes")
    @JsonProperty("RISK_ASSESSMENT_MATRIX2000_SCORE")
    private String riskAssessmentMatrix2000Score;

    @RequiredOnPage(value = 11, message = "Specify if a SARA has been completed")
    @JsonProperty("RISK_ASSESSMENT_SARA_COMPLETED")
    private String riskAssessmentSpousalAssaultAssessmentCompleted;

    @RequiredOnPage(value = 11, message = "Select the SARA score", onlyIfField = "riskAssessmentSpousalAssaultAssessmentCompleted", onlyIfFieldMatchValue = "yes")
    @JsonProperty("RISK_ASSESSMENT_SARA_SCORE")
    private String riskAssessmentSpousalAssaultScore;

    // Page 12 - Current RoSH: community
    @RequiredOnPage(value = 12, message = "Select the risk to the public")
    @JsonProperty("ROSH_COMMUNITY_PUBLIC")
    private String roshCommunityPublic;

    @RequiredOnPage(value = 12, message = "Select the risk to any known adult")
    @JsonProperty("ROSH_COMMUNITY_KNOWN_ADULT")
    private String roshCommunityKnownAdult;

    @RequiredOnPage(value = 12, message = "Select the risk to children")
    @JsonProperty("ROSH_COMMUNITY_CHILDREN")
    private String roshCommunityChildren;

    @RequiredOnPage(value = 12, message = "Select the risk to prisoners")
    @JsonProperty("ROSH_COMMUNITY_PRISONERS")
    private String roshCommunityPrisoners;

    @RequiredOnPage(value = 12, message = "Select the risk to staff")
    @JsonProperty("ROSH_COMMUNITY_STAFF")
    private String roshCommunityStaff;

    // Page 13 - Current RoSH: community
    @RequiredOnPage(value = 13, message = "Select the risk to the public")
    @JsonProperty("ROSH_CUSTODY_PUBLIC")
    private String roshCustodyPublic;

    @RequiredOnPage(value = 13, message = "Select the risk to any known adult")
    @JsonProperty("ROSH_CUSTODY_KNOWN_ADULT")
    private String roshCustodyKnownAdult;

    @RequiredOnPage(value = 13, message = "Select the risk to children")
    @JsonProperty("ROSH_CUSTODY_CHILDREN")
    private String roshCustodyChildren;

    @RequiredOnPage(value = 13, message = "Select the risk to prisoners")
    @JsonProperty("ROSH_CUSTODY_PRISONERS")
    private String roshCustodyPrisoners;

    @RequiredOnPage(value = 13, message = "Select the risk to staff")
    @JsonProperty("ROSH_CUSTODY_STAFF")
    private String roshCustodyStaff;

    // Page 14 - Risk to the prisoner
    @RequiredOnPage(value = 14, message = "Specify if the prisoner poses a risk of self harm in the community")
    @JsonProperty("SELF_HARM_COMMUNITY")
    private String selfHarmCommunity;

    @RequiredOnPage(value = 14, message = "Specify if the prisoner poses a risk of self harm in custody")
    @JsonProperty("SELF_HARM_CUSTODY")
    private String selfHarmCustody;

    @RequiredOnPage(value = 14, message = "Specify if the prisoner is at risk of serious harm from others in the community")
    @JsonProperty("OTHERS_HARM_COMMUNITY")
    private String othersHarmCommunity;

    @RequiredOnPage(value = 14, message = "Specify if the prisoner is at risk of serious harm from others in custody")
    @JsonProperty("OTHERS_HARM_CUSTODY")
    private String othersHarmCustody;

    // Page 15 - RoSH analysis
    @RequiredOnPage(value = 15, message = "Enter the nature of the risk of serious harm")
    @JsonProperty("NATURE_OF_RISK")
    private String natureOfRisk;

    @RequiredOnPage(value = 15, message = "Enter the factors that might increase the risk of serious harm")
    @JsonProperty("INCREASE_FACTORS")
    private String increaseFactors;

    @RequiredOnPage(value = 15, message = "Enter the factors that might decrease the risk of serious harm")
    @JsonProperty("DECREASE_FACTORS")
    private String decreaseFactors;

    @RequiredOnPage(value = 15, message = "Enter your analysis of the likelihood of further offending")
    @JsonProperty("LIKELIHOOD_FURTHER_OFFENDING")
    private String likelihoodFurtherOffending;

    @RequiredOnPage(value = 15, message = "Specify if the prisoner poses a risk of absconding")
    @JsonProperty("RISK_OF_ABSCONDING")
    private String riskOfAbsconding;

    @RequiredOnPage(value = 15, message = "Enter the details of the absconding risk", onlyIfField = "riskOfAbsconding", onlyIfFieldMatchValue = "yes")
    @JsonProperty("RISK_OF_ABSCONDING_DETAILS")
    private String riskOfAbscondingDetails;

    // Page 16 - Risk Management Plan (RMP)
    @RequiredOnPage(value = 16, message = "Specify if the prisoner requires a community RMP")
    @JsonProperty("RISK_MANAGEMENT_PLAN_REQUIRED")
    private String riskManagementPlanRequired;

    @RequiredOnPage(value = 16, message = "Enter the current situation (Agencies)", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("AGENCIES")
    private String agencies;

    @RequiredOnPage(value = 16, message = "Enter the support", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("SUPPORT")
    private String support;

    @RequiredOnPage(value = 16, message = "Enter the control", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("CONTROL")
    private String control;

    @RequiredOnPage(value = 16, message = "Enter the added measures for specific risks", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("RISK_MEASURES")
    private String riskMeasures;

    @RequiredOnPage(value = 16, message = "Enter the agency actions", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("AGENCY_ACTIONS")
    private String agencyActions;

    @RequiredOnPage(value = 16, message = "Enter the additional conditions or requirements", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("ADDITIONAL_CONDITIONS")
    private String additionalConditions;

    @RequiredOnPage(value = 16, message = "Enter the level of contact", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("LEVEL_OF_CONTACT")
    private String levelOfContact;

    @RequiredOnPage(value = 16, message = "Enter the contingency plan", onlyIfField = "riskManagementPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("CONTINGENCY_PLAN")
    private String contingencyPlan;

    // Page 17 - Resettlement plan for release
    @RequiredOnPage(value = 17, message = "Specify if the prisoner requires a resettlement plan for release")
    @JsonProperty("RESETTLEMENT_PLAN")
    private String resettlementPlan;

    @RequiredOnPage(value = 17, message = "Enter the resettlement plan for release", onlyIfField = "resettlementPlan", onlyIfFieldMatchValue = "yes")
    @JsonProperty("RESETTLEMENT_PLAN_DETAIL")
    private String resettlementPlanDetail;

    // Page 18 - Supervision plan for release
    @RequiredOnPage(value = 18, message = "Specify if the prisoner requires a supervision plan for release")
    @JsonProperty("SUPERVISION_PLAN_REQUIRED")
    private String supervisionPlanRequired;

    @RequiredOnPage(value = 18, message = "Enter the supervision plan for release", onlyIfField = "supervisionPlanRequired", onlyIfFieldMatchValue = "yes")
    @JsonProperty("SUPERVISION_PLAN_DETAIL")
    private String supervisionPlanDetail;

    // Page 19 - Recommendation
    @RequiredOnPage(value = 19, message = "Enter your recommendation")
    @JsonProperty("RECOMMENDATION")
    private String recommendation;

    // Page 20 - Oral hearing
    @RequiredOnPage(value = 20, message = "Enter the oral hearing considerations")
    @JsonProperty("ORAL_HEARING")
    private String oralHearing;

    // Page 21 - Sources
    @RequiredGroupOnPage(value = 21, message = "Select the case documents you have used")
    @JsonProperty("SOURCES_PREVIOUS_CONVICTIONS")
    private boolean sourcesPreviousConvictions;

    @RequiredGroupOnPage(value = 21, errorWhenInvalid = false)
    @JsonProperty("SOURCES_CPS_DOCUMENTS")
    private boolean sourcesCPSDocuments;

    @RequiredGroupOnPage(value = 21, errorWhenInvalid = false)
    @JsonProperty("SOURCES_JUDGES_COMMENTS")
    private boolean sourcesJudgesComments;

    @RequiredGroupOnPage(value = 21, errorWhenInvalid = false)
    @JsonProperty("SOURCES_PAROLE_DOSSIER")
    private boolean sourcesParoleDossier;

    @RequiredGroupOnPage(value = 21, errorWhenInvalid = false)
    @JsonProperty("SOURCES_PREVIOUS_PAROLE_REPORTS")
    private boolean sourcesPreviousParoleReports;

    @RequiredGroupOnPage(value = 21, errorWhenInvalid = false)
    @JsonProperty("SOURCES_PRE_SENTENCE_REPORT")
    private boolean sourcesPreSentenceReport;

    @RequiredGroupOnPage(value = 21, errorWhenInvalid = false)
    @JsonProperty("SOURCES_PROBATION_CASE_RECORD")
    private boolean sourcesProbationCaseRecord;

    @RequiredGroupOnPage(value = 21, errorWhenInvalid = false)
    @JsonProperty("SOURCES_OTHER")
    private boolean sourcesOther;

    @RequiredOnPage(value = 21, onlyIfField = "sourcesOther", message = "Enter the other case documents you have used")
    @JsonProperty("SOURCES_OTHER_DETAIL")
    private String sourcesOtherDetail;

    @RequiredOnPage(value = 21, message = "Enter the reports, assessments and directions you have used")
    @JsonProperty("SOURCES_ASSESSMENT_LIST")
    private String sourcesAssessmentList;

    @RequiredOnPage(value = 21, message = "Specify if there have been any omissions or limitations")
    @JsonProperty("SOURCES_LIMITATIONS")
    private String sourceLimitations;

    @RequiredOnPage(value = 21, message = "Enter the explanation", onlyIfField = "sourceLimitations", onlyIfFieldMatchValue = "yes")
    @JsonProperty("SOURCES_LIMITATIONS_DETAIL")
    private String sourceLimitationsDetail;

    // Page 23
    @RequiredOnPage(value = 23, message = "Enter the report author")
    @JsonProperty("SIGNATURE_NAME")
    private String signatureName;

    @RequiredOnPage(value = 23, message = "Enter the NPS division and LDU")
    @JsonProperty("SIGNATURE_DIVISION")
    private String signatureDivision;

    @RequiredOnPage(value = 23, message = "Enter the office address")
    @JsonProperty("SIGNATURE_OFFICE_ADDRESS")
    private String signatureOfficeAddress;

    @RequiredOnPage(value = 23, message = "Enter the email address")
    @JsonProperty("SIGNATURE_EMAIL")
    private String signatureEmail;

    @RequiredOnPage(value = 23, message = "Enter the telephone number and extension")
    @JsonProperty("SIGNATURE_TELEPHONE")
    private String signatureTelephone;

    @OnPage(value = 23)
    @JsonProperty("SIGNATURE_COUNTER_NAME")
    private String signatureCounterName;

    @OnPage(value = 23)
    @JsonProperty("SIGNATURE_COUNTER_ROLE")
    private String signatureCounterRole;

    @JsonProperty("SIGNATURE_DATE")
    public String getSignatureDate() {
        return formattedDateFromDateParts("signatureDate");
    }

    @RequiredDateOnPage(value = 23, message = "Enter the completion date", incompleteMessage = "Enter the completion date and include a day, month and year", invalidMessage = "Enter a real completion date")
    private String signatureDate;
    private String signatureDate_day;
    private String signatureDate_month;
    private String signatureDate_year;

    private static Optional<Integer> asInteger(String value) {
        try {
            return Optional.ofNullable(value).filter(StringUtils::isNumeric).map(Integer::valueOf);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<BigDecimal> asBigDecimal(String value) {
        try {
            return Optional.ofNullable(value).filter(StringUtils::isNotBlank).map(NumberUtils::createBigDecimal);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected List<Function<Map<String, Object>, Stream<ValidationError>>> reportSpecificValidators() {
        return ImmutableList.of(
                this::notWithinRangeRequiredDateErrors,
                this::notWithinRangeDateErrors
        );
    }

    private Stream<ValidationError> notWithinRangeRequiredDateErrors(Map<String, Object> options) {

        return requiredDateFields().
                filter(this::requiredDateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && !composedDateBitsAreInvalid(field) && suppliedDateNotWithinRange(field, field.getAnnotation(RequiredDateOnPage.class).minDate(), field.getAnnotation(RequiredDateOnPage.class).maxDate())).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(RequiredDateOnPage.class).outOfRangeMessage()));

    }

    private Stream<ValidationError> notWithinRangeDateErrors(Map<String, Object> options) {

        return dateFields().
                filter(this::dateFieldEnforced).
                filter(field -> mustValidateField(options, field)).
                filter(field -> allDateFieldsAreSupplied(field) && !composedDateBitsAreInvalid(field) && suppliedDateNotWithinRange(field, field.getAnnotation(DateOnPage.class).minDate(), field.getAnnotation(DateOnPage.class).maxDate())).
                map(field -> new ValidationError(field.getName(), field.getAnnotation(DateOnPage.class).outOfRangeMessage()));

    }

    private boolean suppliedDateNotWithinRange(Field field, String minDate, String maxDate) {

        boolean hasError;
        boolean minDatePresent = !minDate.isEmpty();
        boolean maxDatePresent = !maxDate.isEmpty();

        if (!minDatePresent && !maxDatePresent) {
            return false;
        }

        try {
            String formattedDate = dateFieldValues(field).collect(Collectors.joining("/"));
            SimpleDateFormat dateFormat = getValidatorDateFormat();

            Date parsedDate = dateFormat.parse(formattedDate);
            Date fromDate = minDatePresent ? getRequiredDate(minDate) : null;
            Date toDate = maxDatePresent ? getRequiredDate(maxDate) : null;

            if (minDatePresent && maxDatePresent) {
                hasError = !(parsedDate.compareTo(fromDate) >= 0 && parsedDate.compareTo(toDate) <= 0);
            } else if (minDatePresent) {
                hasError = !(parsedDate.compareTo(fromDate) >= 0);
            } else {
                hasError = !(parsedDate.compareTo(toDate) <= 0);
            }
        } catch (ParseException e) {
            return true;
        }

        return hasError;
    }

    private Date getRequiredDate(String date) {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();

        if (date.equals("LAST_YEAR")) {
            cal.add(Calendar.YEAR, -1);
            cal.add(Calendar.DATE, -1);
            return cal.getTime();
        }

        return today;
    }

}
