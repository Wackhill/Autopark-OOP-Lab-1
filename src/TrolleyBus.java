public class TrolleyBus extends Vehicle {
    private double electricConsumption;

    TrolleyBus(double width, double length, int weight, int maxWeight, String model, int enginePower, double electricConsumption) {
        super(width, length, weight, maxWeight, model, enginePower);
        this.electricConsumption = electricConsumption;
    }
}
