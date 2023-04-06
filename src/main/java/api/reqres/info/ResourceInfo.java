package api.reqres.info;

public class ResourceInfo {
    String name;
    int year;

    public ResourceInfo(String name, int year) {
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
