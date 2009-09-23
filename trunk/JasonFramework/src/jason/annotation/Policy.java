package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A <code>@Policy</code> should appear within a <code>@Policies</code> 
 * annotation like this:
 * <pre><code>
 *   &#64;Policies({
 *     &#64;Policy(name="MyPolicyName",
 *             confidential=&#64;Confidential(encryptedBy="MyRole")),
 *     &#64;Policy(name="MyOtherPolicyName",
 *             authentic=&#64;Authentic(signedBy="MyOtherRole"))
 *   })
 *   public void someMethod() {
 *   }
 * </code></pre>
 * 
 * @author R. Brinkman <r.brinkman@cs.ru.nl>
 */
@Documented
@Target({ElementType.CONSTRUCTOR,
         ElementType.FIELD,
         ElementType.LOCAL_VARIABLE,
         ElementType.METHOD,
         ElementType.PARAMETER,
         ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Policy {
	/**
	 * The name of this policy. It should be chosen from the list of 
	 * {@link AvailablePolicies}.
	 */
	String name();
	AccessibleTo accessibleTo();
	Authentic authentic();
	Confidential confidential();
	Fresh fresh();
	Integrity integrity();
	ProvideRole provideRole();
	Logged logged();
	Roles roles();
	ServiceRoles serviceRoles();
}
