public abstract class User {

    // ENCAPSULATION: private fields 
    private String username;
    private String password;

    // Constructor
    public User(String username, String password) {
        // EXCEPTION HANDLING
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        this.username = username;
        this.password = password;
    }

    // Getter for username 
    public String getUsername() {
        return username;
    }

    // Password check 
    public boolean checkPassword(String inputPassword) {
        if (inputPassword == null) {
            throw new IllegalArgumentException("Password input cannot be null.");
        }
        return this.password.equals(inputPassword);
    }

    /**
     * ABSTRACTION: Abstract method - every subclass MUST implement this
     * This is polymorphism setup - each subclass behaves differently
     */
    public abstract boolean canEdit();

    /**
     * POLYMORPHISM: toString() overridden - each subclass will give different output
     */
    @Override
    public abstract String toString();
}
