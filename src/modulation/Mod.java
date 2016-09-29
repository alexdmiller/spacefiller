package modulation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Mod {
	float min() default 0;
	float max() default 1;
	float defaultValue() default 0;
	String address() default "";
}