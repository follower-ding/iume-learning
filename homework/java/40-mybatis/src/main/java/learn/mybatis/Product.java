package learn.mybatis;

/** 普通 Java 对象；MyBatis 把查询列映射到这些属性。必须有无参构造器和 getter/setter。 */
public class Product {
    private int id;
    private String name;
    private int price;

    public Product() { }

    public Product(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
