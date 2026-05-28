import java.util.ArrayList;

public class UserManager {

    // ENCAPSULATION: private fields
    private ArrayList<User> users;
    private User loggedInUser; 

    // Constructor
    public UserManager() {
        this.users = new ArrayList<>();
        this.loggedInUser = null;
    }

    /**
     * Login method
     * EXCEPTION HANDLING: throws exception for wrong credentials or already logged in
     * POLYMORPHISM: User reference can hold Admin or Guest object
     */
    public User login(String username, String password) {
        // Check if someone is already logged in
        if (loggedInUser != null) {
            throw new IllegalStateException(
                "A user is already logged in: '" + loggedInUser.getUsername() + "'. Please logout first."
            );
        }

        // Search for user
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (user.checkPassword(password)) {
                    loggedInUser = user;
                    System.out.println("[LOGIN SUCCESS] Welcome, " + username + "! " + user.toString());
                    return user;
                } else {
                    // Wrong password
                    throw new SecurityException("Incorrect password for user: '" + username + "'.");
                }
            }
        }

        // Username not found
        throw new IllegalArgumentException("User not found: '" + username + "'. Please register first.");
    }

    /**
     * Logout current user
     * EXCEPTION HANDLING: if no one is logged in
     */
    public void logout() {
        if (loggedInUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        System.out.println("[LOGOUT] Goodbye, " + loggedInUser.getUsername() + "!");
        loggedInUser = null;
    }

    /**
     * Add a new user to the system
     * EXCEPTION HANDLING: null check and duplicate check
     */
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot add a null user to the system.");
        }

        // Check for duplicate username
        for (User existing : users) {
            if (existing.getUsername().equals(user.getUsername())) {
                throw new IllegalArgumentException(
                    "User with username '" + user.getUsername() + "' already exists."
                );
            }
        }

        users.add(user);
        System.out.println("[USER ADDED] " + user.getUsername() + " added to the system.");
    }

    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    /**
     * Get currently logged in user
     * EXCEPTION HANDLING: if no one logged in
     */
    public User getLoggedInUser() {
        if (loggedInUser == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }
        return loggedInUser;
    }

    /**
     * Display all users (POLYMORPHISM: toString() of each user called - Admin or Guest)
     */
    public void listAllUsers() {
        if (users.isEmpty()) {
            System.out.println("[USER LIST] No users registered in the system.");
            return;
        }
        System.out.println("[USER LIST] All registered users:");
        for (int i = 0; i < users.size(); i++) {
            // POLYMORPHISM: calls Admin.toString() or Guest.toString() automatically
            System.out.println("  " + (i + 1) + ". " + users.get(i).toString());
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
