package bdd;

public enum Ports {
    PDF(9101),
    ALFRESCO(9102),
    OFFENDER_API(9103),
    CUSTODY_API(9104);

    Ports(int port) {
        this.port = port;
    }
    private final int port;

    public int getPort() {
        return port;
    }
}
