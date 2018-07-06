package interfaces;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.Offender;
import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.OffenderHelper.*;

public class OffenderTest {

    @Test
    public void displayNameCorrectForFirstNameSurnameOnly() {
        val offender = new Offender(
            "Sam",
            "Jones",
            null,
            null,
            null,
            null
        );

        assertThat(offender.displayName()).isEqualTo("Sam Jones");
    }

    @Test
    public void displayNameCorrectForFirstNameSurnameOnlyWithEmptyMiddleNameArray() {
        val offender = new Offender(
            "Sam",
            "Jones",
            ImmutableList.of(),
            null,
            null,
            null
        );

        assertThat(offender.displayName()).isEqualTo("Sam Jones");
    }

    @Test
    public void displayNameCorrectForFirstNameSurnameAndMiddleName() {
        val offender = new Offender(
            "Sam",
            "Jones",
            ImmutableList.of("Ping", "Pong"),
            null,
            null,
            null
        );

        assertThat(offender.displayName()).isEqualTo("Sam Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingFirstName() {
        val offender = new Offender(
            null,
            "Jones",
            ImmutableList.of("Ping", "Pong"),
            null,
            null,
            null
        );
        assertThat(offender.displayName()).isEqualTo("Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingSurname() {
        val offender = new Offender(
            "Sam",
            null,
            ImmutableList.of("Ping", "Pong"),
            null,
            null,
            null
        );
        assertThat(offender.displayName()).isEqualTo("Sam Ping");
    }

    @Test
    public void displayNameCorrectForMissingEverything() {
        val offender = new Offender(
            null,
            null,
            null,
            null,
            null,
            null
        );
        assertThat(offender.displayName()).isEqualTo("");
    }

    @Test
    public void noAddressWhenContactDetailsAreEmpty() {
        val contactDetails = emptyContactDetails();
        assertThat(contactDetails.currentAddress().isPresent()).isFalse();
    }

    @Test
    public void noAddressWhenContactDetailsHaveNoFromDates() {
        val contactDetails = contactDetailsAddressesHaveNoFromDates();
        assertThat(contactDetails.currentAddress().isPresent()).isFalse();
    }

    @Test
    public void noAddressWhenContactDetailsHaveEmptyAddressList() {
        val contactDetails = contactDetailsWithEmptyAddressList();
        assertThat(contactDetails.currentAddress().isPresent()).isFalse();
    }

    @Test
    public void addressCorrectWhenContactDetailsHasMultipleAddress() {
        val contactDetails = contactDetailsWithMultipleAddresses();
        assertThat(contactDetails.currentAddress().get().render())
            .isEqualTo("Big Building\n7 High Street\nNether Edge\nSheffield\nYorkshire\nS10 1LE");
    }

    @Test
    public void addressCorrectWhenContactDetailsPartialAddress() {
        val contactDetails = contactDetailsPartialAddresses();
        assertThat(contactDetails.currentAddress().get().render())
            .isEqualTo("High Street\nSheffield\nYorkshire\nS10 1LE");
    }
}
