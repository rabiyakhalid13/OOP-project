public class DeviceSpec {

    private final String manufacturer;
    private final String modelNumber;
    private final int wattage;
    private final int yearManufactured;

    public DeviceSpec(String manufacturer, String modelNumber, int wattage, int yearManufactured) {
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.wattage = wattage;
        this.yearManufactured = yearManufactured;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public int getWattage() {
        return wattage;
    }

    public int getYearManufactured() {
        return yearManufactured;
    }

    @Override
    public String toString() {
        return manufacturer + " " + modelNumber + " (" + wattage + "W)";
    }
}