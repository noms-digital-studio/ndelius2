package helpers;

import java.util.function.Function;
import java.util.function.Predicate;
import play.twirl.api.Content;
import play.twirl.api.Txt;

public interface FluentHelper {

    static <T> Predicate<T> not(Predicate<T> predicate) {

        return predicate.negate();
    }

    static <T> Predicate<T> value(boolean result) {

        return ignored -> result;
    }

    static <T, R> Function<T, R> value(R result) {

        return ignored -> result;
    }

    static Content content(Throwable ex) {

        return Txt.apply(ex.getMessage());
    }
}
