package helpers;

import org.elasticsearch.action.ActionListener;
import play.Logger;

public class FutureListener<T> extends CompletionStageAdapter<T> implements ActionListener<T> {
    @Override
    public void onResponse(T t) {
        promise.complete(t);
    }

    @Override
    public void onFailure(Exception e) {
        Logger.error("Got an error calling ElasticSearch API", e);
        promise.completeExceptionally(e);
    }
}
