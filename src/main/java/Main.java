import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        RepositoryMySql<HocSinh> s = new RepositoryMySql<>(HocSinh.class, TestConfig.getI().source());

        Object as = s.getQueryable()
                .select(
                        Queryable.col(Mon::getId),
                        Queryable.col(Mon::getHocSinhId)
                )
                .join(Mon.class, Mon::getHocSinhId, HocSinh::getId)
                .execute(null, HiuHiu.class);
        HocSinh h1 = s.getById("1");
        h1.setTenHocSinh("Hhshshshs");
        s.updateById(h1);
        System.out.println(h1.getTenHocSinh());
//        MapSqlParameterSource p = new MapSqlParameterSource();
//        List<HocSinh> a = (List<HocSinh>) s.getQueryable().execute(s.get_np(), p, HocSinh.class);
//        for (HocSinh h : a) {
//            System.out.println(h.getTenHocSinh());
//        }
    }
}