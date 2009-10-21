package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Container annotation to list all the possible <code>@Policy</code> 
 * annotations.
 * 
 * @author R. Brinkman <r.brinkman@cs.ru.nl>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Policies {
	Policy[] value();
}
