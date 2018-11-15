package interfaces;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.CourtAppearance;
import interfaces.OffenderApi.CourtAppearances;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.CourtAppearanceHelpers.someCourtAppearances;

public class OffenderApi_CourtAppearanceTest {

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

    private CourtAppearances courtAppearanceWithNullItems() {
        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .courtReports(null).build()
            )).build();
    }

}
