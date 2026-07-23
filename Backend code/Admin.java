public class Admin extends User {

    // Admin-specific field
    private int accessLevel = 2;

    // Constructor 
    public Admin(String username, String password) {
        super(username, password); // calls User constructor with validation
    }

    /**
     * POLYMORPHISM: Admin's version of canEdit() returns true
     * Guest's version returns false - same method, different behavior
     */
    @Override
    public boolean canEdit() {
        return true; // Admin can always edit
    }

    /**
     * Admin can add a room to the house
     * EXCEPTION HANDLING: null room check
     */
    public void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null. Please provide a valid room.");
        }
        System.out.println("[ADMIN] Room added by " + getUsername() + ": " + room.getRoomName());
    }

    /**
     * Admin can manage users
     */
    public void manageUsers() {
        if (!canEdit()) {
            throw new SecurityException("Access denied: Only admins can manage users.");
        }
        System.out.println("[ADMIN] " + getUsername() + " is managing users. Access Level: " + accessLevel);
    }

    public int getAccessLevel() {
        return accessLevel;
    }

   //Polymorphism
    @Override
    public String toString() {
        return "Admin{username='" + getUsername() + "', accessLevel=" + accessLevel + ", canEdit=" + canEdit() + "}";
    }
}
