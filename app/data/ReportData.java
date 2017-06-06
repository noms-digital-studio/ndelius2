package data;

import lombok.Data;
import play.data.validation.Constraints.Required;

@Data
public class ReportData {

    @Required
    private String name;

    @Required
    private Integer age;
}
