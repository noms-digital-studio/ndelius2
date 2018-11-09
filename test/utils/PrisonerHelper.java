package utils;

import interfaces.PrisonerApi;
import interfaces.PrisonerApi.Offender;

public class PrisonerHelper {
    public static Offender offenderInPrison() {
        return offenderAtPrison("HMP Wandsworth");
    }
    public static Offender offenderAtPrison(String prison) {
        return Offender.builder().institution(PrisonerApi.Institution.builder().description(prison).build()).build();
    }
}
