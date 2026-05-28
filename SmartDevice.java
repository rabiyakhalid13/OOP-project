public abstract class SmartDevice {

    private final String deviceId;
    private final String name;
    private String roomName;
    private boolean isOn;

    private final DeviceSpec spec;

    public SmartDevice(String deviceId, String name, String roomName,
            String manufacturer, String modelNumber,
            int wattage, int yearManufactured) {

        this.deviceId = deviceId;
        this.name = name;
        this.roomName = roomName;
        this.isOn = false;

        this.spec = new DeviceSpec(manufacturer, modelNumber, wattage, yearManufactured);
    }

    public void turnOn() {
        isOn = true;
        System.out.println(name + " ON");
    }

    public void turnOff() {
        isOn = false;
        System.out.println(name + " OFF");
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isOn() {
        return isOn;
    }

    public DeviceSpec getSpec() {
        return spec;
    }

    public abstract String getStatus();
}