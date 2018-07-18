package bdd.paroleparom1report;

public enum PageHeadings {
    PRISONER_DETAILS("2", "Prisoner details"),
    CURRENT_ROSH_CUSTODY("13", "Current ROSH: custody")
    ;
    private final String pageNumber;
    private final String pageHeader;

    PageHeadings(String pageNumber, String pageHeader) {
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
