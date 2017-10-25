package helpers;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.val;

public interface ThrowableHelper {

    static String toMessageCauseStack(Throwable error) {

        val errorMessage = new StringBuilder();

        errorMessage.append(error);
        errorMessage.append("\n");
        errorMessage.append(String.join("\n", Arrays.stream(error.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList())));

        if (error.getCause() != null) {
            errorMessage.append("\nCaused by: ");
            errorMessage.append(toMessageCauseStack(error.getCause()));
        }

        return errorMessage.toString();
    }
}
