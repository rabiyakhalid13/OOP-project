public class Guest extends User {

    private int accessLevel = 1;

    // Constructor
    public Guest(String username, String password) {
        super(username, password); // parent constructor handles validation
    }

    /**
     * POLYMORPHISM: Guest's canEdit() returns false
     * Same method name as Admin.canEdit() but completely different behavior
     * This is RUNTIME POLYMORPHISM (method overriding)
     */
    @Override
    public boolean canEdit() {
        return false; // Guests cannot edit
    }

    /**
     * Guest can only view status - no editing allowed
     */
    public void viewStatus() {
        System.out.println("[GUEST] " + getUsername() + " is viewing smart home status. (Read-only access)");
    }

    /**
     * Guest requests control but cannot directly control
     * EXCEPTION HANDLING: throws exception if guest tries unauthorized action
     */
    public void requestControl() {
        if (!canEdit()) {
            // Custom exception thrown here
            throw new SecurityException(
                "Access Denied: Guest '" + getUsername() + "' cannot control devices. Please contact an Admin."
            );
        }
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    /**
     * POLYMORPHISM: Guest's toString() - different from Admin's toString()
     */
    @Override
    public String toString() {
        return "Guest{username='" + getUsername() + "', accessLevel=" + accessLevel + ", canEdit=" + canEdit() + "}";
    }
}
