import java.util.ArrayList;
import java.util.List;

public class Room {

    private final String roomName;

    // AGGREGATION — we store references, we did not create these devices.
    private final List<SmartDevice> devices;

    public Room(String roomName) {
        this.roomName = roomName;
        this.devices  = new ArrayList<>();
    }

    /**
     * AGGREGATION in action: the device was created outside this room
     * and is being associated with it. The device survives if the room is removed.
     */
    public void addDevice(SmartDevice device) {
        device.setRoomName(roomName);
        devices.add(device);
        System.out.println("  [Aggregation] Device \"" + device.getName()
                + "\" associated with room \"" + roomName + "\"");
    }

    /**
     * Disassociates a device from this room.
     * The device object is not destroyed — it could be re-added elsewhere.
     */
    public void removeDevice(String deviceId) {
        SmartDevice target = findDeviceById(deviceId);
        if (target != null) {
            devices.remove(target);
            target.setRoomName("Unassigned"); // device still lives, just has no room
            System.out.println("  [Aggregation] Device [" + deviceId
                    + "] disassociated from \"" + roomName + "\" — still exists as Unassigned");
        } else {
            System.out.println("WARNING: Device [" + deviceId + "] not in room: " + roomName);
        }
    }

    public void turnOffAll() {
        System.out.println("--- Turning off all in: " + roomName + " ---");
        for (SmartDevice d : devices) d.turnOff();
    }

    public SmartDevice findDeviceById(String deviceId) {
        for (SmartDevice d : devices)
            if (d.getDeviceId().equals(deviceId)) return d;
        return null;
    }

    public List<SmartDevice> getDevices() { return devices; }
    public String             getRoomName() { return roomName; }

    public void printStatus() {
        System.out.println("=== Room: " + roomName + " (" + devices.size() + " device(s)) ===");
        for (SmartDevice d : devices) System.out.println("  " + d.getStatus());
        if (devices.isEmpty()) System.out.println("  (empty)");
    }
}