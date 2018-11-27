package views.pages.paroleparom1report;

public enum Page {
    PRISONER_DETAILS("2", "Prisoner details"),
    PRISONER_CONTACT("3", "Prisoner contact"),
    ROSH_AT_POINT_OF_SENTENCE("4", "RoSH at point of sentence"),
    VICTIMS("5", "Victims"),
    OPD_PATHWAY("6", "OPD pathway"),
    BEHAVIOUR_IN_PRISON("7", "Behaviour in prison"),
    INTERVENTION("8", "Interventions"),
    SENTENCE_PLAN("9", "Prison sentence plan and response"),
    MAPPA("10", "Multi Agency Public Protection Arrangements (MAPPA)"),
    CURRENT_RISK_ASSESSMENT_SCORES("11", "Current risk assessment scores"),
    CURRENT_ROSH_COMMUNITY("12", "Current RoSH: community"),
    CURRENT_ROSH_CUSTODY("13", "Current RoSH: custody"),
    RISK_TO_PRISONER("14", "Risk to the prisoner"),
    ROSH_ANALYSIS("15", "RoSH analysis"),
    RISK_MANAGEMENT_PLAN("16", "Risk Management Plan (RMP)"),
    RESETTLEMENT_PLAN("17", "Resettlement plan for release"),
    SUPERVISION_PLAN("18", "Supervision plan for release"),
    RECOMMENDATION("19", "Recommendation"),
    ORAL_HEARING("20", "Oral hearing"),
    SOURCES("21", "Sources"),
    CHECK_YOUR_REPORT("22", "Check your report"),
    SIGNATURE("23", "Signature & date")
    ;
    private final String pageNumber;
    private final String pageHeader;

    Page(String pageNumber, String pageHeader) {
        this.pageNumber = pageNumber;
        this.pageHeader = pageHeader;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public String getPageHeader() { return pageHeader; }
}
