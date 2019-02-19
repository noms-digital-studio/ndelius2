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

    @Test
    public void itAppendsOffenceCountWhenPresentAndGreaterThanOne() {
        val mainOffence = OffenderApi.Offence.builder()
            .mainOffence(true)
            .offenceDate("2016-12-24T00:00")
            .offenceCount(3L)
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Main")
                .build())
            .build();

        val additionalOffence1 = OffenderApi.Offence.builder()
            .mainOffence(false)
            .offenceDate("2017-07-01T00:00")
            .offenceCount(1L)
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Additional one")
                .build())
            .build();

        val additionalOffence2 = OffenderApi.Offence.builder()
            .mainOffence(false)
            .offenceDate("2018-08-01T00:00")
            .offenceCount(2L)
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Additional two")
                .build())
            .build();

        val additionalOffence3 = OffenderApi.Offence.builder()
            .mainOffence(false)
            .offenceDate("2017-06-01T00:00")
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Additional three")
                .build())
            .build();

        val offences = ImmutableList.of(mainOffence, additionalOffence1, additionalOffence2, additionalOffence3);

        val conviction = OffenderApi.Conviction.builder()
            .offences(offences)
            .build();

        assertThat(conviction.allOffenceDescriptions()).isEqualTo("Main x 3 - 24/12/2016<br>Additional two x 2 - 01/08/2018<br>Additional one - 01/07/2017<br>Additional three - 01/06/2017");
    }

    @Test
    public void itHandlesAdditionalOffencesWithNoDateCorrectly() {
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

        val additionalOffence3 = OffenderApi.Offence.builder()
            .mainOffence(false)
            .offenceDate("2018-09-01T00:00")
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Additional three")
                .build())
            .build();

        val additionalOffence4 = OffenderApi.Offence.builder()
            .mainOffence(false)
            .detail(OffenceDetail.builder()
                .code("not used")
                .subCategoryDescription("Additional four")
                .build())
            .build();

        val offences = ImmutableList.of(mainOffence, additionalOffence1, additionalOffence2, additionalOffence3, additionalOffence4);

        val conviction = OffenderApi.Conviction.builder()
            .offences(offences)
            .build();

        assertThat(conviction.allOffenceDescriptions()).isEqualTo("Main - 24/12/2016<br>Additional three - 01/09/2018<br>Additional two - 01/08/2018<br>Additional one<br>Additional four");
    }


}
