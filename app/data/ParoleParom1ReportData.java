package data;

import com.fasterxml.jackson.annotation.JsonProperty;
import data.annotations.OnPage;
import data.annotations.RequiredDateOnPage;
import data.annotations.RequiredGroupOnPage;
import data.annotations.RequiredOnPage;
import data.base.ReportGeneratorWizardData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ParoleParom1ReportData extends ReportGeneratorWizardData {

    @RequiredOnPage(value = 3, message = "Enter how long you have managed the prisoner, and what contact you have had with them")
    @JsonProperty("PRISONER_CONTACT_DETAIL")
    private String prisonerContactDetail;

    @RequiredOnPage(value = 3, message = "Enter what contact you have had with the prisoner's family, partners or significant others")
    @JsonProperty("PRISONER_CONTACT_FAMILY_DETAIL")
    private String prisonerContactFamilyDetail;

    @RequiredOnPage(value = 3, message = "Enter what contact you have had with other relevant agencies about the prisoner")
    @JsonProperty("PRISONER_CONTACT_AGENCIES_DETAIL")
    private String prisonerContactAgenciesDetail;

    @RequiredOnPage(value = 5, message = "Enter your analysis of the impact of the offence on the victims")
    @JsonProperty("VICTIMS_IMPACT_DETAILS")
    private String victimsImpactDetails;

    @JsonProperty("VICTIMS_VLO_CONTACT_DATE")
    public String getVictimsVLOContactDate() {
        return formattedDateFromDateParts("victimsVLOContactDate");
    }

    @RequiredDateOnPage(value = 5, message = "Enter the date you contacted the VLO", incompleteMessage = "Enter the date you contacted the VLO and include a day, month and year", invalidMessage = "Enter a real date you contacted the VLO")
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

    @RequiredOnPage(value = 15, message = "Enter the details of the absconding risk", onlyIfField = "riskOfAbsconding", onlyIfFieldMatchValue= "yes")
    @JsonProperty("RISK_OF_ABSCONDING_DETAILS")
    private String riskOfAbscondingDetails;

    // Page 16 - Risk Management Plan (RMP)
    @RequiredOnPage(value = 16, message = "Enter the agencies")
    @JsonProperty("AGENCIES")
    private String agencies;

    @RequiredOnPage(value = 16, message = "Enter the support")
    @JsonProperty("SUPPORT")
    private String support;

    @RequiredOnPage(value = 16, message = "Enter the control")
    @JsonProperty("CONTROL")
    private String control;

    @RequiredOnPage(value = 16, message = "Enter the added measures for specific risks")
    @JsonProperty("RISK_MEASURES")
    private String riskMeasures;

    @RequiredOnPage(value = 16, message = "Enter the agency actions")
    @JsonProperty("AGENCY_ACTIONS")
    private String agencyActions;

    @RequiredOnPage(value = 16, message = "Enter the additional conditions or requirements")
    @JsonProperty("ADDITIONAL_CONDITIONS")
    private String additionalConditions;

    @RequiredOnPage(value = 16, message = "Enter the level of contact")
    @JsonProperty("LEVEL_OF_CONTACT")
    private String levelOfContact;

    @RequiredOnPage(value = 16, message = "Enter the contingency plan")
    @JsonProperty("CONTINGENCY_PLAN")
    private String contingencyPlan;

    // Page 17 - Resettlement plan for release
    @RequiredOnPage(value = 17, message = "Specify if the prisoner requires a resettlement plan for release")
    @JsonProperty("RESETTLEMENT_PLAN")
    private String resettlementPlan;

    @RequiredOnPage(value = 17, message = "Enter the resettlement plan for release", onlyIfField = "resettlementPlan", onlyIfFieldMatchValue= "yes")
    @JsonProperty("RESETTLEMENT_PLAN_DETAIL")
    private String resettlementPlanDetail;

    // Page 18 - Supervision plan for release
    @RequiredOnPage(value = 18, message = "Specify if the prisoner requires a supervision plan for release")
    @JsonProperty("SUPERVISION_PLAN_REQUIRED")
    private String supervisionPlanRequired;

    @RequiredOnPage(value = 18, message = "Enter the supervision plan for release", onlyIfField = "supervisionPlanRequired", onlyIfFieldMatchValue= "yes")
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

    @RequiredOnPage(value = 21, message = "Enter the explanation", onlyIfField = "sourceLimitations", onlyIfFieldMatchValue= "yes")
    @JsonProperty("SOURCES_LIMITATIONS_DETAIL")
    private String sourceLimitationsDetail;


    // Page 23
    @OnPage(23)
    private String dummy23;

}
