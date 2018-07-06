package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.ContactDetails;
import interfaces.OffenderApi.Offender;
import interfaces.OffenderApi.OffenderAddress;

import java.util.ArrayList;

public class OffenderHelper {
    public static Offender anOffenderWithNoContactDetails() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            null
        );
    }

    public static Offender anOffenderWithEmptyContactDetails() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            emptyContactDetails());
    }

    public static Offender anOffenderWithEmptyAddressList() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            contactDetailsWithEmptyAddressList());
    }

    public static Offender anOffenderWithMultipleAddresses() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            contactDetailsWithMultipleAddresses()
            );
    }

    public static Offender anOffenderWithAddressListWithNoFromDate() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            contactDetailsAddressesHaveNoFromDates()
            );
    }

    public static ContactDetails emptyContactDetails() {
        return new ContactDetails(null);
    }

    public static ContactDetails contactDetailsAddressesHaveNoFromDates() {
        OffenderAddress address1 = new OffenderAddress(
            "Big Building",
            "7",
            "High Street",
            "Nether Edge",
            "Sheffield",
            "Yorkshire",
            "S10 1LE",
            null,
            null
        );

        return new ContactDetails(ImmutableList.of(address1));
    }

    public static ContactDetails contactDetailsPartialAddresses() {
        OffenderAddress address1 = new OffenderAddress(
            null,
            null,
            "High Street",
            null,
            "Sheffield",
            "Yorkshire",
            "S10 1LE",
            "2000-11-12",
            null
        );

        return new ContactDetails(ImmutableList.of(address1));
    }

    public static ContactDetails contactDetailsWithMultipleAddresses() {
        OffenderAddress address1 = new OffenderAddress(
            "Big Building",
            "7",
            "High Street",
            "Nether Edge",
            "Sheffield",
            "Yorkshire",
            "S10 1LE",
            "2000-11-12",
            null
        );

        OffenderAddress address2 = new OffenderAddress(
            "Small Building",
            "14",
            "Low Street",
            "East Field",
            "Dover",
            "Kent",
            "S10 1LE",
            "2000-02-11",
            "2000-02-12"
        );


        return new ContactDetails(ImmutableList.of(address2, address1));
    }

    public static ContactDetails contactDetailsWithEmptyAddressList() {
        return new ContactDetails(new ArrayList<>());
    }

}
