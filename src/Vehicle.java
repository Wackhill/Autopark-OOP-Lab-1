public class Vehicle {
    public enum GearboxType {
        MANUAL,
        AUTOMATIC
    }

    public enum FuelType {
        PETROL,
        DIESEL,
        GAS,
        ELECTRIC
    }

    private double width;
    private double length;
    private int weight;
    private int maxWeight;
    private GearboxType gearboxType;
    private FuelType fuelType;

    public Vehicle(double width, double length, int weight, int maxWeight, GearboxType gearboxType, FuelType fuelType) {
        this.width = width;
        this.length = length;
        this.weight = weight;
        this.maxWeight = maxWeight;
        this.gearboxType = gearboxType;
        this.fuelType = fuelType;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public int getWeight() {
        return weight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public GearboxType getGearboxType() {
        return gearboxType;
    }

    public FuelType getFuelType() {
        return fuelType;
    }
}
