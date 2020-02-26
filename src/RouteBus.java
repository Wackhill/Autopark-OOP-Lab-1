public class RouteBus {
    private Bus bus;
    private Driver driver;
    private Route route;

    public RouteBus(Bus bus, Driver driver, Route route) {
        this.bus = bus;
        this.driver = driver;
        this.route = route;
    }

    public Bus getBus() {
        return bus;
    }

    public Driver getDriver() {
        return driver;
    }

    public Route getRoute() {
        return route;
    }
}
