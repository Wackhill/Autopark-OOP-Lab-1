package BasicObjects;

import java.io.Serializable;

public class Vehicle implements Serializable {

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

    public Vehicle() {

    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
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
