public class Vehicle {
    public enum GearboxType {
        MANUAL,
        AUTOMATIC
    }

    private double width;
    private double length;
    private int weight;
    private int maxWeight;
    private GearboxType gearboxType;
    private Engine engine;

    public Vehicle(double width, double length, int weight, int maxWeight, GearboxType gearboxType, Engine.FuelType fuelType, int enginePower) {
        this.width = width;
        this.length = length;
        this.weight = weight;
        this.maxWeight = maxWeight;
        this.gearboxType = gearboxType;
        this.engine = new Engine(fuelType, enginePower);
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

    public Engine getEngine() {
        return engine;
    }
}
