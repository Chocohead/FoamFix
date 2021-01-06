package pl.asie.foamfix.mixin;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target(METHOD)
public @interface NoCast {
	int changing() default -1;

	String to() default "";

	boolean nextLabel() default true;
}