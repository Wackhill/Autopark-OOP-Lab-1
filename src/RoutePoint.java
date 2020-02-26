public class RoutePoint {
    private double xPoint;
    private double yPoint;
    private String stopName;

    public RoutePoint(double xPoint, double yPoint, String stopName) {
        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.stopName = stopName;
    }

    public double getxPoint() {
        return xPoint;
    }

    public double getyPoint() {
        return yPoint;
    }

    public String getStopName() {
        return stopName;
    }
}
