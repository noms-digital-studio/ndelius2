package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.Offence;
import interfaces.OffenderApi.OffenceDetail;
import interfaces.OffenderApi.Offences;

public class OffenceHelpers {
    public static Offences someOffences() {

        return Offences.builder()
            .items(ImmutableList.of(
                Offence.builder()
                    .offenceId("M1")
                    .mainOffence(true)
                    .offenceDate("2018-09-14T00:00")
                    .detail(OffenceDetail.builder()
                        .code("00101")
                        .subCategoryDescription("Some sub category description")
                        .build())
                    .build(),
                Offence.builder()
                    .offenceId("A1")
                    .mainOffence(false)
                    .offenceDate("2018-09-13T00:00")
                    .detail(OffenceDetail.builder()
                        .code("00202")
                        .subCategoryDescription("A different sub category description")
                        .build())
                    .build()
                ))
            .build();
    }
}
