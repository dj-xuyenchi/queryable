
import java.util.List;

public interface ISQLAction<T> {
    T getById(String id);

    T updateById(T t) throws Exception;

    void deleteById(String id);

    Queryable getQueryable();
}

