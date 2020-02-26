public class TrolleyBus extends Vehicle {
    private int electricConsumption;
    public TrolleyBus(double width, double length, int weight, int maxWeight, GearboxType gearboxType, FuelType fuelType, int electricConsumption) {
        super(width, length, weight, maxWeight, gearboxType, fuelType);
        this.electricConsumption = electricConsumption;
    }

    public int getElectricConsumption() {
        return electricConsumption;
    }
}
