
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RepositoryMySql<T> implements ISQLAction<T> {
    final Class<T> c;
    String tableName;
    String idField;
    List<String> fieldNamesSQL;
    List<String> fieldNames;
    JdbcTemplate _j;
    NamedParameterJdbcTemplate _np;

    public RepositoryMySql(Class<T> c, DataSource source) {
        this.c = c;
        tableName = LibUtil.convertCamelToSnake(c.getSimpleName());
        fieldNames = new ArrayList<>();
        fieldNamesSQL = new ArrayList<>();
        int countId = 0;
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                idField = LibUtil.convertCamelToSnake(field.getName());
                countId++;
            }
            fieldNamesSQL.add(LibUtil.convertCamelToSnake(field.getName()));
            fieldNames.add(field.getName());
        }
        if (countId == 0) {
            throw new RuntimeException("Class khong khai bao khoa chinh!");
        }
        if (countId > 1) {
            throw new RuntimeException("Class co nhieu hon 1 khoa chinh!");
        }
        _j = new JdbcTemplate(source);
        _np = new NamedParameterJdbcTemplate(_j);
    }

    @Override
    public T getById(String id) {
        try {
            String query = """
                    select * from %s where %s = :id
                    """.formatted(tableName, idField);
            MapSqlParameterSource p = new MapSqlParameterSource();
            p.addValue("id", id);
            return _np.queryForObject(query, p, new BeanPropertyRowMapper<>(c));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T updateById(T t) {
        try {
            StringBuilder query = new StringBuilder("update " + tableName + " set ");
            for (int i = 0; i < fieldNames.size(); i++) {
                if (fieldNamesSQL.get(i).equals(idField)) {
                    continue;
                }
                Field nameField = c.getDeclaredField(fieldNames.get(i));
                nameField.setAccessible(true);
                String value = String.valueOf(nameField.get(t));
                query.append(fieldNamesSQL.get(i)).append(" = '").append(value).append("',");
            }
            // xoa dau , cuoi
            query.deleteCharAt(query.length() - 1);
            Field nameField = c.getDeclaredField(idField);
            nameField.setAccessible(true);
            String value = String.valueOf(nameField.get(t));
            query.append(" where ").append(idField).append("=").append(value);
            _j.update(query.toString());
            return getById(value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            String query =
                    """
                            delete from %s where %s = :id
                            """.formatted(tableName, idField);

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("id", id);

            int rowsAffected = _np.update(query, params);

            if (rowsAffected > 0) {
                System.out.println("Record deleted successfully.");
            } else {
                System.out.println("No record found with id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Queryable getQueryable() {
        StringBuilder initQuery = new StringBuilder();
        initQuery
                .append("   FROM  ")
                .append(tableName.toUpperCase())
                .append(" ");
        QueryDetail queryDetail = new QueryDetail();
        queryDetail.setFrom(initQuery);
        return new Queryable(queryDetail,_np);
    }

    public JdbcTemplate get_j() {
        return _j;
    }

    public NamedParameterJdbcTemplate get_np() {
        return _np;
    }
}
