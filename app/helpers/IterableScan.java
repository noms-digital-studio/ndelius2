package helpers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.util.Iterator;
import java.util.Optional;

public class IterableScan implements Iterable<ScanResult>, Iterator<ScanResult> {

    private final AmazonDynamoDB amazon;
    private final ScanRequest scanRequest;

    private ScanResult result;

    public IterableScan(AmazonDynamoDB amazon,
                        ScanRequest scanRequest) {

        this.amazon = amazon;
        this.scanRequest = scanRequest;
    }

    @Override
    public Iterator<ScanResult> iterator() {

        return this;
    }

    @Override
    public boolean hasNext() {  // hasNext if haven't scanned yet, or have and have another scan to do

        return result == null || result.getLastEvaluatedKey() != null;
    }

    @Override
    public ScanResult next() {

        Optional.ofNullable(result).map(ScanResult::getLastEvaluatedKey).ifPresent(scanRequest::withExclusiveStartKey);

        result = amazon.scan(scanRequest);
        return result;
    }
}
