package controllers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParoleParom1ReportControllerTest {

    @Test
    public void categoryCodeTransformedToCodeFormValue() {
        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("A")).isEqualTo("a");
        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("B")).isEqualTo("b");
        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("C")).isEqualTo("c");
        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("D")).isEqualTo("d");
        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("T")).isEqualTo("open");
        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("R")).isEqualTo("closed");
        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("Q")).isEqualTo("restricted");

        assertThat(ParoleParom1ReportController.categoryCodeToFormValue("XXXX")).isEqualTo("xxxx");

    }
}