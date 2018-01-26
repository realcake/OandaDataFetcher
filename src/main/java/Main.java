import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Sean McKenna on 10/5/2017.
 */
public class Main {
    public static void main(String[] args) {

        Connection con = null;
        PreparedStatement pst = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://10.0.0.8:3306/rates"; //your own database
        String user = "user";
        String password = "password";


        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION(), CURRENT_DATE");

            if (rs.next()) System.out.println(rs.getString(1) + ", " + rs.getString(2));
            //START REQUEST

            LocalDateTime finalStartDate = LocalDateTime.of(2017,6,11,0,0); //LocalDateTime.of(2005,1,1,0,0);
            LocalDateTime finalEndDate = LocalDateTime.of(2017,10,5,0,0);

            LocalDateTime start = finalStartDate;
            LocalDateTime end   = start.plusDays(3);


            //FORMAT REQUEST
            start = start.minusDays(3);
            end = start.minusDays(3);


            List<Long> times = new ArrayList<Long>();

            //Stopwatch watch = Stopwatch.createStarted();
            while(end.isBefore(finalEndDate)){
                //Stopwatch perWatch = Stopwatch.createStarted();
                start = start.plusDays(3);
                end = start.plusDays(3);


                DataRequest request = new DataRequest("EUR_USD",start.toEpochSecond(ZoneOffset.ofHours(-5)),end.toEpochSecond(ZoneOffset.ofHours(-5)),"M1");
                try {
                    List<Rate> rates =  request.makeRequest();
                    for(Rate rate: rates) {
                        //System.out.println(rate.toString());
                        System.out.println(LocalDateTime.ofEpochSecond(rate.getTime(),0,ZoneOffset.ofHours(-5)));
                        long time = rate.getTime() * 1000;
                        Timestamp timestamp = new Timestamp(time);
                        //System.out.println("Time: "+timestamp.toString());
                        String query ="insert ignore into EURUSD_M1 (id, open, high, low, close, volume)"+ " values (?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStmt = con.prepareStatement(query);
                        preparedStmt.setTimestamp (1, timestamp);
                        preparedStmt.setFloat (2, rate.getOpen());
                        preparedStmt.setFloat (3, rate.getHigh());
                        preparedStmt.setFloat (4, rate.getLow());
                        preparedStmt.setFloat (5, rate.getClose());
                        preparedStmt.setInt (6, rate.getVolume());
                        preparedStmt.execute();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {

            try {
                if (pst != null) {
                    pst.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {

                Logger lgr = Logger.getLogger(Main.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }


    }

    private static double calculateAverage(List<Long> times) {
        Long sum = 0l;
        if(!times.isEmpty()) {
            for (Long time : times) {
                sum += time;
            }
            return sum.doubleValue() / times.size();
        }
        return sum;
    }
}
