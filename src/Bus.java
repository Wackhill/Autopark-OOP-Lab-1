public class Bus extends Vehicle {
    private int fuelLevel;
    private double consumption;

    public Bus(double width, double length, int weight, int maxWeight, GearboxType gearboxType, FuelType fuelType, int fuelLevel, double consumption) {
        super(width, length, weight, maxWeight, gearboxType, fuelType);
        this.fuelLevel = fuelLevel;
        this.consumption = consumption;
    }

    // Нельзя унаследоваться от String
    // Блок static раньше конструктора
    // Нельзя переопределить метод, объявленный в том же классе
    //

    public int getFuelLevel() {
        return fuelLevel;
    }

    public double getConsumption() {
        return consumption;
    }
}
