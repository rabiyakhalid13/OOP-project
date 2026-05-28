public class Fan extends SmartDevice {

    private int speed = 1;

    public Fan(String deviceId, String name, String roomName,
            String manufacturer, String modelNumber, int wattage, int year) {
        super(deviceId, name, roomName, manufacturer, modelNumber, wattage, year);
    }

    public void setSpeed(int speed) {
        if (speed < 1 || speed > 3)
            return;
        this.speed = speed;
    }

    @Override
    public String getStatus() {
        return "Fan " + getName() + " speed=" + speed;
    }
}