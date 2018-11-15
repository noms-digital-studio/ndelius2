package services;

import interfaces.PrisonerApiToken;
import play.Logger;
import play.libs.ws.WSResponse;

import java.util.Optional;

import static play.mvc.Http.Status.*;

public class NomisReponseHelper {
    static Optional<WSResponse> checkForMaybeResponse(WSResponse wsResponse, PrisonerApiToken apiToken) {
        switch (wsResponse.getStatus()) {
            case OK:
                return Optional.of(wsResponse);
            case NOT_FOUND:
                return Optional.empty();
            case UNAUTHORIZED:
            case FORBIDDEN:
                apiToken.clearToken();
                Logger.error("NOMIS authentication token has expired or is invalid");
                throw new RuntimeException("NOMIS authentication token has expired or is invalid");
            default:
                Logger.error("Failed to retrieve offender record from NOMIS. Status code {}", wsResponse.getStatus());
                throw new RuntimeException(String.format("Failed to retrieve offender record from NOMIS. Status code %d", wsResponse.getStatus()));
        }
    }

}
