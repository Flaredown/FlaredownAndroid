package com.flaredown.flaredownApp.API.Sync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for marking fields in classes that are subclasses to {@link ServerUpdate} as a parameter.
 * Also giving the ability to override the key value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WebParameter {
    String keyValue() default "";
}
