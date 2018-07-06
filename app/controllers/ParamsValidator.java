package controllers;

import com.typesafe.config.Config;
import lombok.val;
import play.Logger;
import play.mvc.Result;

import java.time.Instant;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.unauthorized;

public interface ParamsValidator {

    Config getConfiguration();

    default Optional<Result> invalidCredentials(String username, String epochRequestTime, Runnable errorReporter) {

        if (isBlank(username) || isBlank(epochRequestTime)) {
            errorReporter.run();
            return Optional.of(badRequest("one or both of 'user' or 't' not supplied"));
        }

        val timeNowInstant = Instant.now();
        val epochRequestInstant = Instant.ofEpochMilli(Long.valueOf(epochRequestTime));

        val userTokenValidDuration = getConfiguration().getDuration("params.user.token.valid.duration");

        if (Math.abs(timeNowInstant.toEpochMilli() - epochRequestInstant.toEpochMilli()) > userTokenValidDuration.toMillis()) {
            Logger.warn(String.format(
                "Request not authorised because time currently is %s but token time %s",
                timeNowInstant.toString(),
                epochRequestInstant.toString()));

            return Optional.of(unauthorized());
        }

        return Optional.empty();
    }

}
