package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import data.annotations.Encrypted;
import data.annotations.OnPage;
import data.annotations.RequiredOnPage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import play.data.validation.ValidationError;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper=false)
public class ReportGeneratorWizardData extends WizardData {

    @Encrypted
    @RequiredOnPage(1)
    @JsonIgnore
    private Long entityId;

    @Encrypted
    @JsonIgnore
    private String documentId;

    @Encrypted
    @RequiredOnPage(1)
    @JsonProperty("_DELIUS_CRN_")
    private String crn;

    @OnPage(1)
    @Encrypted
    @JsonProperty("START_DATE")
    private String startDate;

    @JsonProperty("_WATERMARK_")
    private String watermark;

    protected List<Function<Map<String, Object>, Stream<ValidationError>>> reportSpecificValidators() {
        return ImmutableList.of();
    }
}
