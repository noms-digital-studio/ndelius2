package interfaces;

import java.util.concurrent.CompletionStage;

public interface PrisonerApi {

    CompletionStage<byte[]> getImage(String nomisId);
}
