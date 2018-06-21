package filters;

import akka.stream.Materializer;
import lombok.val;
import play.Logger;
import play.mvc.Filter;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class LoggingFilter extends Filter {
    @Inject
    public LoggingFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<RequestHeader, CompletionStage<Result>> nextFilter, RequestHeader requestHeader) {
        val startTime = System.currentTimeMillis();

        return nextFilter.apply(requestHeader).thenApply(result -> {
            RequestLogger.requestLogLine(startTime, requestHeader, result).ifPresent(Logger::info);
            return result;
        });
    }


}