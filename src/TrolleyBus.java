public class TrolleyBus extends Vehicle {
    private double electricConsumption;
    TrolleyBus(double width, double length, int weight, int maxWeight, GearboxType gearboxType, Engine.FuelType fuelType, int enginePower, double electricConsumption) {
        super(width, length, weight, maxWeight, gearboxType, fuelType, enginePower);
        this.electricConsumption = electricConsumption;
    }

    public double getElectricConsumption() {
        return electricConsumption;
    }
}
