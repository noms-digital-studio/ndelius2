package data.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import data.annotations.RequiredOnPage;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import play.data.validation.Constraints.*;
import play.data.validation.ValidationError;

@Data
public class WizardData {

    @Required
    @JsonIgnore
    private Integer pageNumber;

    public List<ValidationError> validate() {   // validate() is called by Play Form submission bindFromRequest()

        return requiredFields().filter(field -> {

            field.setAccessible(true);

            try {
                return field.getAnnotation(RequiredOnPage.class).value() <= pageNumber &&
                        Strings.isNullOrEmpty(field.get(this).toString());
            }
            catch (IllegalAccessException ex) {
                return true;
            }

        }).map(field -> new ValidationError(field.getName(), RequiredValidator.message)).collect(Collectors.toList());
    }

    public Integer totalPages() {

        return requiredFields().mapToInt(field -> field.getAnnotation(RequiredOnPage.class).value()).max().orElse(0);
    }

    private Stream<Field> requiredFields() {

        return Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(RequiredOnPage.class));
    }
}
