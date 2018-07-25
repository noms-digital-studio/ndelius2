package views.pages.paroleparom1report;

public enum Page {
    PRISONER_DETAILS("2", "Prisoner details"),
    INTERVENTION("8", "Interventions"),
    CURRENT_ROSH_CUSTODY("13", "Current ROSH: custody")
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

    public String getPageHeader() {
        return pageHeader;
    }
}
