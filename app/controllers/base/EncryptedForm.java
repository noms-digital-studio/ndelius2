package controllers.base;

import java.util.Map;
import java.util.function.Function;
import play.data.Form;
import play.data.format.Formatters;
import play.i18n.MessagesApi;
import play.mvc.Http;

public class EncryptedForm<T> extends Form<T> {

    private final Function<Map<String, String>, Map<String, String>> decrypter;

    public EncryptedForm(Class<T> clazz, Function<Map<String, String>, Map<String, String>> decrypter, MessagesApi messagesApi, Formatters formatters, javax.validation.Validator validator) {

        super(clazz, messagesApi, formatters, validator);

        this.decrypter = decrypter;
    }

    @Override
    protected Map<String, String> requestData(Http.Request request) {

        return decrypter.apply(super.requestData(request));
    }
}
