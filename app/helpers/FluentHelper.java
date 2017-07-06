package helpers;

import java.util.function.Predicate;

public interface FluentHelper {

    static <T> Predicate<T> not(Predicate<T> predicate) {

        return predicate.negate();
    }

    static <T> Predicate<T> value(boolean result) {

        return ignored -> result;
    }
}
