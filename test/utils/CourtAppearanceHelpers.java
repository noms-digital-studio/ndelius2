package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.Court;
import interfaces.OffenderApi.CourtAppearance;
import interfaces.OffenderApi.CourtAppearances;
import interfaces.OffenderApi.CourtReport;

public class CourtAppearanceHelpers {
    public static CourtAppearances someCourtAppearances() {

        return CourtAppearances.builder()
            .items(ImmutableList.of(
                CourtAppearance.builder()
                    .courtAppearanceId(1L)
                    .courtReports(ImmutableList.of(
                        CourtReport.builder()
                            .courtReportId(41L)
                            .build()
                    ))
                    .offenceIds(ImmutableList.of("M1", "A1", "A2"))
                    .appearanceDate("2018-08-06T00:00:00")
                    .court(Court.builder().courtName("Some court").locality("Leeds Justice Area").build())
                    .build(),
                CourtAppearance.builder()
                    .courtAppearanceId(2L)
                    .courtReports(ImmutableList.of(
                        CourtReport.builder()
                            .courtReportId(2L)
                            .build(),
                        CourtReport.builder()
                            .courtReportId(3L)
                            .build()
                    ))
                    .offenceIds(ImmutableList.of("A1", "A2"))
                    .build(),
                CourtAppearance.builder()
                    .courtAppearanceId(3L)
                    .courtReports(ImmutableList.of(
                        CourtReport.builder()
                            .courtReportId(4L)
                            .build()
                    ))
                    .offenceIds(ImmutableList.of("M1"))
                    .build()
            )).build();
    }
}

