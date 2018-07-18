package data;

import data.annotations.OnPage;
import data.base.ReportGeneratorWizardData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ParoleParom1ReportData extends ReportGeneratorWizardData {

    // Page 23
    @OnPage(23)
    private String dummy23;

}
