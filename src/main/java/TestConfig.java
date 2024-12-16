import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

public class TestConfig {
   static TestConfig testConfig;

    public DataSource source() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/dbhoc");
        dataSource.setUser("root");
        dataSource.setPassword("1231234");
        return dataSource;
    }

    private TestConfig() {

    }

    public static TestConfig getI() {
        if (testConfig == null) {
            testConfig = new TestConfig();
            return testConfig;
        } else {
            return testConfig;
        }
    }
}
