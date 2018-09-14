package interfaces;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.CourtAppearance;
import interfaces.OffenderApi.CourtAppearances;
import interfaces.OffenderApi.Offence;
import interfaces.OffenderApi.OffenceDetail;
import interfaces.OffenderApi.Offences;
import interfaces.OffenderApi.Offender;
import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenceHelpers.someOffences;
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
        assertThat(someCourtAppearances().findForCourtReportId(4L).get().getCourtAppearanceId()).isEqualTo(3L);
    }

    @Test
    public void findsNoCourtAppearanceWhenNoCourtReportsMatch() {
        assertThat(someCourtAppearances().findForCourtReportId(42L).isPresent()).isFalse();
    }

    @Test
    public void findsNoCourtAppearanceWhenCourtReportsIsNull() {
        assertThat(courtAppearanceWithNullItems().findForCourtReportId(1L).isPresent()).isFalse();
    }

    @Test
    public void formatsTheOffenceDescriptionCorrectly() {
        val offence = Offence.builder()
            .offenceDate("2016-12-24T00:00")
            .detail(OffenceDetail.builder()
                .code("code")
                .subCategoryDescription("sub")
                .build()).build();
        assertThat(offence.offenceDescription()).isEqualTo("sub (code) - 24/12/2016");
    }

    @Test
    public void formatsTheOffenceDescriptionCorrectlyWhenDateIsMissing() {
        val offence = Offence.builder()
            .detail(OffenceDetail.builder()
                .code("00101")
                .subCategoryDescription("sub")
                .build()).build();
        assertThat(offence.offenceDescription()).isEqualTo("sub (00101)");
    }

    @Test
    public void itGetsTheCorrectMainOffenceId() {
        CourtAppearance courtAppearance = someCourtAppearances().getItems().get(0);
        assertThat(courtAppearance.mainOffenceId()).isEqualTo("M1");
    }

    @Test
    public void itReturnEmptyStringWhenThereIsNoMainOffenceId() {
        CourtAppearance courtAppearance = someCourtAppearances().getItems().get(1);
        assertThat(courtAppearance.mainOffenceId()).isEqualTo("");
    }

    @Test
    public void itGetsTheCorrectOtherOffenceIds() {
        CourtAppearance courtAppearance = someCourtAppearances().getItems().get(0);
        assertThat(courtAppearance.otherOffenceIds()).containsOnly("A1", "A2");
    }

    @Test
    public void itReturnsAnEmptyListWhenThereAreNoOtherOffenceIds() {
        CourtAppearance courtAppearance = someCourtAppearances().getItems().get(2);
        assertThat(courtAppearance.otherOffenceIds()).isEmpty();
    }

    @Test
    public void itFindsTheMainOffenceDescription() {
        assertThat(someOffences().mainOffenceDescriptionForId("M1")).isEqualTo("Some sub category description (00101) - 14/09/2018");
    }

    @Test
    public void itReturnsDefaultTextWhenIdsDontMatch() {
        assertThat(someOffences().mainOffenceDescriptionForId("M92")).isEqualTo("NO MAIN OFFENCE FOUND");
    }

    @Test
    public void itReturnsDefaultTextIfThereIsNoMainOffence() {
        assertThat(additionalOffences().mainOffenceDescriptionForId("M1")).isEqualTo("NO MAIN OFFENCE FOUND");
    }

    @Test
    public void itFindTheOtherOffenceDescriptions() {
        assertThat(someOffences().otherOffenceDescriptionsForIds(ImmutableList.of("A1", "A2"))).isEqualTo("A different sub category description (00202) - 13/09/2018");
    }

    @Test
    public void itReturnsEmptyStringWhenIdsDontMatchOffenceIds() {
        assertThat(someOffences().otherOffenceDescriptionsForIds(ImmutableList.of("A92", "A93"))).isEqualTo("");
    }

    @Test
    public void itReturnsEmptyStringWhenThereAreNoOtherOffenceDescriptions() {
        assertThat(onlyAMainOffence().otherOffenceDescriptionsForIds(ImmutableList.of("A1", "A2"))).isEqualTo("");
    }

    private CourtAppearances courtAppearanceWithNullItems() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .courtReports(null).build()
            )).build();
    }

    private Offences onlyAMainOffence() {

        return Offences.builder().items(
            ImmutableList.of(
                Offence.builder()
                    .offenceId("M1")
                    .mainOffence(true)
                    .detail(OffenceDetail.builder()
                        .code("00101")
                        .subCategoryDescription("Sub")
                        .build())
                    .build()
            )).build();
    }

    private Offences additionalOffences() {

        return Offences.builder().items(
            ImmutableList.of(
                Offence.builder()
                    .offenceId("A1")
                    .mainOffence(false)
                    .detail(OffenceDetail.builder()
                        .code("00102")
                        .subCategoryDescription("Sub")
                        .build())
                    .build()
            )).build();
    }
}
