public class Engine {
    public enum FuelType {
        PETROL,
        DIESEL,
        GAS,
        ELECTRIC
    }

    private FuelType fuelType;
    private int power;

    public Engine(FuelType fuelType, int power) {
        this.fuelType = fuelType;
        this.power = power;
    }
}
