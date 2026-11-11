package learn.lesson13_inheritance;

public class Animal {
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String eat() {
        return name + "在吃饭";
    }

    public String speak() {
        return "动物叫";
    }
}
