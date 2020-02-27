public class ElectricBus extends TrolleyBus {
    private int batteryLevel;

    public ElectricBus(double width, double length, int weight, int maxWeight, GearboxType gearboxType, Engine.FuelType fuelType, int enginePower, int electricConsumption, int batteryLevel) {
        super(width, length, weight, maxWeight, gearboxType, fuelType, enginePower, electricConsumption);
        this.batteryLevel = batteryLevel;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }
}
