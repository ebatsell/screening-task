import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.h2.jdbcx.JdbcConnectionPool;
import java.sql.*;
import java.util.ArrayList;

public class MyTask {
    public static void main(String[] args) throws Exception {
        // Setup DB connection
        JdbcConnectionPool cp = JdbcConnectionPool.
                create("jdbc:h2:~/test", "sa", "sa");
        Connection conn = cp.getConnection();
        Statement st = conn.createStatement();

        // Clean out table for every new execution
        st.execute("drop table if exists normaldist");
        st.execute("create table normaldist ("+
                "value  double"+
                ")");

        // Generate table values
        NormalDistribution nd = new NormalDistribution();
        for (int i = 0; i < 100000; ++i) {
            double sample = nd.sample();
            st.execute("insert into normaldist (value) values (" + sample + ")");
        }

        // Estimate standard deviation
        ResultSet rs = st.executeQuery("select * from normaldist");
        ArrayList<Double> values = new ArrayList<Double>();
        while (rs.next()) {
            values.add(rs.getDouble("value"));
        }

        // calculate 100 values of Xi
        double[] xis = new double[100];
        for (int i = 0; i < 100; ++i) {
            double sum = 0;
            for (int j = 0; j < 1000; ++j) {
                sum += values.get(100*i + j);
            }
            xis[i] = sum / 1000.0;
        }

        System.out.println("The estimated expected value of the standard deviation of Xi is:");
        System.out.println(new StandardDeviation().evaluate(xis));

        conn.close();
    }
}
