package interfaces;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.CourtAppearance;
import interfaces.OffenderApi.CourtAppearances;
import interfaces.OffenderApi.Offender;
import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.OffenderHelper.contactDetailsAddressesNonOfWhichHAveAMainStatus;
import static utils.OffenderHelper.contactDetailsHaveOneAddressWithNullStatus;
import static utils.OffenderHelper.contactDetailsWithEmptyAddressList;
import static utils.OffenderHelper.contactDetailsWithMultipleAddresses;
import static utils.OffenderHelper.emptyContactDetails;

public class OffenderTest {

    @Test
    public void displayNameCorrectForFirstNameSurnameOnly() {
        val offender = Offender.builder()
            .firstName("Sam")
            .surname("Jones")
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Jones");
    }

    @Test
    public void displayNameCorrectForFirstNameSurnameOnlyWithEmptyMiddleNameArray() {
        val offender = Offender.builder()
            .firstName("Sam")
            .surname("Jones")
            .middleNames(ImmutableList.of())
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Jones");
    }

    @Test
    public void displayNameCorrectForFirstNameSurnameAndMiddleName() {
        val offender = Offender.builder()
            .firstName("Sam")
            .surname("Jones")
            .middleNames(ImmutableList.of("Ping", "Pong"))
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingFirstName() {
        val offender = Offender.builder()
            .surname("Jones")
            .middleNames(ImmutableList.of("Ping", "Pong"))
            .build();

        assertThat(offender.displayName()).isEqualTo("Ping Jones");
    }

    @Test
    public void displayNameCorrectForMissingSurname() {
        val offender = Offender.builder()
            .firstName("Sam")
            .middleNames(ImmutableList.of("Ping", "Pong"))
            .build();

        assertThat(offender.displayName()).isEqualTo("Sam Ping");
    }

    @Test
    public void displayNameCorrectForMissingEverything() {
        assertThat(Offender.builder().build().displayName()).isEqualTo("");
    }

    @Test
    public void noMainAddressWhenContactDetailsAreEmpty() {
        assertThat(emptyContactDetails().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void noMainAddressWhenContactDetailsHaveEmptyAddressList() {
        assertThat(contactDetailsWithEmptyAddressList().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void noMainAddressWhenContactDetailsHaveNoAddressesWithAMainStatus() {
        assertThat(contactDetailsAddressesNonOfWhichHAveAMainStatus().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void noMainAddressWhenContactDetailsHaveOneAddressWithNullStatus() {
        assertThat(contactDetailsHaveOneAddressWithNullStatus().mainAddress().isPresent()).isFalse();
    }

    @Test
    public void selectsTheMainAddressFromMultipleAddresses() {
        assertThat(contactDetailsWithMultipleAddresses().mainAddress().get().render())
            .isEqualTo("Main address Building\n7 High Street\nNether Edge\nSheffield\nYorkshire\nS10 1LE");
    }

    @Test
    public void findsCourtAppearanceForCourtReportId() {
        assertThat(courtAppearances().findForCourtReportId(1L).get().getCourtAppearanceId()).isEqualTo(3L);
    }

    @Test
    public void findsNoCourtAppearanceWhenAllAreSoftDeleted() {
        assertThat(softDeletedCourtAppearances().findForCourtReportId(1L).isPresent()).isFalse();
    }

    @Test
    public void findsNoCourtAppearanceWhenAllCourtReportsAreSoftDeleted() {
        assertThat(courtAppearancesWithSoftDeletedCourtReports().findForCourtReportId(1L).isPresent()).isFalse();
    }

    @Test
    public void findsNoCourtAppearanceWhenNoCourtReportsMatch() {
        assertThat(courtAppearances().findForCourtReportId(3L).isPresent()).isFalse();
    }

    @Test
    public void findsNoCourtAppearanceWhenCourtReportsIsNull() {
        assertThat(courtAppearanceWithNullItems().findForCourtReportId(1L).isPresent()).isFalse();
    }

    private CourtAppearances courtAppearanceWithNullItems() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .courtReports(null).build()
            )).build();
    }
    private CourtAppearances courtAppearances() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .softDeleted(true)
                    .courtReports(ImmutableList.of(
                        OffenderApi.CourtReport.builder()
                            .courtReportId(1L)
                            .build()
                    )).build(),
                CourtAppearance.builder()
                    .courtAppearanceId(2L)
                    .courtReports(ImmutableList.of(
                        OffenderApi.CourtReport.builder()
                            .courtReportId(2L)
                            .build(),
                        OffenderApi.CourtReport.builder()
                            .courtReportId(1L)
                            .softDeleted(true)
                            .build()
                    )).build(),
                CourtAppearance.builder()
                    .courtAppearanceId(3L)
                    .courtReports(ImmutableList.of(
                        OffenderApi.CourtReport.builder()
                            .courtReportId(1L)
                            .build()
                    )).build()
                )).build();
    }

    private CourtAppearances softDeletedCourtAppearances() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .softDeleted(true)
                    .courtReports(ImmutableList.of(
                        OffenderApi.CourtReport.builder()
                            .courtReportId(1L)
                            .build()
                    )).build(),
                CourtAppearance.builder()
                    .courtAppearanceId(2L)
                    .softDeleted(true)
                    .courtReports(ImmutableList.of(
                        OffenderApi.CourtReport.builder()
                            .courtReportId(2L)
                            .build()
                    )).build()
            )).build();
    }

    private CourtAppearances courtAppearancesWithSoftDeletedCourtReports() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .courtReports(ImmutableList.of(
                        OffenderApi.CourtReport.builder()
                            .courtReportId(1L)
                            .softDeleted(true)
                            .build()
                    )).build(),
                CourtAppearance.builder()
                    .courtAppearanceId(2L)
                    .courtReports(ImmutableList.of(
                        OffenderApi.CourtReport.builder()
                            .courtReportId(2L)
                            .softDeleted(true)
                            .build()
                    )).build()
            )).build();
    }
}
