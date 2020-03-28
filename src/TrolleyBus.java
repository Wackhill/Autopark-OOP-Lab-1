public class TrolleyBus extends Vehicle {
    private double electricConsumption;

    public TrolleyBus(double width, double length, int weight, int maxWeight, String model, int enginePower, int engineSpeed, double electricConsumption) {
        super(width, length, weight, maxWeight, model, enginePower, engineSpeed);
        this.electricConsumption = electricConsumption;
    }

    public TrolleyBus() {

    }

    public double getElectricConsumption() {
        return electricConsumption;
    }

    public void setElectricConsumption(double electricConsumption) {
        this.electricConsumption = electricConsumption;
    }
}
