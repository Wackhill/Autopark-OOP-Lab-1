public class RouteTrolleyBus {
    private TrolleyBus trolleyBus;
    private Driver driver;
    private Route route;

    public RouteTrolleyBus(TrolleyBus trolleyBus, Driver driver, Route route) {
        this.trolleyBus = trolleyBus;
        this.driver = driver;
        this.route = route;
    }

    public TrolleyBus getTrolleyBus() {
        return trolleyBus;
    }

    public Driver getDriver() {
        return driver;
    }

    public Route getRoute() {
        return route;
    }
}
