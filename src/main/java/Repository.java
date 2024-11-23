
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Repository<T> implements ISQLAction<T> {
    Class<T> c;
    String tableName;
    String idField;
    List<String> fieldNamesSQL;
    List<String> fieldNames;

    public Repository(Class<T> c) {
        this.c = c;
        tableName = convertCamelToSnake(c.getSimpleName());
        fieldNames = new ArrayList<>();
        fieldNamesSQL = new ArrayList<>();
        int countId = 0;
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                idField = convertCamelToSnake(field.getName());
                countId++;
            }
            fieldNamesSQL.add(convertCamelToSnake(field.getName()));
            fieldNames.add(field.getName());
        }
        if (countId == 0) {
            throw new RuntimeException("Class khong khai bao khoa chinh!");
        }
        if (countId > 1) {
            throw new RuntimeException("Class co nhieu hon 1 khoa chinh!");
        }

    }


    @Override
    public T getById(String id) {
        String query = "select * from " + tableName +
                " where id = '" + id + "'";
        T res = null;
        return res;
    }

    @Override
    public T updateById(T t) throws Exception {
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
        System.out.println(query);
        // do sql
        T res = null;
        return res;
    }

    @Override
    public void deleteById(String id) {
        String query = "delete from " + tableName + " where " + idField + " = '" + id + "'";
        System.out.println(query);
    }

    @Override
    public List<T> getListByQuery(String query) {
        return null;
    }

    @Override
    public Queryable getQueryable() {
        StringBuilder initQuery = new StringBuilder();
        initQuery
                .append("   FROM  ")
                .append(tableName)
                .append(" ");
        QueryDetail queryDetail = new QueryDetail();
        queryDetail.setFrom(initQuery);
        return new Queryable(queryDetail);
    }


    public static String convertCamelToSnake(String camelCase) {
        return camelCase
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}
