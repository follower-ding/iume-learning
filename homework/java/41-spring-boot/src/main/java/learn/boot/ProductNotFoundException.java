package learn.boot;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(long id) { super("商品 " + id + " 不存在"); }
}
