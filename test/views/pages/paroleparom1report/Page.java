package views.pages.paroleparom1report;

public enum Page {
    PRISONER_DETAILS("2", "Prisoner details"),
    PRISONER_CONTACT("3", "Prisoner contact"),
    VICTIMS("5", "OPD pathway"),
    OPD_PATHWAY("6", "OPD pathway"),
    BEHAVIOUR_IN_PRISON("7", "Behaviour in prison"),
    INTERVENTION("8", "Interventions"),
    SENTENCE_PLAN("9", "Current sentence plan and response"),
    CURRENT_ROSH_CUSTODY("13", "Current ROSH: custody"),
    RISK_TO_PRISONER("14", "Risk to the prisoner"),
    ROSH_ANALYSIS("15", "RoSH analysis"),
    RISK_MANAGEMENT_PLAN("16", "Risk Management Plan (RMP)"),
    RESETTLEMENT_PLAN("17", "Resettlement plan for release"),
    SUPERVISION_PLAN("18", "Supervision plan for release"),
    RECOMMENDATION("19", "Recommendation"),
    ORAL_HEARING("20", "Oral hearing"),
    SOURCES("21", "Sources")
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
