package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that some checksum should be included.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
@Deprecated
@Documented
@Target({ElementType.METHOD,
         ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface Integrity {
}
