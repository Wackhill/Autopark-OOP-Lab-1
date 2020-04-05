package BasicObjects;

import java.io.Serializable;

public class Driver implements Serializable {
    private String name;
    private int experience;

    public Driver(String name, int experience) {
        this.name = name;
        this.experience = experience;
    }

    public Driver() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getExperience() {
        return experience;
    }
}
