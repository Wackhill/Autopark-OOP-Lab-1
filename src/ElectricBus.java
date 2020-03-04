public class ElectricBus extends TrolleyBus {
    private int batteryLevel;

    public ElectricBus(double width, double length, int weight, int maxWeight, String model, int enginePower, double electricConsumption, int batteryLevel) {
        super(width, length, weight, maxWeight, model, enginePower, electricConsumption);
        this.batteryLevel = batteryLevel;
    }
}
