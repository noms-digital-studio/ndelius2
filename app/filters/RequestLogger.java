package filters;

import com.github.coveo.ua_parser.Parser;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.JwtHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import scala.collection.JavaConverters;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static controllers.SessionKeys.OFFENDER_API_BEARER_TOKEN;
import static play.mvc.Http.HeaderNames.USER_AGENT;
import static play.mvc.Http.MimeTypes.HTML;
import static play.mvc.Http.MimeTypes.JSON;

class RequestLogger {
    private static List<String> loggableContent = ImmutableList.of(HTML, JSON, "application/pdf");

    @Data
    @AllArgsConstructor
    public static class LogEntry {
        private final String message;
        private final Map<String, String> context;
    }

    static Optional<LogEntry> requestLogLine(long startTime, Http.RequestHeader requestHeader, Result result) {
        val session = session(requestHeader);

        val endTime = System.currentTimeMillis();
        val requestTime = endTime - startTime;

        return result.contentType()
                .filter(RequestLogger::shouldLog)
                .map(notUsed ->
                        new LogEntry(
                                String.format("%s %s", requestHeader.method(), requestHeader.uri()),
                                new ImmutableMap.Builder<String, String>()
                                        .put("sessionId", Optional.ofNullable(id(session)).orElse("unknown"))
                                        .put("user", userId(session))
                                        .put("agent",browser(requestHeader))
                                        .put("status", String.format("%d", result.status()))
                                        .put("duration", String.format("%dms", requestTime))
                                        .build()));


    }
    private static boolean shouldLog(String contentType) {
        return loggableContent.contains(contentType);
    }

    private static Http.Session session(Http.RequestHeader requestHeader) {
        return new Http.Session(JavaConverters.mapAsJavaMap(requestHeader.asScala().session().data()));
    }

    private static String userId(Http.Session session) {
        return Optional.ofNullable(session.get(OFFENDER_API_BEARER_TOKEN))
                .map(JwtHelper::principal)
                .orElse("unknown user");
    }

    private static String id(Http.Session session) {
        return session.get("id");
    }

    private static String browser(Http.RequestHeader requestHeader) {
        return requestHeader.header(USER_AGENT).map(userAgent -> {
            try {
                val client = new Parser().parse(userAgent);
                return String.format("%s %s", client.userAgent.family, client.userAgent.major);
            } catch (Exception e) {
                Logger.warn("Unable to parse user agent", e);
                return "unknown browser - error";
            }

        }).orElse("unknown browser");
    }

}
