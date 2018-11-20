package services;

import lombok.val;
import org.junit.Test;

import static interfaces.PrisonerCategoryApi.Category;
import static org.assertj.core.api.Assertions.assertThat;
import static services.NomisElite2Api.CategoryTransformer.categoryOf;

public class NomisElite2Api_CategoryTransformerTest {
    @Test
    public void codeUsedForCategoryCode() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .category("CAT B")
                        .categoryCode("B")
                        .build();

        val category = categoryOf(offenderEntity);

        assertThat(category.map(Category::getCode).orElseThrow(() -> new AssertionError("No category"))).isEqualTo("B");
    }

    @Test
    public void categoryUsedForCategoryDescription() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .category("CAT B")
                        .categoryCode("B")
                        .build();

        val category = categoryOf(offenderEntity);

        assertThat(category.map(Category::getDescription).orElseThrow(() -> new AssertionError("No category"))).isEqualTo("CAT B");
    }


    @Test
    public void emptyReturnedWhenNoCategoryCode() {

        val offenderEntity =
                aOffenderEntity()
                        .toBuilder()
                        .categoryCode(null)
                        .build();

        val category = categoryOf(offenderEntity);

        assertThat(category.isPresent()).isFalse();
    }


    private NomisElite2Api.OffenderEntity aOffenderEntity() {
        return NomisElite2Api.OffenderEntity.builder().categoryCode("A").category("CAT A").build();
    }


}