package helpers;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import play.api.mvc.Call;
import play.mvc.Http;

public interface CallHelper {

    static String relative(String url) {

        val path = Http.Context.current().request().path();
        val depth = Math.max(0, StringUtils.countMatches(path, "/") - 1);
        val relative = String.join("", IntStream.range(0, depth).mapToObj(x -> "../").collect(Collectors.toList()));

        return relative + url.substring(1);
    }

    static Call relative(Call call) {

        return call.copy(call.method(), relative(call.url()), call.fragment());
    }
}
