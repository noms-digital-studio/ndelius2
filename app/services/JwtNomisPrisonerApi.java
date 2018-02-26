package services;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.PrisonerApiToken;
import lombok.val;
import pdi.jwt.Jwt;
import pdi.jwt.JwtAlgorithm;

import javax.inject.Inject;
import java.time.Instant;

public class JwtNomisPrisonerApi implements PrisonerApiToken {

    private final String privateKey;
    private final String payloadToken;

   @Inject
    public JwtNomisPrisonerApi(Config configuration) {

        privateKey = configuration.getString("nomis.private.key");
        payloadToken = configuration.getString("nomis.payload.token");
    }

    @Override
    public String get() {

        val claim = ImmutableMap.of(
                "iat", Instant.now().getEpochSecond(),
                "token", payloadToken
        );

        return Jwt.encode(JsonHelper.stringify(claim), privateKey, JwtAlgorithm.ES256$.MODULE$);
    }
}
