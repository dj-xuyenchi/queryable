
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.*;

public class Queryable {
    StringBuilder query;
    QueryDetail detail;
    boolean isGroupBy = false;
    boolean isOrderBy = false;
    boolean isHaving = false;
    JDBCAction _jdbc;

    public StringBuilder getQuery() {
        return detail.getSelect()
                .append(detail.getFrom())
                .append(detail.getJoin() == null ? "" : detail.getJoin())
                .append(detail.getWhere() == null ? "" : detail.getWhere())
                .append(isGroupBy ? detail.getGroupBy() : "")
                .append(detail.getOrderBy() == null ? "" : detail.getOrderBy())
                .append(isHaving ? detail.getHaving() : "");
    }

    public Object execute() {
        return _jdbc.getList(getQuery().toString());
    }

    private Queryable() {
    }

    public Queryable(QueryDetail queryDetail) {
        this.detail = queryDetail;
    }

    public <T, K, S, D> Queryable join(Class<?> tableJoin, SerializableFunction<T, K> col1, SerializableFunction<S, D> col2) throws Exception {
        StringBuilder joinQuery = detail.getJoin() == null ? new StringBuilder() : detail.getJoin();
        String table = convertCamelToSnake(tableJoin.getSimpleName()).toUpperCase();
        String tbl1 = getClassName(col1).toUpperCase();
        String tbl2 = getClassName(col2).toUpperCase();
        if (!tbl1.equals(table) && !tbl2.equals(table)) {
            throw new Exception("Ca 2 cot khong co cot nao join vao table " + table);
        }
        if (tbl1.equals(table) && tbl2.equals(table)) {
            throw new Exception("Ca 2 cot deu cung 1 table " + table);
        }
        String col1Name = convertCamelToSnake(getMethodName(col1).substring(3));
        String col2Name = convertCamelToSnake(getMethodName(col2).substring(3));
        joinQuery
                .append("\n         JOIN ")
                .append(table)
                .append(" on ")
                .append(tbl1)
                .append(".")
                .append(col1Name)
                .append(" = ")
                .append(tbl2)
                .append(".")
                .append(col2Name);
        detail.setJoin(joinQuery);
        return this;
    }

    public Queryable where(String where) {
        boolean isWhereBefore = detail.getWhere() != null;
        StringBuilder whereQuery = isWhereBefore ? detail.getWhere().append("\n   AND   ") : new StringBuilder("\n   WHERE\n");
        whereQuery
                .append(isWhereBefore ? where : "         ")
                .append(where);
        detail.setWhere(whereQuery);
        return this;
    }

    public Queryable select(String... colList) {
        StringBuilder selectCols = new StringBuilder();
        for (String col : colList) {
            selectCols
                    .append("         ")
                    .append(col)
                    .append(",\n");
        }
        selectCols.deleteCharAt(selectCols.length() - 2);
        String res = detail.getSelect().toString().replace("*", selectCols);
        detail.setSelect(new StringBuilder(res));
        return this;
    }

    public Queryable groupBy(String... colList) {
        isGroupBy = true;
        StringBuilder groupCol = new StringBuilder();
        Set<String> groupColList = new HashSet<>(Arrays.asList(colList));
        for (String col : groupColList) {
            groupCol
                    .append("         ")
                    .append(col)
                    .append(",\n");
        }
        groupCol.deleteCharAt(groupCol.length() - 2);
        detail.setGroupBy(detail.getGroupBy().append(groupCol));
        return this;
    }

    public Queryable orderBy(String... colList) {
        isOrderBy = true;
        StringBuilder orderCol = new StringBuilder();
        Set<String> orderList = new HashSet<>(Arrays.asList(colList));
        for (String col : orderList) {
            orderCol
                    .append("         ")
                    .append(col)
                    .append(",\n");
        }
        orderCol.deleteCharAt(orderCol.length() - 2);
        detail.setOrderBy(isGroupBy ? detail.getOrderBy().append(orderCol) : new StringBuilder("\n").append(detail.getOrderBy().append(orderCol)));
        return this;
    }

    public Queryable having(String... colList) throws Exception {
        if (!isGroupBy) {
            throw new Exception("Having bat buoc phai co Group By di cung!");
        }
        isHaving = true;
        StringBuilder havingCol = new StringBuilder();
        Set<String> havingList = new HashSet<>(Arrays.asList(colList));
        for (String col : havingList) {
            havingCol
                    .append("         ")
                    .append(col)
                    .append(",\n");
        }
        havingCol.deleteCharAt(havingCol.length() - 2);
        detail.setHaving(detail.getHaving().append(havingCol));
        return this;
    }


    public static String convertCamelToSnake(String camelCase) {
        return camelCase
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    private static String getMethodName(SerializableFunction<?, ?> function) throws Exception {
        Method writeReplace = function.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(function);
        return lambda.getImplMethodName();
    }

    private static String getClassName(SerializableFunction<?, ?> function) throws Exception {
        Method writeReplace = function.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(function);
        String[] fullName = serializedLambda.getImplClass().split("/");
        return fullName[fullName.length - 1];
    }

    public static <T, K> String col(SerializableFunction<T, K> function) throws Exception {
        Method writeReplace = function.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(function);
        String[] fullName = serializedLambda.getImplClass().split("/");
        return fullName[fullName.length - 1] + "." + convertCamelToSnake(getMethodName(function).substring(3));
    }
}
