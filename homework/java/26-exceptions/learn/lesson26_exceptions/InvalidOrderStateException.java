package learn.lesson26_exceptions;

/** 订单状态迁移不允许时抛出，消息携带订单 ID 和当前状态。 */
public final class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
