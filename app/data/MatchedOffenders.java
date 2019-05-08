package data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.List;

@Data
public class MatchedOffenders {
    public static MatchedOffenders veryHighConfidence(ObjectNode response) {
        return new MatchedOffenders(DefendantMatchConfidence.VERY_HIGH, response);
    }

    public static MatchedOffenders highConfidence(ObjectNode response) {
        return new MatchedOffenders(DefendantMatchConfidence.HIGH, response);
    }

    public static MatchedOffenders mediumConfidence(ObjectNode response) {
        return new MatchedOffenders(DefendantMatchConfidence.MEDIUM, response);
    }

    public static MatchedOffenders duplicateVeryHighConfidence(List<ObjectNode> response) {
        return new MatchedOffenders(DefendantMatchConfidence.VERY_HIGH, response);
    }

    public static MatchedOffenders duplicateHighConfidence(List<ObjectNode> response) {
        return new MatchedOffenders(DefendantMatchConfidence.HIGH, response);
    }

    public static MatchedOffenders duplicateLowConfidence(List<ObjectNode> response) {
        return new MatchedOffenders(DefendantMatchConfidence.LOW, response);
    }

    public static MatchedOffenders noMatch() {
        return new MatchedOffenders();
    }

    private DefendantMatchConfidence confidence;
    private List<ObjectNode> duplicates;
    private ObjectNode match;

    private MatchedOffenders(DefendantMatchConfidence confidence, ObjectNode match) {
        this.confidence = confidence;
        this.match = match;
    }
    private MatchedOffenders(DefendantMatchConfidence confidence, List<ObjectNode> duplicates) {
        this.confidence = confidence;
        this.duplicates = duplicates;
    }
    private MatchedOffenders() {
        this.confidence = DefendantMatchConfidence.NONE;
    }
}
