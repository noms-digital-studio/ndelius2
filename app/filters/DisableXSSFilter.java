package filters;

import akka.stream.Materializer;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class DisableXSSFilter extends Filter {
    @Inject
    public DisableXSSFilter(Materializer mat) {
        super(mat);
    }

    private Result addXssProtectionHeader(Result result) {
        return result.withHeader("X-XSS-Protection", "0");
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> result, Http.RequestHeader requestHeader) {
        return result.apply(requestHeader).thenApply(this::addXssProtectionHeader);
    }
}
