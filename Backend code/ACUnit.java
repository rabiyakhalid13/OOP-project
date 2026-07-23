public class ACUnit extends SmartDevice {

    private int temperature = 24;

    public ACUnit(String deviceId, String name, String roomName,
            String manufacturer, String modelNumber, int wattage, int year) {
        super(deviceId, name, roomName, manufacturer, modelNumber, wattage, year);
    }

    public void setTemperature(int t) {
        if (t < 16 || t > 30)
            return;
        temperature = t;
    }

 @Override
public String getStatus() {
    return "AC " + getDeviceName() + " temp=" + temperature;
}
}