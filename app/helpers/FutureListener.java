package helpers;

import org.elasticsearch.action.ActionListener;

public class FutureListener<T> extends CompletionStageAdapter<T> implements ActionListener<T> {
    @Override
    public void onResponse(T t) {
        promise.complete(t);
    }

    @Override
    public void onFailure(Exception e) {
        promise.completeExceptionally(e);
    }
}
