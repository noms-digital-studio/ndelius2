package utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import interfaces.OffenderApi.AddressStatus;
import interfaces.OffenderApi.ContactDetails;
import interfaces.OffenderApi.Offender;
import interfaces.OffenderApi.OffenderAddress;
import lombok.val;

import java.util.ArrayList;

public class OffenderHelper {
    public static Offender anOffenderWithNoContactDetails() {
        return aBasicOffender();
    }

    public static Offender anOffenderWithNoContactDetailsAndNoPnc() {
        return aBasicOffender().toBuilder().otherIds(ImmutableMap.of()).build();
    }

    public static Offender anOffenderWithEmptyContactDetails() {
        return aBasicOffender().toBuilder()
                .contactDetails(emptyContactDetails())
                .build();
    }

    public static Offender anOffenderWithEmptyAddressList() {
        return aBasicOffender().toBuilder()
                .contactDetails(contactDetailsWithEmptyAddressList())
                .build();
    }

    public static Offender anOffenderWithMultipleAddresses() {
        return aBasicOffender().toBuilder()
                .contactDetails(contactDetailsWithMultipleAddresses())
                .build();
    }

    public static Offender anOffenderWithNoMainAddress() {
        return aBasicOffender().toBuilder()
                .contactDetails(contactDetailsAddressesNonOfWhichHAveAMainStatus())
                .build();
    }

    public static ContactDetails emptyContactDetails() {
        return ContactDetails.builder().build();
    }

    public static ContactDetails contactDetailsWithMultipleAddresses() {
        return ContactDetails.builder().addresses(
            ImmutableList.of(aPreviousAddress(), aMainAddress(), aBailAddress())).build();
    }

    public static ContactDetails contactDetailsWithEmptyAddressList() {
        return ContactDetails.builder().addresses(new ArrayList<>()).build();
    }

    public static ContactDetails contactDetailsAddressesNonOfWhichHAveAMainStatus() {
        return ContactDetails.builder().addresses(ImmutableList.of(aPreviousAddress(), aBailAddress())).build();
    }

    public static ContactDetails contactDetailsHaveOneAddressWithNullStatus() {
        val addressWithNullStatus = aMainAddress().toBuilder()
            .status(null)
            .build();

        return ContactDetails.builder().addresses(ImmutableList.of(addressWithNullStatus)).build();
    }

    private static Offender aBasicOffender() {
        return Offender.builder()
            .firstName("Jimmy")
            .surname("Fizz")
            .middleNames(ImmutableList.of("Jammy", "Fred"))
            .otherIds(ImmutableMap.of("pncNumber", "2018/123456N"))
            .dateOfBirth("2000-06-22")
            .build();
    }

    private static OffenderAddress aMainAddress() {
        return OffenderAddress.builder()
            .buildingName("Main address Building")
            .addressNumber("7")
            .streetName("High Street")
            .district("Nether Edge")
            .town("Sheffield")
            .county("Yorkshire")
            .postcode("S10 1LE")
            .from("2000-02-11")
            .status(AddressStatus.builder()
                .code("M")
                .description("Main")
                .build())
            .build();
    }

    private static OffenderAddress aPreviousAddress() {
        return OffenderAddress.builder()
            .buildingName("Previous address Building")
            .addressNumber("14")
            .streetName("Low Street")
            .district("East Field")
            .town("Dover")
            .county("Kent")
            .postcode("K6 9SH")
            .from("1998-10-23")
            .to("2000-02-11")
            .status(AddressStatus.builder()
                .code("P")
                .description("Previous")
                .build())
            .build();
    }

    private static OffenderAddress aBailAddress() {
        return OffenderAddress.builder()
            .buildingName("Bail address building")
            .addressNumber("7")
            .streetName("High Street")
            .district("Nether Edge")
            .town("Sheffield")
            .county("Yorkshire")
            .postcode("S10 1LE")
            .from("2005-05-24")
            .status(AddressStatus.builder()
                .code("B")
                .description("Bail")
                .build())
            .build();
    }

}
