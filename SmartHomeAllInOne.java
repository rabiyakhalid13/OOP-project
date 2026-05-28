import java.util.*;
import java.io.*;

// ================= DEVICE SPEC =================
class DeviceSpec implements Serializable {
    private String manufacturer;
    private String model;
    private int wattage;
    private int year;

    DeviceSpec(String m, String model, int w, int y) {
        this.manufacturer = m;
        this.model = model;
        this.wattage = w;
        this.year = y;
    }

    public String toString() {
        return manufacturer + " " + model + " | " + wattage + "W | " + year;
    }
}

// ================= SMART DEVICE =================
abstract class SmartDevice implements Serializable {
    private String id, name, room;
    private boolean isOn;
    private DeviceSpec spec;

    public SmartDevice(String id, String name, String room,
            String m, String model, int w, int y) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.spec = new DeviceSpec(m, model, w, y);
    }

    public void turnOn() {
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
    }

    public boolean isOn() {
        return isOn;
    }

    public String getDeviceId() {
        return id;
    }

    public String getRoomName() {
        return room;
    }

    public DeviceSpec getSpec() {
        return spec;
    }

    public abstract String getStatus();
}

// ================= DEVICES =================
class Light extends SmartDevice {
    private int brightness = 50;

    public Light(String id, String name, String room) {
        super(id, name, room, "Generic", "LT", 10, 2023);
    }

    public String getStatus() {
        return "Light " + getDeviceId() +
                " [" + (isOn() ? "ON" : "OFF") + "] Brightness=" + brightness;
    }
}

class ACUnit extends SmartDevice {
    private int temp = 24;

    public ACUnit(String id, String name, String room) {
        super(id, name, room, "AC", "X", 1500, 2022);
    }

    public String getStatus() {
        return "AC " + getDeviceId() +
                " [" + (isOn() ? "ON" : "OFF") + "] Temp=" + temp;
    }
}

class Fan extends SmartDevice {
    private int speed = 1;

    public Fan(String id, String name, String room) {
        super(id, name, room, "Fan", "F", 75, 2023);
    }

    public String getStatus() {
        return "Fan " + getDeviceId() +
                " [" + (isOn() ? "ON" : "OFF") + "] Speed=" + speed;
    }
}

// ================= ROOM =================
class Room implements Serializable {
    private String name;
    private List<SmartDevice> devices = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public void addDevice(SmartDevice d) {
        devices.add(d);
    }

    public String getRoomName() {
        return name;
    }

    public List<SmartDevice> getDevices() {
        return devices;
    }
}

// ================= HOUSE =================
class House implements Serializable {
    private List<Room> rooms = new ArrayList<>();

    public Room createRoom(String name) {
        Room r = new Room(name);
        rooms.add(r);
        return r;
    }

    public Room findRoom(String name) {
        for (Room r : rooms)
            if (r.getRoomName().equals(name))
                return r;
        return null;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    // IMPORTANT METHOD (you had error before)
    public void printFullStatus() {
        for (Room r : rooms) {
            System.out.println("Room: " + r.getRoomName());

            for (SmartDevice d : r.getDevices()) {
                System.out.println("  " + d.getStatus());
                System.out.println("    Spec: " + d.getSpec());
            }
        }
    }
}

// ================= USERS =================
abstract class User {
    String username, password;

    public User(String u, String p) {
        username = u;
        password = p;
    }

    public boolean checkPassword(String p) {
        return password.equals(p);
    }

    public String getUsername() {
        return username;
    }

    public abstract boolean canEdit();
}

class Admin extends User {
    public Admin(String u, String p) {
        super(u, p);
    }

    public boolean canEdit() {
        return true;
    }
}

class Guest extends User {
    public Guest(String u, String p) {
        super(u, p);
    }

    public boolean canEdit() {
        return false;
    }
}

// ================= USER MANAGER =================
class UserManager {
    List<User> users = new ArrayList<>();

    public void addUser(User u) {
        users.add(u);
    }

    public User login(String u, String p) {
        for (User user : users)
            if (user.getUsername().equals(u) && user.checkPassword(p))
                return user;

        throw new RuntimeException("Login failed");
    }
}

// ================= LOGGER =================
class EventLogger {
    public static void log(String msg) {
        try (FileWriter fw = new FileWriter("log.txt", true)) {
            fw.write(msg + "\n");
        } catch (Exception e) {
        }
    }
}

// ================= MAIN =================
public class SmartHomeAllInOne {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        UserManager um = new UserManager();
        um.addUser(new Admin("admin", "admin123"));
        um.addUser(new Guest("guest", "guest123"));

        House house = new House();
        User current = null;

        while (true) {
            System.out.println("\n1.Login 2.AddRoom 3.AddDevice 4.Show 0.Exit");
            int ch = sc.nextInt();
            sc.nextLine();

            try {
                switch (ch) {

                    case 1:
                        System.out.print("User:");
                        String u = sc.nextLine();
                        System.out.print("Pass:");
                        String p = sc.nextLine();
                        current = um.login(u, p);
                        System.out.println("Logged in");
                        break;

                    case 2:
                        if (current == null || !current.canEdit())
                            break;
                        System.out.print("Room:");
                        house.createRoom(sc.nextLine());
                        break;

                    case 3:
                        if (current == null || !current.canEdit())
                            break;

                        System.out.print("Type 1=Light 2=AC 3=Fan:");
                        int t = sc.nextInt();
                        sc.nextLine();

                        System.out.print("ID:");
                        String id = sc.nextLine();
                        System.out.print("Name:");
                        String name = sc.nextLine();
                        System.out.print("Room:");
                        String r = sc.nextLine();

                        Room room = house.findRoom(r);
                        if (room == null) {
                            System.out.println("Room not found");
                            break;
                        }

                        if (t == 1)
                            room.addDevice(new Light(id, name, r));
                        if (t == 2)
                            room.addDevice(new ACUnit(id, name, r));
                        if (t == 3)
                            room.addDevice(new Fan(id, name, r));

                        break;

                    case 4:
                        house.printFullStatus();
                        break;

                    case 0:
                        return;
                }

            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }
}