package BasicObjects;

import Serializators.Serializer;

import java.io.Serializable;

public class Bus extends Vehicle implements Serializable {
    private int fuelLevel;
    private double consumption;

    public Bus(double width, double length, int weight, int maxWeight, String model, int enginePower, int engineSpeed, int fuelLevel, double consumption) {
        super(width, length, weight, maxWeight, model, enginePower, engineSpeed);
        this.fuelLevel = fuelLevel;
        this.consumption = consumption;
    }

    private Bus() {

    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    // Нельзя унаследоваться от String
    // Блок static раньше конструктора
    // Нельзя переопределить метод, объявленный в том же классе
    //
}
