package learn.lesson26_exceptions;

/** 找不到指定商品时抛出的业务异常，供调用方与存储故障区分。 */
public final class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
