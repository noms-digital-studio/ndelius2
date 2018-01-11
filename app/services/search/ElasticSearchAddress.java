package services.search;

import data.offendersearch.Address;
import lombok.Data;

@Data
public class ElasticSearchAddress {
    private String addressNumber;
    private String buildingName;
    private String streetName;
    private String town;
    private String county;
    private String postcode;

    public Address toAddress() {
        return Address.builder()
            .addressNumber(addressNumber)
            .buildingName(buildingName)
            .streetName(streetName)
            .town(town)
            .county(county)
            .postcode(postcode)
            .build();
    }
}
