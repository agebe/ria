package org.rescript.p1;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

@Documented
@Retention(RUNTIME)
public @interface NotEmpty {
    String message() default "{org.hibernate.validator.constraints.NotEmpty.message}";

    Class<?>[] groups() default { };


    /**
     * Defines several {@code @NotEmpty} annotations on the same element.
     */
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        NotEmpty[] value();
    }
}