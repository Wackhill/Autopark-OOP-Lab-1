public class TrolleyBus extends Vehicle {
    private int electricConsumption;
    TrolleyBus(double width, double length, int weight, int maxWeight, GearboxType gearboxType, Engine.FuelType fuelType, int enginePower, int electricConsumption) {
        super(width, length, weight, maxWeight, gearboxType, fuelType, enginePower);
        this.electricConsumption = electricConsumption;
    }

    public int getElectricConsumption() {
        return electricConsumption;
    }
}
