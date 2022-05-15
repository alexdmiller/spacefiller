package spacefiller.patchbay.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Smooth {
  float value() default 0.9f;
}