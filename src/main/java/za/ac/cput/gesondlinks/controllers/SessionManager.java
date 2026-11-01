
package za.ac.cput.gesondlinks.controllers;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Fezeka
 */
public class SessionManager {
 
    private static SessionManager instance;
    private Map<String, Object> sessionData;
    private boolean isLoggedIn;
    private String username;
    private String role;
    private String fullName;

    private SessionManager() {
        sessionData = new HashMap<>();
        isLoggedIn = false;
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(String username, String role, String fullName) {
        this.isLoggedIn = true;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
    }

    public void logout() {
        this.isLoggedIn = false;
        this.username = null;
        this.role = null;
        this.fullName = null;
        sessionData.clear();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setAttribute(String key, Object value) {
        sessionData.put(key, value);
    }

    public Object getAttribute(String key) {
        return sessionData.get(key);
    }

    public void removeAttribute(String key) {
        sessionData.remove(key);
    }

    public boolean hasRole(String... allowedRoles) {
        if (!isLoggedIn || role == null) return false;
        for (String allowedRole : allowedRoles) {
            if (role.equalsIgnoreCase(allowedRole)) {
                return true;
            }
        }
        return false;
    }
}  

