import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        RepositoryMySql<HocSinh> s = new RepositoryMySql<>(HocSinh.class, TestConfig.getI().source());
        try {

            List<HiuHiu> as = (List<HiuHiu>) s.getQueryable()
                    .select(
                            Queryable.sl(Mon::getId),
                            Queryable.sl(Mon::getHocSinhId),
                            Queryable.sl(HocSinh::getTenHocSinh)
                    )
                    .join(Mon.class, Mon::getHocSinhId, HocSinh::getId)
                    .groupBy(Queryable.col(Mon::getId))
                    .orderBy(Queryable.col(Mon::getId))
                    .execute(new MapSqlParameterSource(), HiuHiu.class);
            for (HiuHiu h : as) {
                System.out.println(h.getTenHocSinh());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   HocSinh h1 = s.getById("1");
        //   h1.setTenHocSinh("Hhshshshs");
        //    s.updateById(h1);
//        MapSqlParameterSource p = new MapSqlParameterSource();

    }
}