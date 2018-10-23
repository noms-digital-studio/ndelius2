package data.annotations;

import play.data.validation.Constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateOnPage {

    int value();

    String onlyIfField() default "";

    String onlyIfFieldMatchValue() default "";

    String incompleteMessage() default Constraints.RequiredValidator.message;
    String invalidMessage() default Constraints.RequiredValidator.message;
    String outOfRangeMessage() default Constraints.RequiredValidator.message;

    String minDate() default "";
    String maxDate() default "";
}
