public class ElectricBus extends TrolleyBus {
    private int batteryLevel;

    public ElectricBus(double width, double length, int weight, int maxWeight, String model, int enginePower, int engineSpeed, double electricConsumption, int batteryLevel) {
        super(width, length, weight, maxWeight, model, enginePower, engineSpeed, electricConsumption);
        this.batteryLevel = batteryLevel;
    }
}
