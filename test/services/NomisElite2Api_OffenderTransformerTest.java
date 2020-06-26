package services;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static services.NomisElite2Api.LivingUnit;
import static services.NomisElite2Api.OffenderEntity;
import static services.NomisElite2Api.OffenderTransformer.offenderOf;

public class NomisElite2Api_OffenderTransformerTest {
    @Test
    public void detailsCopied() {

        val offenderEntity =
                OffenderEntity.builder()
                        .firstName("Bob")
                        .lastName("Builder")
                        .bookingNo("V38608")
                        .assignedLivingUnit(LivingUnit
                                .builder()
                                .agencyName("HMP Brixton")
                                .build())
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getFirstName()).isEqualTo("Bob");
        assertThat(offender.getSurname()).isEqualTo("Builder");
        assertThat(offender.getMostRecentPrisonerNumber()).isEqualTo("V38608");
    }

    @Test
    public void locationTakenFromAssignedLivingUnitAgencyName() {

        val offenderEntity =
                OffenderEntity.builder()
                        .firstName("Bob")
                        .lastName("Builder")
                        .bookingNo("V38608")
                        .assignedLivingUnit(LivingUnit
                                .builder()
                                .agencyName("HMP Brixton")
                                .build())
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getInstitution().getDescription()).isEqualTo("HMP Brixton");
    }

    @Test
    public void locationWillBeUnknownWhenAssignedLivingUnitMissing() {

        val offenderEntity =
                OffenderEntity.builder()
                        .firstName("Bob")
                        .lastName("Builder")
                        .bookingNo("V38608")
                        .assignedLivingUnit(null)
                        .build();

        val offender = offenderOf(offenderEntity);

        assertThat(offender.getInstitution().getDescription()).isEqualTo("Unknown");
    }

}