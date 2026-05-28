import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private List<String> tasks = new ArrayList<>();

    public void addTask(String task) {
        tasks.add(task);
        EventLogger.log("Task added: " + task);
    }

    public void run(House house) {
        System.out.println("=== Running Scheduled Tasks ===");

        for (String task : tasks) {
            System.out.println("Executing: " + task);
            EventLogger.log("Executed task: " + task);
        }
    }
}