public class Vehicle {

    private double width;
    private double length;
    private int weight;
    private int maxWeight;
    private String model;
    private Engine engine;

    Vehicle(double width, double length, int weight, int maxWeight, String model, int enginePower, int engineVolume) {
        this.width = width;
        this.length = length;
        this.weight = weight;
        this.maxWeight = maxWeight;
        this.model = model;
        this.engine = new Engine(enginePower, engineVolume);
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
}
