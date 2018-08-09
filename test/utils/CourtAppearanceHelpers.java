package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi;
import interfaces.OffenderApi.CourtAppearance;
import interfaces.OffenderApi.CourtAppearances;
import interfaces.OffenderApi.CourtReport;

public class CourtAppearanceHelpers {
    public static CourtAppearances someCourtAppearances() {

        return CourtAppearances.builder()
            .items(ImmutableList.of(CourtAppearance.builder()
                .appearanceDate("2018-08-06T00:00:00")
                .court(OffenderApi.Court.builder().courtName("Some court").build())
                .courtReports(ImmutableList.of(CourtReport.builder()
                    .courtReportId(41L).build()))
                .build()))
            .build();
    }
}
