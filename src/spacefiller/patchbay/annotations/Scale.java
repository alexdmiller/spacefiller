package spacefiller.patchbay.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Scale {
  float min() default 0f;
  float max() default 1f;
}