import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class EventLogger {

    private static final String FILE_NAME = "events.txt";

    public static void log(String message) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(LocalDateTime.now() + " - " + message + "\n");
        } catch (IOException e) {
            System.out.println("Logging failed: " + e.getMessage());
        }
    }
}