package data;

import lombok.Data;

import java.util.Map;

@Data
public class Params {
    private Map<String, String> words;
}