package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InstitutionalReportHelpers {

    public static String getConvictionDate() {
        return LocalDate.now().plusMonths(-6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static OffenderApi.InstitutionalReport anInstitutionalReport() {
        return OffenderApi.InstitutionalReport.builder()
            .conviction(OffenderApi.Conviction.builder()
                .convictionDate(getConvictionDate())
                .offences(ImmutableList.of(OffenderApi.Offence.builder()
                    .offenceId("123")
                    .mainOffence(true)
                    .offenceDate("2018-11-08T00:00")
                    .detail(OffenderApi.OffenceDetail.builder()
                        .subCategoryDescription("Stealing the limelight")
                        .code("code123")
                        .build())
                    .build(),
                    OffenderApi.Offence.builder()
                        .offenceId("123")
                        .mainOffence(false)
                        .offenceDate("2016-06-06T00:00")
                        .detail(OffenderApi.OffenceDetail.builder()
                            .subCategoryDescription("Jumping the queue")
                            .code("code456")
                            .build())
                        .build(),
                    OffenderApi.Offence.builder()
                        .offenceId("123")
                        .mainOffence(false)
                        .offenceDate("2017-07-07T00:00")
                        .detail(OffenderApi.OffenceDetail.builder()
                            .subCategoryDescription("Interrupting")
                            .code("code789")
                            .build())
                        .build()
                    ))
                .build())
            .build();
    }

    public static OffenderApi.InstitutionalReport anInstitutionalReportWithOffence(String desc, String code, String date) {
        return OffenderApi.InstitutionalReport.builder()
            .conviction(OffenderApi.Conviction.builder()
                .convictionDate(getConvictionDate())
                .offences(ImmutableList.of(anOffenceWith(desc, code, date)))
                .build())
            .build();
    }

    public static OffenderApi.InstitutionalReport anInstitutionalReportWithOffenceButNoConvictionDate(String desc, String code, String date) {
        return OffenderApi.InstitutionalReport.builder()
            .conviction(OffenderApi.Conviction.builder()
                .offences(ImmutableList.of(anOffenceWith(desc, code, date)))
                .build())
            .build();
    }

    public static OffenderApi.InstitutionalReport anInstitutionalReportWithSentence(String sentenceDescription,
                                                                                    Long originalLength,
                                                                                    String originalLengthUnits) {
        return OffenderApi.InstitutionalReport.builder()
            .sentence(OffenderApi.Sentence.builder()
                .description(sentenceDescription)
                .originalLength(originalLength)
                .originalLengthUnits(originalLengthUnits)
                .build())
            .build();
    }

    private static OffenderApi.Offence anOffenceWith(String desc, String code, String date) {
        return OffenderApi.Offence.builder()
            .offenceId("123")
            .mainOffence(true)
            .offenceDate(date + "T00:00")
            .detail(OffenderApi.OffenceDetail.builder()
                .subCategoryDescription(desc)
                .code(code)
                .build())
            .build();
    }
}
