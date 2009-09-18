package jason.test.framework;

import java.security.Permission;
import java.util.HashSet;

/**
 * Standard SecurityManager with the addition that all permission checks are 
 * logged.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class LoggingSecurityManager extends SecurityManager {
	private final HashSet<Permission> permissions = new HashSet<Permission>();

	@Override
	public void checkPermission(Permission perm) {
		addPermission(perm);
		super.checkPermission(perm);
	}
	
	@Override
	public void checkPermission(Permission perm, Object context) {
		addPermission(perm);
		super.checkPermission(perm, context);
	}
	
	private void addPermission(Permission newPermission) {
		boolean shouldAdd = true;
		HashSet<Permission> toBeDeleted = new HashSet<Permission>();
		for (Permission permission : permissions) 
			if (permission.implies(newPermission)) {
				if (!permission.equals(newPermission))
					System.err.println(newPermission.toString() + " implied by " + permission.toString());
				shouldAdd = false;
			}
			else if (newPermission.implies(permission)) {
				System.err.println(newPermission.toString() + " supersedes " + permission.toString());
				toBeDeleted.add(permission);
			}
		permissions.removeAll(toBeDeleted);
		if (shouldAdd) {
			permissions.add(newPermission);
			System.err.println("new: " + newPermission.toString());
		}
	}
	
	public void printAllPermissions() {
		for (Permission permission : permissions)
			System.err.println(permission.toString());
	}
}
