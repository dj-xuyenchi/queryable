import java.util.List;

public interface JDBCAction {
    List<Object> getList(String sql);
}
