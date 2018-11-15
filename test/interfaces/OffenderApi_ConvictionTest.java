package interfaces;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.OffenceDetail;
import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OffenderApi_ConvictionTest {

    @Test
    public void itCombinesTheMainAndAdditionalOffencesOrderingMainFirstFollowedByAdditionalOffencesInDateOrder() {
        val mainOffence = OffenderApi.Offence.builder()
            .mainOffence(true)
            .offenceDate("2016-12-24T00:00")
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Main")
                .build())
            .build();

        val additionalOffence1 = OffenderApi.Offence.builder()
            .mainOffence(false)
            .offenceDate("2017-07-01T00:00")
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Additional one")
                .build())
            .build();

        val additionalOffence2 = OffenderApi.Offence.builder()
            .mainOffence(false)
            .offenceDate("2018-08-01T00:00")
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Additional two")
                .build())
            .build();

        val offences = ImmutableList.of(mainOffence, additionalOffence1, additionalOffence2);

        val conviction = OffenderApi.Conviction.builder()
            .offences(offences)
            .build();

        assertThat(conviction.allOffenceDescriptions()).isEqualTo("Main - 24/12/2016<br>Additional two - 01/08/2018<br>Additional one - 01/07/2017");
    }


}
