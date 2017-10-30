package data.nationalSearch;

import lombok.Value;
import java.util.List;

@Value
public class Response {

    private String mistake;
    private List<String> suggestions;
}
