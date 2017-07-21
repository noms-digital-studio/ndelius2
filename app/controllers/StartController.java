package controllers;

import com.typesafe.config.Config;
import helpers.Encryption;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.val;
import play.mvc.*;
import views.html.startReport;

public class StartController extends Controller {

    private final startReport template;
    private final Function<String, String> encrypter;

    @Inject
    public StartController(startReport template,
                           Config configuration) {

        this.template = template;

        val paramsSecretKey = configuration.getString("params.secret.key");

        encrypter = plainText -> Encryption.encrypt(plainText, paramsSecretKey);
    }

    public Result startReport() {

        val reportData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", "johnsmith");
                put("entityId", "12345");
                put("name", "Alan Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "1 Albert Square, Manchester, Greater Manchester, M60 2LA");
                put("crn", "B56789");
                put("pnc", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", new SimpleDateFormat("dd/MM/yyy").format(new Date()));
                put("localJusticeArea", "Greater Manchester");
            }
        };

        val encryptedData = reportData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {

            try {
                return URLEncoder.encode(encrypter.apply(entry.getValue()), "UTF-8");
            }
            catch (UnsupportedEncodingException ex) {

                return entry;
            }
        }));

        val parameters = String.join("&", encryptedData.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.toList()));

        return ok(template.render(parameters));
    }
}
