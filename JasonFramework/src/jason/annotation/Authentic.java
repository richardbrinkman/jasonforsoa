package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.CONSTRUCTOR,
         ElementType.FIELD,
         ElementType.LOCAL_VARIABLE,
         ElementType.METHOD,
         ElementType.PARAMETER,
         ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Authentic {
	String signedBy() default "";
	Remember rememberSignature() default Remember.Never;
}
