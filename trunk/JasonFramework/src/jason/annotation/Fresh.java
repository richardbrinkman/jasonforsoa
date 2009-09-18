package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to prevent replay attacks. 
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
@Documented
@Target({ElementType.PARAMETER,
         ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Fresh {
	/**
	 * URL that should be included besides the timestamp
	 */
	String url();
	Check[] check() default Check.TIMESTAMP;
}
