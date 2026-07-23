import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ================= DEVICE SPEC =================
class DeviceSpec implements Serializable {
    private String manufacturer, model;
    private int wattage, year;

    DeviceSpec(String m, String model, int w, int y) {
        manufacturer = m;
        this.model = model;
        wattage = w;
        year = y;
    }

    public int getWattage() {
        return wattage;
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

    public SmartDevice(String id, String name, String room, String m, String model, int w, int y) {
        this.id = id;
        this.name = name;
        this.room = room;
        spec = new DeviceSpec(m, model, w, y);
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

    public String getDeviceName() {
        return name;
    }

    public String getRoomName() {
        return room;
    }

    public void setRoomName(String r) {
        room = r;
    }

    public DeviceSpec getSpec() {
        return spec;
    }

    public abstract String getStatus();
}

// ================= LIGHT =================
class Light extends SmartDevice {
    private int brightness = 50;

    public Light(String id, String name, String room) {
        super(id, name, room, "Philips", "LT-X", 10, 2023);
    }

    public void setBrightness(int b) {
        if (b >= 0 && b <= 100)
            brightness = b;
    }

    public int getBrightness() {
        return brightness;
    }

    public String getStatus() {
        return "Brightness=" + brightness + "%";
    }
}

// ================= AC =================
class ACUnit extends SmartDevice {
    private int temp = 24;

    public ACUnit(String id, String name, String room) {
        super(id, name, room, "Dawlance", "AC-Pro", 1500, 2022);
    }

    public void setTemp(int t) {
        if (t >= 16 && t <= 30)
            temp = t;
    }

    public int getTemp() {
        return temp;
    }

    public String getStatus() {
        return "Temp=" + temp + "°C";
    }
}

// ================= FAN =================
class Fan extends SmartDevice {
    private int speed = 1;

    public Fan(String id, String name, String room) {
        super(id, name, room, "Usha", "FAN-T3", 75, 2023);
    }

    public void setSpeed(int s) {
        if (s >= 1 && s <= 3)
            speed = s;
    }

    public int getSpeed() {
        return speed;
    }

    public String getStatus() {
        return "Speed=" + speed + "/3";
    }
}

// ================= ROOM =================
class Room implements Serializable {
    private String name;
    private java.util.List<SmartDevice> devices = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public void addDevice(SmartDevice d) {
        devices.add(d);
    }

    public void removeDevice(SmartDevice d) {
        devices.remove(d);
    }

    public String getRoomName() {
        return name;
    }

    public java.util.List<SmartDevice> getDevices() {
        return devices;
    }

    public int getTotalWattage() {
        int total = 0;
        for (SmartDevice d : devices)
            if (d.isOn())
                total += d.getSpec().getWattage();
        return total;
    }
}

// ================= HOUSE =================
class House implements Serializable {
    private String houseName = "My Smart Home";
    private java.util.List<Room> rooms = new ArrayList<>();

    public House() {
    }

    public House(String name) {
        this.houseName = name;
    }

    public String getHouseName() {
        return houseName;
    }

    public Room createRoom(String name) {
        Room r = new Room(name);
        rooms.add(r);
        return r;
    }

    public void removeRoom(Room r) {
        rooms.remove(r);
    }

    public Room findRoom(String name) {
        for (Room r : rooms)
            if (r.getRoomName().equals(name))
                return r;
        return null;
    }

    public java.util.List<Room> getRooms() {
        return rooms;
    }

    public int getTotalWattageOn() {
        int t = 0;
        for (Room r : rooms)
            t += r.getTotalWattage();
        return t;
    }

    public int getTotalDevices() {
        int t = 0;
        for (Room r : rooms)
            t += r.getDevices().size();
        return t;
    }

    public int getOnDevicesCount() {
        int t = 0;
        for (Room r : rooms)
            for (SmartDevice d : r.getDevices())
                if (d.isOn())
                    t++;
        return t;
    }
}

// ================= AUTOMATION RULE =================
class AutomationRule implements Serializable {
    private String trigger, targetDevice, action;
    private boolean active = true;

    public AutomationRule(String trigger, String targetDevice, String action) {
        this.trigger = trigger;
        this.targetDevice = targetDevice;
        this.action = action;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public String getAction() {
        return action;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void evaluate(String condition, House house) {
        if (!active || !condition.equalsIgnoreCase(trigger))
            return;
        for (Room r : house.getRooms()) {
            for (SmartDevice d : r.getDevices()) {
                if (d.getDeviceName().equalsIgnoreCase(targetDevice)) {
                    if (action.equalsIgnoreCase("TURN_ON"))
                        d.turnOn();
                    if (action.equalsIgnoreCase("TURN_OFF"))
                        d.turnOff();
                    EventLogger.log("Rule triggered: " + trigger + " → " + action + " on " + targetDevice);
                }
            }
        }
    }
}

// ================= USER =================
abstract class User implements Serializable {
    protected String username, password;

    public User(String u, String p) {
        if (u == null || u.isEmpty())
            throw new IllegalArgumentException("Username cannot be empty.");
        if (p == null || p.length() < 4)
            throw new IllegalArgumentException("Password must be at least 4 characters.");
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

    public abstract String getRole();
}

class Admin extends User {
    public Admin(String u, String p) {
        super(u, p);
    }

    public boolean canEdit() {
        return true;
    }

    public String getRole() {
        return "Admin";
    }
}

class Guest extends User {
    public Guest(String u, String p) {
        super(u, p);
    }

    public boolean canEdit() {
        return false;
    }

    public String getRole() {
        return "Guest";
    }
}

// ================= USER MANAGER =================
class UserManager implements Serializable {
    private java.util.List<User> users = new ArrayList<>();
    private User loggedIn = null;

    public UserManager() {
    }

    public void addUser(User u) {
        for (User ex : users)
            if (ex.getUsername().equals(u.getUsername()))
                throw new IllegalArgumentException("Username '" + u.getUsername() + "' already exists.");
        users.add(u);
    }

    public User login(String u, String p) {
        if (loggedIn != null) {
            loggedIn = null;
        } // auto-logout previous
        for (User user : users)
            if (user.getUsername().equals(u) && user.checkPassword(p)) {
                loggedIn = user;
                return user;
            }
        throw new RuntimeException("Invalid username or password.");
    }

    public void logout() {
        loggedIn = null;
    }

    public java.util.List<User> getUsers() {
        return users;
    }
}

// ================= EVENT LOGGER =================
class EventLogger {
    private static final String FILE = "events.txt";

    public static void log(String msg) {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + " | " + msg + "\n");
        } catch (Exception e) {
        }
    }
}

// ================= DATA MANAGER =================
class DataManager {
    private static final String FILE = "house_data.txt";

    public static void save(House house) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println("HOUSE:" + house.getHouseName());
            for (Room r : house.getRooms()) {
                pw.println("ROOM:" + r.getRoomName());
                for (SmartDevice d : r.getDevices()) {
                    pw.println(d.getClass().getSimpleName() + ","
                            + d.getDeviceId() + "," + d.getDeviceName()
                            + "," + r.getRoomName() + "," + d.isOn());
                }
            }
            EventLogger.log("House data saved.");
        } catch (Exception e) {
            EventLogger.log("Save failed: " + e.getMessage());
        }
    }

    public static House load() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line = br.readLine();
            String houseName = line != null && line.startsWith("HOUSE:") ? line.substring(6) : "My Smart Home";
            House house = new House(houseName);
            Room current = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ROOM:")) {
                    current = house.createRoom(line.substring(5));
                } else if (current != null) {
                    String[] p = line.split(",");
                    SmartDevice d = null;
                    if (p[0].equals("Light"))
                        d = new Light(p[1], p[2], p[3]);
                    if (p[0].equals("ACUnit"))
                        d = new ACUnit(p[1], p[2], p[3]);
                    if (p[0].equals("Fan"))
                        d = new Fan(p[1], p[2], p[3]);
                    if (d != null) {
                        if (p.length > 4 && p[4].equals("true"))
                            d.turnOn();
                        current.addDevice(d);
                    }
                }
            }
            EventLogger.log("House data loaded.");
            return house;
        } catch (Exception e) {
            return null;
        }
    }
}

// ================= SCHEDULER =================
class Scheduler implements Serializable {
    private java.util.List<String[]> tasks = new ArrayList<>(); // [time, deviceName, action]

    public void addTask(String time, String deviceName, String action) {
        tasks.add(new String[] { time, deviceName, action });
        EventLogger.log("Task scheduled: " + action + " on " + deviceName + " at " + time);
    }

    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size())
            tasks.remove(index);
    }

    public java.util.List<String[]> getTasks() {
        return tasks;
    }

    public void runAll(House house) {
        for (String[] task : tasks) {
            String deviceName = task[1], action = task[2];
            for (Room r : house.getRooms())
                for (SmartDevice d : r.getDevices())
                    if (d.getDeviceName().equalsIgnoreCase(deviceName)) {
                        if (action.equalsIgnoreCase("TURN_ON"))
                            d.turnOn();
                        if (action.equalsIgnoreCase("TURN_OFF"))
                            d.turnOff();
                        EventLogger.log("Scheduled task executed: " + action + " on " + deviceName);
                    }
        }
    }
}

// ==================== COLORS & THEME ====================
class Theme {
    static final Color BG_DARK = new Color(10, 15, 30);
    static final Color BG_CARD = new Color(20, 28, 50);
    static final Color BG_SIDEBAR = new Color(15, 20, 40);
    static final Color ACCENT = new Color(99, 179, 255);
    static final Color ACCENT2 = new Color(72, 219, 155);
    static final Color DANGER = new Color(255, 90, 90);
    static final Color WARNING = new Color(255, 190, 50);
    static final Color TEXT_WHITE = new Color(240, 245, 255);
    static final Color TEXT_MUTED = new Color(120, 135, 165);
    static final Color BORDER = new Color(40, 55, 85);
    static final Color ON_GREEN = new Color(50, 210, 130);
    static final Color OFF_GRAY = new Color(80, 95, 120);

    static Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    static Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    static Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    static Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    static Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);
}

// ==================== ROUNDED BUTTON ====================
class RoundedButton extends JButton {
    private Color bg, hover;
    private boolean hovered = false;

    public RoundedButton(String text, Color bg) {
        super(text);
        this.bg = bg;
        this.hover = bg.brighter();
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(Theme.TEXT_WHITE);
        setFont(Theme.FONT_BODY);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? hover : bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g);
        g2.dispose();
    }
}

// ==================== CARD PANEL ====================
class CardPanel extends JPanel {
    public CardPanel() {
        setBackground(Theme.BG_CARD);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.setColor(Theme.BORDER);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();
    }
}

// ==================== MAIN GUI ====================
public class smarthomee {

    private static House house = new House("My Smart Home");
    private static UserManager userManager = new UserManager();
    private static User currentUser;
    private static java.util.List<AutomationRule> rules = new ArrayList<>();
    private static Scheduler scheduler = new Scheduler();

    private static JFrame frame;
    private static CardLayout cardLayout = new CardLayout();
    private static JPanel mainPanel = new JPanel(cardLayout);

    // Dashboard panels (refreshable)
    private static JPanel devicesPanel;
    private static JPanel statsBar;
    private static JLabel statDevices, statOn, statWatts, userLabel;

    public static void main(String[] args) {
        // Default users
        userManager.addUser(new Admin("admin", "admin123"));
        userManager.addUser(new Guest("guest", "guest123"));

        // Default data
        Room living = house.createRoom("Living Room");
        living.addDevice(new Light("L1", "Main Light", "Living Room"));
        living.addDevice(new Fan("F1", "Ceiling Fan", "Living Room"));
        Room bed = house.createRoom("Bedroom");
        bed.addDevice(new Light("L2", "Bed Light", "Bedroom"));
        bed.addDevice(new ACUnit("AC1", "Main AC", "Bedroom"));

        // Default automation rules
        rules.add(new AutomationRule("NIGHT_MODE", "Main Light", "TURN_OFF"));
        rules.add(new AutomationRule("AWAY_MODE", "Main AC", "TURN_OFF"));
        rules.add(new AutomationRule("MORNING_MODE", "Main Light", "TURN_ON"));

        UIManager.put("OptionPane.background", Theme.BG_CARD);
        UIManager.put("Panel.background", Theme.BG_CARD);

        frame = new JFrame("🏠 Smart Home Automation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 780);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(1000, 650));

        mainPanel.setBackground(Theme.BG_DARK);
        mainPanel.add(buildLoginScreen(), "login");
        mainPanel.add(buildDashboard(), "dashboard");

        frame.add(mainPanel);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "login");
    }

    // ===================== LOGIN SCREEN =====================
    private static JPanel buildLoginScreen() {
        JPanel root = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(5, 10, 25),
                        getWidth(), getHeight(), new Color(15, 30, 65));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 520));
        card.setBackground(new Color(18, 25, 48));

        // Title
        JLabel icon = new JLabel("🏠", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Smart Home", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Theme.TEXT_WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Login to your account", SwingConstants.CENTER);
        subtitle.setFont(Theme.FONT_SMALL);
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JTextField userField = styledField("Username");
        JPasswordField passField = new JPasswordField();
        stylePasswordField(passField, "Password");

        // Role toggle
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        rolePanel.setOpaque(false);
        rolePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel roleLabel = new JLabel("Register as:");
        roleLabel.setForeground(Theme.TEXT_MUTED);
        roleLabel.setFont(Theme.FONT_SMALL);
        JRadioButton adminRadio = new JRadioButton("Admin");
        JRadioButton guestRadio = new JRadioButton("Guest");
        adminRadio.setOpaque(false);
        guestRadio.setOpaque(false);
        adminRadio.setForeground(Theme.TEXT_WHITE);
        guestRadio.setForeground(Theme.TEXT_WHITE);
        adminRadio.setFont(Theme.FONT_SMALL);
        guestRadio.setFont(Theme.FONT_SMALL);
        guestRadio.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(adminRadio);
        bg.add(guestRadio);
        rolePanel.add(roleLabel);
        rolePanel.add(adminRadio);
        rolePanel.add(guestRadio);
        rolePanel.setVisible(false);

        // Message
        JLabel msgLabel = new JLabel(" ", SwingConstants.CENTER);
        msgLabel.setFont(Theme.FONT_SMALL);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        RoundedButton loginBtn = new RoundedButton("Login", Theme.ACCENT.darker());
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(340, 42));
        loginBtn.setPreferredSize(new Dimension(340, 42));

        RoundedButton toggleBtn = new RoundedButton("New user? Register", new Color(35, 50, 85));
        toggleBtn.setFont(Theme.FONT_SMALL);
        toggleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleBtn.setMaximumSize(new Dimension(340, 36));

        JLabel hint = new JLabel("Admin: admin/admin123  |  Guest: guest/guest123", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        hint.setForeground(new Color(70, 90, 130));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setVisible(false);

        // Register mode toggle
        final boolean[] registerMode = { false };
        toggleBtn.addActionListener(e -> {
            registerMode[0] = !registerMode[0];
            if (registerMode[0]) {
                subtitle.setText("Create a new account");
                loginBtn.setText("Create Account");
                toggleBtn.setText("Already have account? Login");
                rolePanel.setVisible(true);
                msgLabel.setText(" ");
            } else {
                subtitle.setText("Login to your account");
                loginBtn.setText("Login");
                toggleBtn.setText("New user? Register");
                rolePanel.setVisible(false);
                msgLabel.setText(" ");
            }
        });

        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) {
                msgLabel.setForeground(Theme.DANGER);
                msgLabel.setText("⚠ Please fill all fields");
                return;
            }
            if (registerMode[0]) {
                try {
                    User nu = adminRadio.isSelected() ? new Admin(u, p) : new Guest(u, p);
                    userManager.addUser(nu);
                    msgLabel.setForeground(Theme.ACCENT2);
                    msgLabel.setText("✓ Registered! Now login.");
                    registerMode[0] = false;
                    subtitle.setText("Login to your account");
                    loginBtn.setText("Login");
                    toggleBtn.setText("New user? Register");
                    rolePanel.setVisible(false);
                } catch (Exception ex) {
                    msgLabel.setForeground(Theme.DANGER);
                    msgLabel.setText("✗ " + ex.getMessage());
                }
            } else {
                try {
                    // Try to load saved data first
                    House loaded = DataManager.load();
                    if (loaded != null)
                        house = loaded;

                    currentUser = userManager.login(u, p);
                    EventLogger.log("Login: " + u + " (" + currentUser.getRole() + ")");
                    refreshDashboard();
                    cardLayout.show(mainPanel, "dashboard");
                    userField.setText("");
                    passField.setText("");
                    msgLabel.setText(" ");
                } catch (Exception ex) {
                    msgLabel.setForeground(Theme.DANGER);
                    msgLabel.setText("✗ " + ex.getMessage());
                }
            }
        });

        // Layout
        int gap = 8;
        card.add(Box.createVerticalStrut(20));
        card.add(icon);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(centeredWrap(userField, 340));
        card.add(Box.createVerticalStrut(gap));
        card.add(centeredWrap(passField, 340));
        card.add(Box.createVerticalStrut(gap));
        card.add(rolePanel);
        card.add(Box.createVerticalStrut(gap));
        card.add(msgLabel);
        card.add(Box.createVerticalStrut(gap));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(gap));
        card.add(toggleBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(hint);
        card.add(Box.createVerticalStrut(15));

        root.add(card);
        return root;
    }

    // ===================== DASHBOARD =====================
    private static JPanel buildDashboard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_SIDEBAR);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        topBar.setPreferredSize(new Dimension(0, 55));

        JLabel homeLabel = new JLabel("🏠  Smart Home Dashboard");
        homeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        homeLabel.setForeground(Theme.TEXT_WHITE);

        userLabel = new JLabel("", SwingConstants.RIGHT);
        userLabel.setFont(Theme.FONT_SMALL);
        userLabel.setForeground(Theme.TEXT_MUTED);

        RoundedButton logoutBtn = new RoundedButton("Logout", Theme.DANGER.darker());
        logoutBtn.setFont(Theme.FONT_SMALL);
        logoutBtn.setPreferredSize(new Dimension(80, 30));
        logoutBtn.addActionListener(e -> {
            userManager.logout();
            EventLogger.log("Logout");
            cardLayout.show(mainPanel, "login");
        });

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);
        rightTop.add(userLabel);
        rightTop.add(logoutBtn);

        topBar.add(homeLabel, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        // STATS BAR
        statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        statsBar.setBackground(new Color(13, 18, 38));
        statsBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        statDevices = statLabel("📦 Devices: 0");
        statOn = statLabel("🟢 ON: 0");
        statWatts = statLabel("⚡ Total: 0W");
        statsBar.add(statDevices);
        statsBar.add(statOn);
        statsBar.add(statWatts);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(statsBar, BorderLayout.SOUTH);

        // TABS
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG_DARK);
        tabs.setForeground(Theme.TEXT_WHITE);
        tabs.setFont(Theme.FONT_BODY);

        devicesPanel = new JPanel();
        devicesPanel.setBackground(Theme.BG_DARK);

        tabs.addTab("🏠  Devices", wrapScroll(buildDevicesContent()));
        tabs.addTab("⚙  Manage", wrapScroll(buildManageContent(tabs)));
        tabs.addTab("⚡  Automation", wrapScroll(buildAutomationContent()));
        tabs.addTab("📅  Scheduler", wrapScroll(buildSchedulerContent()));
        tabs.addTab("👥  Users", wrapScroll(buildUsersContent()));
        tabs.addTab("📋  Event Log", wrapScroll(buildLogContent()));

        // Disable manage/users for guest
        tabs.addChangeListener(e -> {
            if (currentUser != null && !currentUser.canEdit()) {
                int sel = tabs.getSelectedIndex();
                if (sel == 1 || sel == 4) {
                    tabs.setSelectedIndex(0);
                    showMsg("⛔ Guest cannot access this tab.");
                }
            }
        });

        root.add(topSection, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        return root;
    }

    // ===================== DEVICES TAB =====================
    private static JPanel buildDevicesContent() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(Theme.BG_DARK);
        wrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (house.getRooms().isEmpty()) {
            JLabel empty = new JLabel("No rooms yet. Go to Manage tab to add rooms.", SwingConstants.CENTER);
            empty.setForeground(Theme.TEXT_MUTED);
            empty.setFont(Theme.FONT_BODY);
            wrap.add(empty);
            return wrap;
        }

        for (Room room : house.getRooms()) {
            wrap.add(buildRoomCard(room));
            wrap.add(Box.createVerticalStrut(12));
        }
        return wrap;
    }

    private static JPanel buildRoomCard(Room room) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Room header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel roomName = new JLabel("📍 " + room.getRoomName()
                + "  (" + room.getDevices().size() + " devices | "
                + room.getTotalWattage() + "W ON)");
        roomName.setFont(Theme.FONT_HEADER);
        roomName.setForeground(Theme.ACCENT);

        RoundedButton turnOffAll = new RoundedButton("Turn All OFF", Theme.DANGER.darker());
        turnOffAll.setFont(Theme.FONT_SMALL);
        turnOffAll.setPreferredSize(new Dimension(110, 28));
        turnOffAll.addActionListener(e -> {
            for (SmartDevice d : room.getDevices())
                d.turnOff();
            EventLogger.log("All devices in '" + room.getRoomName() + "' turned off.");
            refreshDashboard();
        });

        if (currentUser != null && !currentUser.canEdit())
            turnOffAll.setEnabled(false);
        header.add(roomName, BorderLayout.WEST);
        header.add(turnOffAll, BorderLayout.EAST);

        // Devices grid
        JPanel devGrid = new JPanel(new GridLayout(0, 2, 10, 10));
        devGrid.setOpaque(false);

        if (room.getDevices().isEmpty()) {
            JLabel none = new JLabel("No devices in this room.");
            none.setForeground(Theme.TEXT_MUTED);
            devGrid.setLayout(new FlowLayout(FlowLayout.LEFT));
            devGrid.add(none);
        }

        for (SmartDevice device : room.getDevices()) {
            devGrid.add(buildDeviceCard(device, room));
        }

        card.add(header, BorderLayout.NORTH);
        card.add(devGrid, BorderLayout.CENTER);
        return card;
    }

    private static JPanel buildDeviceCard(SmartDevice d, Room room) {
        JPanel card = new JPanel(new BorderLayout(8, 5));
        card.setBackground(new Color(25, 35, 62));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(d.isOn() ? Theme.ON_GREEN : Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        // Icon & name
        String icon = d instanceof Light ? "💡" : d instanceof ACUnit ? "❄️" : "🌀";
        JLabel nameLabel = new JLabel(icon + "  " + d.getDeviceName());
        nameLabel.setFont(Theme.FONT_BODY);
        nameLabel.setForeground(Theme.TEXT_WHITE);

        JLabel statusLabel = new JLabel(d.isOn() ? "● ON" : "○ OFF");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(d.isOn() ? Theme.ON_GREEN : Theme.OFF_GRAY);

        JLabel specLabel = new JLabel(d.getStatus());
        specLabel.setFont(Theme.FONT_SMALL);
        specLabel.setForeground(Theme.TEXT_MUTED);

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        controls.setOpaque(false);

        boolean canEdit = currentUser != null && currentUser.canEdit();

        RoundedButton toggleBtn = new RoundedButton(d.isOn() ? "Turn OFF" : "Turn ON",
                d.isOn() ? Theme.DANGER.darker() : new Color(30, 140, 90));
        toggleBtn.setFont(Theme.FONT_SMALL);
        toggleBtn.setPreferredSize(new Dimension(85, 28));
        toggleBtn.setEnabled(canEdit);
        toggleBtn.addActionListener(e -> {
            if (d.isOn())
                d.turnOff();
            else
                d.turnOn();
            EventLogger.log(d.getDeviceName() + " toggled to " + (d.isOn() ? "ON" : "OFF")
                    + " by " + currentUser.getUsername());
            refreshDashboard();
        });

        controls.add(toggleBtn);

        // Device-specific controls
        if (canEdit) {
            if (d instanceof Light) {
                JSlider slider = new JSlider(0, 100, ((Light) d).getBrightness());
                slider.setOpaque(false);
                slider.setForeground(Theme.ACCENT);
                slider.setPreferredSize(new Dimension(90, 25));
                slider.addChangeListener(e -> {
                    ((Light) d).setBrightness(slider.getValue());
                    specLabel.setText(d.getStatus());
                });
                JLabel bLabel = new JLabel("🔆");
                bLabel.setFont(Theme.FONT_SMALL);
                controls.add(bLabel);
                controls.add(slider);

            } else if (d instanceof ACUnit) {
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(((ACUnit) d).getTemp(), 16, 30, 1));
                spinner.setPreferredSize(new Dimension(60, 25));
                spinner.addChangeListener(e -> {
                    ((ACUnit) d).setTemp((Integer) spinner.getValue());
                    specLabel.setText(d.getStatus());
                });
                JLabel tLabel = new JLabel("🌡");
                tLabel.setFont(Theme.FONT_SMALL);
                controls.add(tLabel);
                controls.add(spinner);

            } else if (d instanceof Fan) {
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(((Fan) d).getSpeed(), 1, 3, 1));
                spinner.setPreferredSize(new Dimension(55, 25));
                spinner.addChangeListener(e -> {
                    ((Fan) d).setSpeed((Integer) spinner.getValue());
                    specLabel.setText(d.getStatus());
                });
                JLabel sLabel = new JLabel("💨");
                sLabel.setFont(Theme.FONT_SMALL);
                controls.add(sLabel);
                controls.add(spinner);
            }

            // Remove device button
            RoundedButton removeBtn = new RoundedButton("✕", new Color(80, 30, 30));
            removeBtn.setFont(Theme.FONT_SMALL);
            removeBtn.setPreferredSize(new Dimension(30, 28));
            removeBtn.addActionListener(e -> {
                int res = JOptionPane.showConfirmDialog(frame,
                        "Remove device '" + d.getDeviceName() + "'?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    room.removeDevice(d);
                    EventLogger.log("Device removed: " + d.getDeviceName());
                    refreshDashboard();
                }
            });
            controls.add(removeBtn);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(nameLabel, BorderLayout.WEST);
        top.add(statusLabel, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(specLabel, BorderLayout.CENTER);
        card.add(controls, BorderLayout.SOUTH);
        return card;
    }

    // ===================== MANAGE TAB =====================
    private static JPanel buildManageContent(JTabbedPane tabs) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(Theme.BG_DARK);
        wrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add Room
        wrap.add(sectionTitle("➕ Add Room"));
        wrap.add(Box.createVerticalStrut(8));
        JPanel addRoomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addRoomRow.setOpaque(false);
        JTextField roomField = styledField("Room name (e.g. Kitchen)");
        roomField.setPreferredSize(new Dimension(260, 36));
        RoundedButton addRoomBtn = new RoundedButton("Add Room", Theme.ACCENT.darker());
        JLabel roomMsg = new JLabel(" ");
        roomMsg.setFont(Theme.FONT_SMALL);
        addRoomBtn.setPreferredSize(new Dimension(110, 36));
        addRoomBtn.addActionListener(e -> {
            String n = roomField.getText().trim();
            if (n.isEmpty()) {
                roomMsg.setForeground(Theme.DANGER);
                roomMsg.setText("Enter room name!");
                return;
            }
            if (house.findRoom(n) != null) {
                roomMsg.setForeground(Theme.DANGER);
                roomMsg.setText("Room already exists!");
                return;
            }
            house.createRoom(n);
            EventLogger.log("Room added: " + n);
            roomField.setText("");
            roomMsg.setForeground(Theme.ACCENT2);
            roomMsg.setText("✓ Room '" + n + "' added!");
            refreshDashboard();
        });
        addRoomRow.add(roomField);
        addRoomRow.add(addRoomBtn);
        addRoomRow.add(roomMsg);
        wrap.add(addRoomRow);

        wrap.add(Box.createVerticalStrut(20));

        // Add Device
        wrap.add(sectionTitle("💡 Add Device to Room"));
        wrap.add(Box.createVerticalStrut(8));
        JPanel addDevRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addDevRow.setOpaque(false);
        JTextField devName = styledField("Device Name");
        devName.setPreferredSize(new Dimension(180, 36));
        JTextField devId = styledField("Device ID");
        devId.setPreferredSize(new Dimension(100, 36));
        JComboBox<String> roomCombo = new JComboBox<>();
        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "Light", "Fan", "AC Unit" });
        styleCombo(roomCombo);
        styleCombo(typeCombo);
        roomCombo.setPreferredSize(new Dimension(150, 36));
        typeCombo.setPreferredSize(new Dimension(110, 36));
        for (Room r : house.getRooms())
            roomCombo.addItem(r.getRoomName());
        RoundedButton addDevBtn = new RoundedButton("Add Device", new Color(30, 140, 90));
        addDevBtn.setPreferredSize(new Dimension(120, 36));
        JLabel devMsg = new JLabel(" ");
        devMsg.setFont(Theme.FONT_SMALL);

        addDevBtn.addActionListener(e -> {
            String rName = (String) roomCombo.getSelectedItem();
            String dName = devName.getText().trim();
            String dId = devId.getText().trim();
            String dType = (String) typeCombo.getSelectedItem();
            if (rName == null || dName.isEmpty() || dId.isEmpty()) {
                devMsg.setForeground(Theme.DANGER);
                devMsg.setText("Fill all fields!");
                return;
            }
            Room r = house.findRoom(rName);
            SmartDevice nd = dType.equals("Light") ? new Light(dId, dName, rName)
                    : dType.equals("AC Unit") ? new ACUnit(dId, dName, rName)
                            : new Fan(dId, dName, rName);
            r.addDevice(nd);
            EventLogger.log("Device added: " + dName + " to " + rName);
            devName.setText("");
            devId.setText("");
            devMsg.setForeground(Theme.ACCENT2);
            devMsg.setText("✓ " + dName + " added to " + rName + "!");
            refreshDashboard();
        });
        addDevRow.add(devName);
        addDevRow.add(devId);
        addDevRow.add(roomCombo);
        addDevRow.add(typeCombo);
        addDevRow.add(addDevBtn);
        addDevRow.add(devMsg);
        wrap.add(addDevRow);

        wrap.add(Box.createVerticalStrut(20));

        // Remove Room
        wrap.add(sectionTitle("🗑 Remove Room"));
        wrap.add(Box.createVerticalStrut(8));
        JPanel removeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        removeRow.setOpaque(false);
        JComboBox<String> removeCombo = new JComboBox<>();
        styleCombo(removeCombo);
        removeCombo.setPreferredSize(new Dimension(200, 36));
        for (Room r : house.getRooms())
            removeCombo.addItem(r.getRoomName());
        RoundedButton removeRoomBtn = new RoundedButton("Remove Room", Theme.DANGER.darker());
        removeRoomBtn.setPreferredSize(new Dimension(130, 36));
        JLabel removeMsg = new JLabel(" ");
        removeMsg.setFont(Theme.FONT_SMALL);
        removeRoomBtn.addActionListener(e -> {
            String rName = (String) removeCombo.getSelectedItem();
            if (rName == null) {
                removeMsg.setForeground(Theme.DANGER);
                removeMsg.setText("Select a room!");
                return;
            }
            int res = JOptionPane.showConfirmDialog(frame, "Remove room '" + rName + "' and all its devices?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                house.removeRoom(house.findRoom(rName));
                EventLogger.log("Room removed: " + rName);
                removeMsg.setForeground(Theme.ACCENT2);
                removeMsg.setText("✓ Room removed!");
                refreshDashboard();
            }
        });
        removeRow.add(removeCombo);
        removeRow.add(removeRoomBtn);
        removeRow.add(removeMsg);
        wrap.add(removeRow);

        wrap.add(Box.createVerticalStrut(20));

        // Save / Load
        wrap.add(sectionTitle("💾 Save & Load House Data"));
        wrap.add(Box.createVerticalStrut(8));
        JPanel saveRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        saveRow.setOpaque(false);
        RoundedButton saveBtn = new RoundedButton("💾 Save to File", new Color(30, 100, 160));
        RoundedButton loadBtn = new RoundedButton("📂 Load from File", new Color(80, 60, 20));
        saveBtn.setPreferredSize(new Dimension(145, 36));
        loadBtn.setPreferredSize(new Dimension(155, 36));
        JLabel saveMsg = new JLabel(" ");
        saveMsg.setFont(Theme.FONT_SMALL);
        saveBtn.addActionListener(e -> {
            DataManager.save(house);
            saveMsg.setForeground(Theme.ACCENT2);
            saveMsg.setText("✓ Saved to house_data.txt!");
        });
        loadBtn.addActionListener(e -> {
            House loaded = DataManager.load();
            if (loaded != null) {
                house = loaded;
                saveMsg.setForeground(Theme.ACCENT2);
                saveMsg.setText("✓ Data loaded!");
                refreshDashboard();
            } else {
                saveMsg.setForeground(Theme.DANGER);
                saveMsg.setText("✗ No saved file found.");
            }
        });
        saveRow.add(saveBtn);
        saveRow.add(loadBtn);
        saveRow.add(saveMsg);
        wrap.add(saveRow);

        return wrap;
    }

    // ===================== AUTOMATION TAB =====================
    private static JPanel buildAutomationContent() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(Theme.BG_DARK);
        wrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        wrap.add(sectionTitle("⚡ Automation Rules"));
        wrap.add(Box.createVerticalStrut(5));
        JLabel info = new JLabel("  Rules auto-execute when you trigger a condition below.");
        info.setFont(Theme.FONT_SMALL);
        info.setForeground(Theme.TEXT_MUTED);
        wrap.add(info);
        wrap.add(Box.createVerticalStrut(12));

        // Rules list
        for (AutomationRule rule : rules) {
            JPanel ruleCard = new JPanel(new BorderLayout(10, 0));
            ruleCard.setBackground(new Color(20, 28, 52));
            ruleCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(rule.isActive() ? Theme.WARNING : Theme.BORDER, 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            ruleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            JLabel ruleText = new JLabel("🔔 " + rule.getTrigger()
                    + "  →  " + rule.getAction() + " [" + rule.getTargetDevice() + "]");
            ruleText.setFont(Theme.FONT_BODY);
            ruleText.setForeground(Theme.TEXT_WHITE);

            JLabel activeLabel = new JLabel(rule.isActive() ? "Active ✅" : "Inactive ❌");
            activeLabel.setFont(Theme.FONT_SMALL);
            activeLabel.setForeground(rule.isActive() ? Theme.ACCENT2 : Theme.TEXT_MUTED);

            RoundedButton toggleBtn = new RoundedButton(rule.isActive() ? "Deactivate" : "Activate",
                    rule.isActive() ? new Color(80, 40, 20) : new Color(20, 80, 40));
            toggleBtn.setFont(Theme.FONT_SMALL);
            toggleBtn.setPreferredSize(new Dimension(95, 28));
            toggleBtn.setEnabled(currentUser != null && currentUser.canEdit());
            toggleBtn.addActionListener(e -> {
                try {
                    if (rule.isActive())
                        rule.deactivate();
                    else
                        rule.activate();
                } catch (Exception ex) {
                }
                refreshDashboard();
            });

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            right.setOpaque(false);
            right.add(activeLabel);
            right.add(toggleBtn);

            ruleCard.add(ruleText, BorderLayout.WEST);
            ruleCard.add(right, BorderLayout.EAST);
            wrap.add(ruleCard);
            wrap.add(Box.createVerticalStrut(8));
        }

        // Trigger condition
        wrap.add(Box.createVerticalStrut(15));
        wrap.add(sectionTitle("▶ Trigger a Condition"));
        wrap.add(Box.createVerticalStrut(8));
        JPanel trigRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        trigRow.setOpaque(false);
        JComboBox<String> condCombo = new JComboBox<>(new String[] {
                "NIGHT_MODE", "AWAY_MODE", "MORNING_MODE" });
        styleCombo(condCombo);
        condCombo.setPreferredSize(new Dimension(180, 36));
        RoundedButton trigBtn = new RoundedButton("▶ Trigger", new Color(160, 100, 0));
        trigBtn.setPreferredSize(new Dimension(110, 36));
        trigBtn.setEnabled(currentUser != null && currentUser.canEdit());
        JLabel trigMsg = new JLabel(" ");
        trigMsg.setFont(Theme.FONT_SMALL);
        trigBtn.addActionListener(e -> {
            String cond = (String) condCombo.getSelectedItem();
            for (AutomationRule rule : rules)
                rule.evaluate(cond, house);
            trigMsg.setForeground(Theme.ACCENT2);
            trigMsg.setText("✓ Condition '" + cond + "' evaluated!");
            refreshDashboard();
        });
        trigRow.add(condCombo);
        trigRow.add(trigBtn);
        trigRow.add(trigMsg);
        wrap.add(trigRow);

        // Add custom rule
        wrap.add(Box.createVerticalStrut(20));
        wrap.add(sectionTitle("➕ Add Custom Rule"));
        wrap.add(Box.createVerticalStrut(8));
        JPanel addRuleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        addRuleRow.setOpaque(false);
        JTextField trigField = styledField("Trigger (e.g. MY_MODE)");
        trigField.setPreferredSize(new Dimension(180, 36));
        JTextField devField = styledField("Device Name");
        devField.setPreferredSize(new Dimension(150, 36));
        JComboBox<String> actionCombo = new JComboBox<>(new String[] { "TURN_ON", "TURN_OFF" });
        styleCombo(actionCombo);
        actionCombo.setPreferredSize(new Dimension(110, 36));
        RoundedButton addRuleBtn = new RoundedButton("Add Rule", new Color(50, 80, 150));
        addRuleBtn.setPreferredSize(new Dimension(100, 36));
        addRuleBtn.setEnabled(currentUser != null && currentUser.canEdit());
        JLabel ruleMsg = new JLabel(" ");
        ruleMsg.setFont(Theme.FONT_SMALL);
        addRuleBtn.addActionListener(e -> {
            String trig = trigField.getText().trim().toUpperCase();
            String dev = devField.getText().trim();
            String act = (String) actionCombo.getSelectedItem();
            if (trig.isEmpty() || dev.isEmpty()) {
                ruleMsg.setForeground(Theme.DANGER);
                ruleMsg.setText("Fill all fields!");
                return;
            }
            rules.add(new AutomationRule(trig, dev, act));
            EventLogger.log("Automation rule added: " + trig + " → " + act + " on " + dev);
            ruleMsg.setForeground(Theme.ACCENT2);
            ruleMsg.setText("✓ Rule added!");
            trigField.setText("");
            devField.setText("");
            refreshDashboard();
        });
        addRuleRow.add(trigField);
        addRuleRow.add(devField);
        addRuleRow.add(actionCombo);
        addRuleRow.add(addRuleBtn);
        addRuleRow.add(ruleMsg);
        wrap.add(addRuleRow);

        return wrap;
    }

    // ===================== SCHEDULER TAB =====================
    private static JPanel buildSchedulerContent() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(Theme.BG_DARK);
        wrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        wrap.add(sectionTitle("📅 Task Scheduler"));
        wrap.add(Box.createVerticalStrut(8));
        JLabel info = new JLabel("  Schedule device actions. Click 'Run All' to execute.");
        info.setFont(Theme.FONT_SMALL);
        info.setForeground(Theme.TEXT_MUTED);
        wrap.add(info);
        wrap.add(Box.createVerticalStrut(12));

        // Add task
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addRow.setOpaque(false);
        JTextField timeField = styledField("Time (e.g. 10:00 PM)");
        timeField.setPreferredSize(new Dimension(160, 36));
        JTextField devField = styledField("Device Name");
        devField.setPreferredSize(new Dimension(160, 36));
        JComboBox<String> actionCombo = new JComboBox<>(new String[] { "TURN_ON", "TURN_OFF" });
        styleCombo(actionCombo);
        actionCombo.setPreferredSize(new Dimension(110, 36));
        RoundedButton addBtn = new RoundedButton("Add Task", new Color(40, 90, 160));
        addBtn.setPreferredSize(new Dimension(100, 36));
        addBtn.setEnabled(currentUser != null && currentUser.canEdit());
        JLabel addMsg = new JLabel(" ");
        addMsg.setFont(Theme.FONT_SMALL);

        JPanel tasksList = new JPanel();
        tasksList.setLayout(new BoxLayout(tasksList, BoxLayout.Y_AXIS));
        tasksList.setOpaque(false);

        Runnable refreshTasks = () -> {
            tasksList.removeAll();
            for (int i = 0; i < scheduler.getTasks().size(); i++) {
                String[] t = scheduler.getTasks().get(i);
                final int idx = i;
                JPanel tRow = new JPanel(new BorderLayout(10, 0));
                tRow.setBackground(new Color(20, 28, 52));
                tRow.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                tRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
                JLabel tLabel = new JLabel("⏰ " + t[0] + "  |  " + t[2] + "  →  " + t[1]);
                tLabel.setFont(Theme.FONT_BODY);
                tLabel.setForeground(Theme.TEXT_WHITE);
                RoundedButton delBtn = new RoundedButton("Remove", new Color(80, 20, 20));
                delBtn.setFont(Theme.FONT_SMALL);
                delBtn.setPreferredSize(new Dimension(80, 26));
                delBtn.setEnabled(currentUser != null && currentUser.canEdit());
                delBtn.addActionListener(ev -> {
                    scheduler.removeTask(idx);
                    refreshDashboard();
                });
                tRow.add(tLabel, BorderLayout.WEST);
                tRow.add(delBtn, BorderLayout.EAST);
                tasksList.add(tRow);
                tasksList.add(Box.createVerticalStrut(6));
            }
            tasksList.revalidate();
            tasksList.repaint();
        };

        addBtn.addActionListener(e -> {
            String time = timeField.getText().trim();
            String dev = devField.getText().trim();
            String act = (String) actionCombo.getSelectedItem();
            if (time.isEmpty() || dev.isEmpty()) {
                addMsg.setForeground(Theme.DANGER);
                addMsg.setText("Fill all fields!");
                return;
            }
            scheduler.addTask(time, dev, act);
            addMsg.setForeground(Theme.ACCENT2);
            addMsg.setText("✓ Task added!");
            timeField.setText("");
            devField.setText("");
            refreshTasks.run();
        });

        addRow.add(timeField);
        addRow.add(devField);
        addRow.add(actionCombo);
        addRow.add(addBtn);
        addRow.add(addMsg);
        wrap.add(addRow);
        wrap.add(Box.createVerticalStrut(10));

        RoundedButton runAllBtn = new RoundedButton("▶▶ Run All Tasks Now", new Color(30, 130, 70));
        runAllBtn.setFont(Theme.FONT_BODY);
        runAllBtn.setPreferredSize(new Dimension(200, 38));
        runAllBtn.setEnabled(currentUser != null && currentUser.canEdit());
        JPanel runWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        runWrap.setOpaque(false);
        runWrap.add(runAllBtn);
        JLabel runMsg = new JLabel(" ");
        runMsg.setFont(Theme.FONT_SMALL);
        runAllBtn.addActionListener(e -> {
            scheduler.runAll(house);
            runMsg.setForeground(Theme.ACCENT2);
            runMsg.setText("✓ All tasks executed!");
            refreshDashboard();
        });
        wrap.add(runWrap);
        wrap.add(runMsg);
        wrap.add(Box.createVerticalStrut(15));
        wrap.add(sectionTitle("Scheduled Tasks"));
        wrap.add(Box.createVerticalStrut(8));
        refreshTasks.run();
        wrap.add(tasksList);

        return wrap;
    }

    // ===================== USERS TAB =====================
    private static JPanel buildUsersContent() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(Theme.BG_DARK);
        wrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        wrap.add(sectionTitle("👥 Registered Users"));
        wrap.add(Box.createVerticalStrut(10));

        for (User u : userManager.getUsers()) {
            JPanel uCard = new JPanel(new BorderLayout(10, 0));
            uCard.setBackground(new Color(20, 28, 52));
            uCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(u.canEdit() ? Theme.WARNING : Theme.ACCENT, 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            uCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            JLabel icon = new JLabel(u.canEdit() ? "👑" : "👤");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            JLabel uName = new JLabel(u.getUsername());
            uName.setFont(Theme.FONT_HEADER);
            uName.setForeground(Theme.TEXT_WHITE);
            JLabel uRole = new JLabel(u.getRole());
            uRole.setFont(Theme.FONT_SMALL);
            uRole.setForeground(u.canEdit() ? Theme.WARNING : Theme.ACCENT);
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            left.setOpaque(false);
            left.add(icon);
            left.add(uName);
            uCard.add(left, BorderLayout.WEST);
            uCard.add(uRole, BorderLayout.EAST);
            wrap.add(uCard);
            wrap.add(Box.createVerticalStrut(8));
        }

        // Add user
        wrap.add(Box.createVerticalStrut(15));
        wrap.add(sectionTitle("➕ Add New User"));
        wrap.add(Box.createVerticalStrut(8));
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addRow.setOpaque(false);
        JTextField uField = styledField("Username");
        uField.setPreferredSize(new Dimension(160, 36));
        JPasswordField pField = new JPasswordField();
        stylePasswordField(pField, "Password (min 4)");
        pField.setPreferredSize(new Dimension(160, 36));
        JComboBox<String> roleCombo = new JComboBox<>(new String[] { "Guest", "Admin" });
        styleCombo(roleCombo);
        roleCombo.setPreferredSize(new Dimension(100, 36));
        RoundedButton addUserBtn = new RoundedButton("Add User", new Color(70, 30, 130));
        addUserBtn.setPreferredSize(new Dimension(100, 36));
        JLabel uMsg = new JLabel(" ");
        uMsg.setFont(Theme.FONT_SMALL);
        addUserBtn.addActionListener(e -> {
            String u = uField.getText().trim();
            String p = new String(pField.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();
            if (u.isEmpty() || p.isEmpty()) {
                uMsg.setForeground(Theme.DANGER);
                uMsg.setText("Fill all fields!");
                return;
            }
            try {
                User nu = role.equals("Admin") ? new Admin(u, p) : new Guest(u, p);
                userManager.addUser(nu);
                EventLogger.log("User added: " + u + " (" + role + ")");
                uMsg.setForeground(Theme.ACCENT2);
                uMsg.setText("✓ User '" + u + "' added!");
                uField.setText("");
                pField.setText("");
                refreshDashboard();
            } catch (Exception ex) {
                uMsg.setForeground(Theme.DANGER);
                uMsg.setText("✗ " + ex.getMessage());
            }
        });
        addRow.add(uField);
        addRow.add(pField);
        addRow.add(roleCombo);
        addRow.add(addUserBtn);
        addRow.add(uMsg);
        wrap.add(addRow);

        return wrap;
    }

    // ===================== EVENT LOG TAB =====================
    private static JPanel buildLogContent() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setBackground(Theme.BG_DARK);
        wrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = sectionTitle("📋 Event Log  (events.txt)");
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(Theme.FONT_MONO);
        area.setBackground(new Color(10, 14, 28));
        area.setForeground(Theme.ACCENT2);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load log file
        try (BufferedReader br = new BufferedReader(new FileReader("events.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            area.setText(sb.toString());
            area.setCaretPosition(area.getDocument().getLength());
        } catch (Exception e) {
            area.setText("No events logged yet.");
        }

        RoundedButton refreshBtn = new RoundedButton("🔄 Refresh Log", new Color(30, 80, 130));
        refreshBtn.setPreferredSize(new Dimension(140, 34));
        refreshBtn.addActionListener(e -> {
            try (BufferedReader br = new BufferedReader(new FileReader("events.txt"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    sb.append(line).append("\n");
                area.setText(sb.toString());
                area.setCaretPosition(area.getDocument().getLength());
            } catch (Exception ex) {
                area.setText("No events logged yet.");
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(refreshBtn, BorderLayout.EAST);

        wrap.add(top, BorderLayout.NORTH);
        wrap.add(new JScrollPane(area), BorderLayout.CENTER);
        return wrap;
    }

    // ===================== REFRESH =====================
    private static void refreshDashboard() {
        if (currentUser != null) {
            userLabel.setText(currentUser.getUsername() + " · " + currentUser.getRole() + "   ");
        }

        // Update stats
        statDevices.setText("📦 Devices: " + house.getTotalDevices());
        statOn.setText("🟢 ON: " + house.getOnDevicesCount());
        statWatts.setText("⚡ Total: " + house.getTotalWattageOn() + "W");

        // Rebuild main panel tabs
        Component[] comps = mainPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JPanel && c != mainPanel) {
                // We rebuild only the dashboard
            }
        }

        // Rebuild dashboard in place
        mainPanel.remove(1); // remove old dashboard
        mainPanel.add(buildDashboard(), "dashboard", 1);
        mainPanel.revalidate();
        mainPanel.repaint();
        cardLayout.show(mainPanel, "dashboard");
    }

    // ===================== HELPERS =====================
    private static JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setBackground(new Color(30, 40, 70));
        f.setForeground(Theme.TEXT_WHITE);
        f.setCaretColor(Theme.TEXT_WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        f.setFont(Theme.FONT_BODY);
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    private static void stylePasswordField(JPasswordField f, String placeholder) {
        f.setBackground(new Color(30, 40, 70));
        f.setForeground(Theme.TEXT_WHITE);
        f.setCaretColor(Theme.TEXT_WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        f.setFont(Theme.FONT_BODY);
    }

    private static void styleCombo(JComboBox<String> c) {
        c.setBackground(new Color(30, 40, 70));
        c.setForeground(Theme.TEXT_WHITE);
        c.setFont(Theme.FONT_BODY);
    }

    private static JLabel statLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    private static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADER);
        l.setForeground(Theme.ACCENT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JScrollPane wrapScroll(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.setBackground(Theme.BG_DARK);
        sp.getViewport().setBackground(Theme.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private static JPanel centeredWrap(JComponent c, int width) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        c.setPreferredSize(new Dimension(width, 38));
        p.add(c);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return p;
    }

    private static void showMsg(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}