import java.util.ArrayList;
import java.util.List;

public class House {

    private final String houseName;

    // COMPOSITION — House owns these rooms entirely.
    // Rooms are created by House, not passed in.
    private final List<Room> rooms;

    public House(String houseName) {
        this.houseName = houseName;
        this.rooms     = new ArrayList<>();
    }

    /**
     * COMPOSITION: House creates the Room itself.
     * The caller gets back a reference to work with,
     * but the Room's lifecycle is controlled by this House.
     */
    public Room createRoom(String roomName) {
        Room room = new Room(roomName); // House creates it
        rooms.add(room);
        System.out.println("  [Composition] House \"" + houseName
                + "\" created and owns Room \"" + roomName + "\"");
        return room;
    }

    /**
     * Destroying a room also loses all device associations in it.
     * The devices themselves (aggregation) still exist in memory.
     */
    public void destroyRoom(String roomName) {
        Room target = findRoomByName(roomName);
        if (target != null) {
            // Before removing, disassociate all devices from this room
            for (SmartDevice d : new ArrayList<>(target.getDevices())) {
                d.setRoomName("Unassigned");
            }
            rooms.remove(target);
            System.out.println("  [Composition] Room \"" + roomName
                    + "\" destroyed — its devices are now Unassigned");
        }
    }

    /** Global search — delegates down to each Room. */
    public SmartDevice findDeviceById(String deviceId) {
        for (Room room : rooms) {
            SmartDevice d = room.findDeviceById(deviceId);
            if (d != null) return d;
        }
        System.out.println("WARNING: Device [" + deviceId + "] not found.");
        return null;
    }

    public Room findRoomByName(String name) {
        for (Room r : rooms)
            if (r.getRoomName().equals(name)) return r;
        return null;
    }

    public List<Room> getRooms()    { return rooms; }
    public String     getHouseName(){ return houseName; }

    /** Total wattage of all ON devices — uses composition chain: House→Room→Device→Spec */
    public int getTotalWattageOn() {
        int total = 0;
        for (Room room : rooms)
            for (SmartDevice d : room.getDevices())
                if (d.isOn()) total += d.getSpec().getWattage();
        return total;
    }

    public void printFullStatus() {
        System.out.println("        ");
        System.out.println("  HOUSE: " + houseName + " | Rooms: " + rooms.size());
        System.out.println("  Total wattage (ON devices): " + getTotalWattageOn() + "W");
        System.out.println("        ");
        for (Room r : rooms) r.printStatus();
    }
}
