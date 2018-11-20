package services;

import com.google.common.collect.ImmutableList;
import lombok.val;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static services.NomisCustodyApi.OffenderTransformer.offenderOf;

public class NomisCustodyApi_OffenderTransformerTest {
    @Test
    public void agencyLocationDescriptionUsedForInstitutionDescription() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .bookings(ImmutableList.of(
                                aBooking(1)
                                        .toBuilder()
                                        .activeFlag(true)
                                        .agencyLocation(
                                                aAgencyLocation()
                                                        .toBuilder()
                                                        .description("HMP Manchester")
                                                        .build())
                                        .build()))
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getInstitution().getDescription()).isEqualTo("HMP Manchester");
    }

    @Test
    public void agencyLocationDescriptionUsedForInstitutionDescriptionEvenForInactiveBookings() {
        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .bookings(ImmutableList.of(
                                aBooking(1)
                                        .toBuilder()
                                        .activeFlag(false)
                                        .agencyLocation(
                                                aAgencyLocation()
                                                        .toBuilder()
                                                        .description("HMP Manchester")
                                                        .build())
                                        .build()))
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getInstitution().getDescription()).isEqualTo("HMP Manchester");
    }

    @Test
    public void bookingNumberUsedForPrisonNumber() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .bookings(ImmutableList.of(
                                aBooking(1)
                                        .toBuilder()
                                        .bookingId(4815)
                                        .bookingNo("D88666")
                                        .build()))
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getMostRecentPrisonerNumber()).isEqualTo("D88666");
    }

    @Test
    public void firstNameIsCopied() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .firstName("Rodger")
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getFirstName()).isEqualTo("Rodger");
    }
    @Test
    public void surnameIsCopied() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .surname("Smiles")
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getSurname()).isEqualTo("Smiles");
    }


    @Test
    public void exceptionWhenNoCurrentBooking() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .bookings(ImmutableList.of(aBooking(99)))
                        .build();

        assertThatThrownBy(() -> offenderOf(offenderEntity)).hasMessage("No current booking for offender found");

    }

    private NomisCustodyApi.OffenderEntity aOffenderEntity() {
        return NomisCustodyApi.OffenderEntity.builder()
                .firstName("Tommy")
                .surname("Blacks")
                .bookings(someBookings()).build();
    }

    private List<NomisCustodyApi.Booking> someBookings() {
        return ImmutableList.of(
                aBooking(1),
                aBooking(2)
        );
    }

    private NomisCustodyApi.Booking aBooking(int bookingSequence) {
        return NomisCustodyApi.Booking.builder()
                .agencyLocation(aAgencyLocation())
                .bookingSequence(bookingSequence)
                .activeFlag(true)
                .build();
    }

    private NomisCustodyApi.AgencyLocation aAgencyLocation() {
        return NomisCustodyApi.AgencyLocation.builder().description("HMP Humber").build();
    }



}