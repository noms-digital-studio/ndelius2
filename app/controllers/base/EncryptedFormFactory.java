package controllers.base;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.function.Function;
import javax.validation.Validator;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.i18n.MessagesApi;

@Singleton
public class EncryptedFormFactory extends FormFactory {

    private final MessagesApi messagesApi;
    private final Formatters formatters;
    private final Validator validator;

    @Inject
    public EncryptedFormFactory(MessagesApi messagesApi, Formatters formatters, Validator validator) {

        super(messagesApi, formatters, validator);

        this.messagesApi = messagesApi;
        this.formatters = formatters;
        this.validator = validator;
    }

    public <T> EncryptedForm<T> form(Class<T> clazz, Function<Map<String, String>, Map<String, String>> decrypter) {

        return new EncryptedForm<>(clazz, decrypter, messagesApi, formatters, validator);
    }
}
