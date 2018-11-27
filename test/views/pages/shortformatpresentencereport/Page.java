package views.pages.shortformatpresentencereport;

public enum Page {
    OFFENDER_DETAILS("2", "Offender details"),
    SENTENCING_COURT_DETAILS("3", "Sentencing court details"),
    OFFENCE_DETAILS("4", "Offence details"),
    OFFENCE_ANALYSIS("5", "Offence analysis"),
    OFFENDER_ASSESSMENT("6", "Offender assessment"),
    RISK_ASSESSMENT("7", "Risk assessment"),
    PROPOSAL("8", "Proposal"),
    SOURCES_OF_INFORMATION("9", "Sources of information"),
    CHECK_YOUR_REPORT("10", "Check your report"),
    SIGNATURE("11", "Sign your report");

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
