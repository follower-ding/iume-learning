package learn.lesson25_generics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** 内存仓储：LinkedHashMap 保插入序；findAll 返回不可修改快照。 */
public final class InMemoryRepository<T, ID> implements Repository<T, ID> {
    private final Map<ID, T> storage = new LinkedHashMap<>();

    @Override
    public void save(ID id, T value) {
        storage.put(Objects.requireNonNull(id, "id must not be null"),
                Objects.requireNonNull(value, "value must not be null"));
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(Objects.requireNonNull(id, "id must not be null")));
    }

    @Override
    public List<T> findAll() {
        // 快照：调用者不能改仓储内部 Map
        return List.copyOf(storage.values());
    }

    @Override
    public boolean deleteById(ID id) {
        return storage.remove(Objects.requireNonNull(id, "id must not be null")) != null;
    }
}
