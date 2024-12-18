
public class QueryDetail {
    private StringBuilder join;
    private StringBuilder where;
    private StringBuilder groupBy;
    private StringBuilder having;
    private StringBuilder orderBy;
    private StringBuilder select;
    private StringBuilder from;

    public QueryDetail() {
        select = new StringBuilder("   SELECT  \n*");
    }

    public StringBuilder getJoin() {
        return join;
    }

    public void setJoin(StringBuilder join) {
        this.join = join;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public StringBuilder getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(StringBuilder groupBy) {
        this.groupBy = groupBy;
    }

    public StringBuilder getHaving() {
        return having;
    }

    public void setHaving(StringBuilder having) {
        this.having = having;
    }

    public StringBuilder getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(StringBuilder orderBy) {
        this.orderBy = orderBy;
    }

    public StringBuilder getSelect() {
        return select;
    }

    public void setSelect(StringBuilder select) {
        this.select = select;
    }

    public StringBuilder getFrom() {
        return from;
    }

    public void setFrom(StringBuilder from) {
        this.from = from;
    }
}
