import java.io.*;

public class DataManager {

    private static final String FILE = "house_data.txt";

    public static void saveHouse(House house) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {

            pw.println(house.getHouseName());

            for (Room r : house.getRooms()) {
                pw.println("ROOM:" + r.getRoomName());

                for (SmartDevice d : r.getDevices()) {
                    pw.println(d.getClass().getSimpleName() + ","
                            + d.getDeviceId() + ","
                            + d.getName() + ","
                            + d.getRoomName());
                }
            }

            EventLogger.log("House saved to file");

        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public static House loadHouse() {

        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {

            String houseName = br.readLine();
            House house = new House(houseName);

            Room current = null;
            String line;

            while ((line = br.readLine()) != null) {

                if (line.startsWith("ROOM:")) {
                    current = house.createRoom(line.substring(5));
                } else {

                    String[] parts = line.split(",");

                    String type = parts[0];
                    String id   = parts[1];
                    String name = parts[2];
                    String room = parts[3];

                    // ✅ FIX: 7-argument constructors used with hardcoded default values
                    // because Light / ACUnit / Fan only have the full 7-arg constructor.
                    if (type.equals("Light")) {
                        Light l = new Light(id, name, room, "Generic", "N/A", 0, 0);
                        current.addDevice(l);
                    }

                    if (type.equals("ACUnit")) {
                        ACUnit a = new ACUnit(id, name, room, "Generic", "N/A", 0, 0);
                        current.addDevice(a);
                    }

                    if (type.equals("Fan")) {
                        Fan f = new Fan(id, name, room, "Generic", "N/A", 0, 0);
                        current.addDevice(f);
                    }
                }
            }

            EventLogger.log("House loaded from file");
            return house;

        } catch (Exception e) {
            System.out.println("Load failed: " + e.getMessage());
        }

        return null;
    }
}