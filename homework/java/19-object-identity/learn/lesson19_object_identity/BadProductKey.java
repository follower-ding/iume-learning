package learn.lesson19_object_identity;

import java.util.Objects;

/** Intentionally unsafe as a hash collection key: its equality field can change. */
/** 错误示例：可变 ID 参与哈希计算，入 Set 后改 ID 会破坏查找约定。 */
public final class BadProductKey {
    private String id;

    public BadProductKey(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof BadProductKey that && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
