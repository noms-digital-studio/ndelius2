package data;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParoleParom1ReportDataTest {
    private ParoleParom1ReportData data;

    @Before
    public void before() {
        data = new ParoleParom1ReportData();
    }

    @Test
    public void riskAssessmentRSRScoreLevel_invalid_blank() {
        data.setRiskAssessmentRSRScore("beans");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("");

        data.setRiskAssessmentRSRScore("");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("");
    }
    @Test
    public void riskAssessmentRSRScoreLevel_low_below_3() {
        data.setRiskAssessmentRSRScore("0");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("low");

        data.setRiskAssessmentRSRScore("2.9");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("low");
    }
    @Test
    public void riskAssessmentRSRScoreLevel_medium_between_3_and_7() {
        data.setRiskAssessmentRSRScore("3");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("medium");

        data.setRiskAssessmentRSRScore("6.9");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("medium");
    }
    @Test
    public void riskAssessmentRSRScoreLevel_high_7_or_above() {
        data.setRiskAssessmentRSRScore("7");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("high");

        data.setRiskAssessmentRSRScore("9.9");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("high");

        data.setRiskAssessmentRSRScore("99999.9");
        assertThat(data.getRiskAssessmentRSRScoreLevel()).isEqualTo("high");
    }

    @Test
    public void getRiskAssessmentOGRS3ReoffendingProbabilityLevel_invalid_blank() {
        data.setRiskAssessmentOGRS3ReoffendingProbability("beans");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("");

        data.setRiskAssessmentOGRS3ReoffendingProbability("");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("");
    }
    @Test
    public void getRiskAssessmentOGRS3ReoffendingProbabilityLevel_low_below_50() {
        data.setRiskAssessmentOGRS3ReoffendingProbability("0");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("low");

        data.setRiskAssessmentOGRS3ReoffendingProbability("49");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("low");
    }
    @Test
    public void getRiskAssessmentOGRS3ReoffendingProbabilityLevel_medium_between_50_and_75() {
        data.setRiskAssessmentOGRS3ReoffendingProbability("50");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("medium");

        data.setRiskAssessmentOGRS3ReoffendingProbability("74");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("medium");
    }
    @Test
    public void getRiskAssessmentOGRS3ReoffendingProbabilityLevel_high_between_75_and_90() {
        data.setRiskAssessmentOGRS3ReoffendingProbability("75");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("high");

        data.setRiskAssessmentOGRS3ReoffendingProbability("89");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("high");
    }
    @Test
    public void getRiskAssessmentOGRS3ReoffendingProbabilityLevel_veryHigh_90_or_above() {
        data.setRiskAssessmentOGRS3ReoffendingProbability("90");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("very_high");

        data.setRiskAssessmentOGRS3ReoffendingProbability("99");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("very_high");

        data.setRiskAssessmentOGRS3ReoffendingProbability("99999");
        assertThat(data.getRiskAssessmentOGRS3ReoffendingProbabilityLevel()).isEqualTo("very_high");
    }

    @Test
    public void getRiskAssessmentOGPReoffendingProbabilityLevel_invalid_blank() {
        data.setRiskAssessmentOGPReoffendingProbability("beans");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("");

        data.setRiskAssessmentOGPReoffendingProbability("");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("");
    }
    @Test
    public void getRiskAssessmentOGPReoffendingProbabilityLevel_low_below_34() {
        data.setRiskAssessmentOGPReoffendingProbability("0");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("low");

        data.setRiskAssessmentOGPReoffendingProbability("33");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("low");
    }
    @Test
    public void getRiskAssessmentOGPReoffendingProbabilityLevel_medium_between_34_and_67() {
        data.setRiskAssessmentOGPReoffendingProbability("34");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("medium");

        data.setRiskAssessmentOGPReoffendingProbability("66");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("medium");
    }
    @Test
    public void getRiskAssessmentOGPReoffendingProbabilityLevel_high_between_67_and_85() {
        data.setRiskAssessmentOGPReoffendingProbability("67");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("high");

        data.setRiskAssessmentOGPReoffendingProbability("84");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("high");
    }
    @Test
    public void getRiskAssessmentOGPReoffendingProbabilityLevel_veryHigh_85_or_above() {
        data.setRiskAssessmentOGPReoffendingProbability("85");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("very_high");

        data.setRiskAssessmentOGPReoffendingProbability("99");
        assertThat(data.getRiskAssessmentOGPReoffendingProbabilityLevel()).isEqualTo("very_high");
    }

    @Test
    public void getRiskAssessmentOVPReoffendingProbabilityLevel_invalid_blank() {
        data.setRiskAssessmentOVPReoffendingProbability("beans");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("");

        data.setRiskAssessmentOVPReoffendingProbability("");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("");
    }
    @Test
    public void getRiskAssessmentOVPReoffendingProbabilityLevel_low_below_30() {
        data.setRiskAssessmentOVPReoffendingProbability("0");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("low");

        data.setRiskAssessmentOVPReoffendingProbability("29");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("low");
    }
    @Test
    public void getRiskAssessmentOVPReoffendingProbabilityLevel_medium_between_30_and_60() {
        data.setRiskAssessmentOVPReoffendingProbability("30");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("medium");

        data.setRiskAssessmentOVPReoffendingProbability("59");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("medium");
    }
    @Test
    public void getRiskAssessmentOVPReoffendingProbabilityLevel_high_between_60_and_80() {
        data.setRiskAssessmentOVPReoffendingProbability("60");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("high");

        data.setRiskAssessmentOVPReoffendingProbability("79");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("high");
    }
    @Test
    public void getRiskAssessmentOVPReoffendingProbabilityLevel_veryHigh_80_or_above() {
        data.setRiskAssessmentOVPReoffendingProbability("80");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("very_high");

        data.setRiskAssessmentOVPReoffendingProbability("99");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("very_high");

        data.setRiskAssessmentOVPReoffendingProbability("9999");
        assertThat(data.getRiskAssessmentOVPReoffendingProbabilityLevel()).isEqualTo("very_high");
    }
}