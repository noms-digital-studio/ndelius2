package data;

import lombok.Data;
import play.data.validation.Constraints;

@Data
public class ReportData {

    @Constraints.Required
    private String name;

    @Constraints.Required
    private Integer age;
}
