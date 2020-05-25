package edu.daiict.other;




import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private DBConnection() 
            throws ClassNotFoundException, SQLException {
        Class.forName( DBConfig.DB_DRIVER ); 
        
        con = DriverManager.getConnection(
                DBConfig.DB_URL, // + "?searchpath=company",
                DBConfig.DB_USER, 
                DBConfig.DB_PWD );
        System.out.println("connected");
    }
      
    public static DBConnection getInstance() 
            throws ClassNotFoundException, SQLException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException{
        return con;
    }

    public Connection getConnectionSchema(String schema) throws SQLException{
        //String schema="company";
        //con.setSchema("company");
        Statement st = con.createStatement();
        st.execute("set search_path to " + schema + ";");
        return con;
    }

    public void close() throws SQLException{
        con.close();
        instance = null;
    }

    private final Connection con;
    private static DBConnection instance = null;
    
}