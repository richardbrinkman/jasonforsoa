package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All accesses to methods that are annotated with the <code>@Logged</code> 
 * annotation will appear in the log files.
 * 
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Logged {

}
