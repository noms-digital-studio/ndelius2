package utils;

import interfaces.PrisonerApi;
import interfaces.PrisonerApi.Offender;
import interfaces.PrisonerCategoryApi.Category;

public class PrisonerHelper {
    public static Offender offenderInPrison() {
        return aBasicOffender();
    }
    public static Offender offenderAtPrison(String prison) {
        return aBasicOffender().toBuilder().institution(PrisonerApi.Institution.builder().description(prison).build()).build();
    }
    public static Offender offenderWithMostRecentPrisonerNumber(String mostRecentPrisonerNumber) {
        return aBasicOffender().toBuilder().mostRecentPrisonerNumber(mostRecentPrisonerNumber).build();
    }

    private static Offender aBasicOffender() {
        return Offender.builder()
                .mostRecentPrisonerNumber("4815")
                .institution(PrisonerApi.Institution.builder()
                        .description("HMP Wandsworth")
                        .build())
                .build();

    }

    public static Category offenderCategory() {
        return offenderCategory("A", "Cat A");
    }
    public static Category offenderCategory(String code, String description) {
        return Category.builder().code(code).description(description).build();
    }
}
