package jason.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If multiple policies are to be allowed the class should be annotated with
 * <code>@AvailablePolicies({"policy1","policy2",...})</code>, where policy1, policy2, 
 * etc. are names chosen by the programmer, for insctance "customerX" or 
 * "versionY". 
 * When for a particular method different policies are in use the following 
 * notation is used:
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
 * When annotations are used without the {@link Policy} annotation then that 
 * annotations holds in all the policy versions.
 * @author R. Brinkman <r.brinkman@cs.ru.nl>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AvailablePolicies {
	String[] value();
}
