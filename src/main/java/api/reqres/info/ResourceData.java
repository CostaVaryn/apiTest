package api.reqres.info;

public class ResourceData {
    String name;
    int year;

    public ResourceData(String name, int year) {
        this.name = name;
        this.year = year;
    }

    public String getResName() {
        return name;
    }

    public int getResYear() {
        return year;
    }
}
