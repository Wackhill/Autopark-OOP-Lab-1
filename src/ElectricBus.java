public class ElectricBus extends TrolleyBus {
    private int batteryLevel;

    public ElectricBus(double width, double length, int weight, int maxWeight, GearboxType gearboxType, FuelType fuelType, int electricConsumption, int batteryLevel) {
        super(width, length, weight, maxWeight, gearboxType, fuelType, electricConsumption);
        this.batteryLevel = batteryLevel;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }
}
