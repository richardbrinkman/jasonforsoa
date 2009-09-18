package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies whether the parameter or method result should be encrypted. If no
 * policies attribute is given, it is assumed that the referenced parameter or 
 * method result is encrypted with the same role name key in all the different
 * policies. 
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface Confidential {
	/**
	 * Role name that is used to find the encryption key.
	 */
	String encryptedFor() default "";
}
