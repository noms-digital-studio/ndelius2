package interfaces;

import java.util.concurrent.CompletionStage;

public interface OffenderApi {
    CompletionStage<String> logon(String username);

    CompletionStage<Boolean> canAccess(String bearerToken, long offenderId);
}
