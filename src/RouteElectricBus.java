public class RouteElectricBus {
    private ElectricBus electricBus;
    private Driver driver;
    private Route route;

    public RouteElectricBus(ElectricBus electricBus, Driver driver, Route route) {
        this.electricBus = electricBus;
        this.driver = driver;
        this.route = route;
    }

    public ElectricBus getElectricBus() {
        return electricBus;
    }

    public Driver getDriver() {
        return driver;
    }

    public Route getRoute() {
        return route;
    }
}
