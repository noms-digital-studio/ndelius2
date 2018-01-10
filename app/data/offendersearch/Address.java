package data.offendersearch;

import lombok.Data;

@Data
public class Address {
    private String addressNumber;
    private String buildingName;
    private String streetName;
    private String town;
    private String county;
    private String postcode;
}
