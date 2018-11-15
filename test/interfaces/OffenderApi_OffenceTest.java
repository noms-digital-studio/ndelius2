package interfaces;

import com.google.common.collect.ImmutableList;
import lombok.val;
import org.junit.Test;
import utils.OffenceHelpers;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.OffenceHelpers.someOffences;


public class OffenderApi_OffenceTest {
    @Test
    public void formatsTheOffenceDescriptionCorrectly() {
        val offence = OffenderApi.Offence.builder()
            .offenceDate("2016-12-24T00:00")
            .detail(OffenderApi.OffenceDetail.builder()
                .code("code")
                .subCategoryDescription("sub")
                .build()).build();
        assertThat(offence.offenceDescription()).isEqualTo("sub (code) - 24/12/2016");
    }

    @Test
    public void formatsTheOffenceDescriptionCorrectlyWhenDateIsMissing() {
        val offence = OffenderApi.Offence.builder()
            .detail(OffenderApi.OffenceDetail.builder()
                .code("00101")
                .subCategoryDescription("sub")
                .build()).build();
        assertThat(offence.offenceDescription()).isEqualTo("sub (00101)");
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
        assertThat(OffenceHelpers.additionalOffences().mainOffenceDescriptionForId("M1")).isEqualTo("NO MAIN OFFENCE FOUND");
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
        assertThat(OffenceHelpers.onlyAMainOffence().otherOffenceDescriptionsForIds(ImmutableList.of("A1", "A2"))).isEqualTo("");
    }

}
