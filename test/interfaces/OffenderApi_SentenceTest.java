package interfaces;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OffenderApi_SentenceTest {
    @Test
    public void itCombinesTheDescriptionLengthAndUnits() {
        OffenderApi.Sentence sentence = OffenderApi.Sentence.builder()
            .description("Adult Custody < 12m")
            .originalLength(6L)
            .originalLengthUnits("Months")
            .build();

        assertThat(sentence.descriptionAndLength()).isEqualTo("Adult Custody < 12m, 6 Months.");
    }
}
