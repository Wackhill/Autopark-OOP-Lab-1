package BasicObjects;

import java.io.Serializable;

class Engine implements Serializable {
    private int power;
    private int speed;

    Engine(int power, int speed) {
        this.power = power;
        this.speed = speed;
    }

    public Engine() {

    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getPower() {
        return power;
    }

    public int getSpeed() {
        return speed;
    }
}
