public class Bus extends Vehicle {
    private int fuelLevel;
    private double consumption;

    public Bus(double width, double length, int weight, int maxWeight, String model, int enginePower, int engineSpeed, int fuelLevel, double consumption) {
        super(width, length, weight, maxWeight, model, enginePower, engineSpeed);
        this.fuelLevel = fuelLevel;
        this.consumption = consumption;
    }

    // Нельзя унаследоваться от String
    // Блок static раньше конструктора
    // Нельзя переопределить метод, объявленный в том же классе
    //
}
