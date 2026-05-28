public class Light extends SmartDevice {

    private int brightness = 50;

    public Light(String deviceId, String name, String roomName,
            String manufacturer, String modelNumber, int wattage, int year) {
        super(deviceId, name, roomName, manufacturer, modelNumber, wattage, year);
    }
    
    public Light(String deviceId, String name, String roomName) {
    this(deviceId, name, roomName, "Generic", "N/A", 0, 0);
}

    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100)
            return;
        this.brightness = brightness;
    }

    @Override
    public String getStatus() {
        return "Light " + getName() + " brightness=" + brightness;
    }
}