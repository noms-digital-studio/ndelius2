package data.annotations;

import play.data.validation.Constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredGroupOnPage {

    int value();

    String group() default "defaultGroup";

    String message() default Constraints.RequiredValidator.message;

    boolean errorWhenInvalid() default true;

}
