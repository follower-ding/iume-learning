package learn.boot;

/** 数据库行对象；id 在 INSERT 后由数据库生成。 */
public class Product {
    private Long id;
    private String name;
    private int price;
    public Product() { }
    public Product(String name, int price) { this.name = name; this.price = price; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
