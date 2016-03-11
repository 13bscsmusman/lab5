/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab5ap;
import java.io.*;
import java.sql.*;
import java.util.*;


public class Lab5ap{

   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "root";
   
   
   public static void main(String[] args) throws Exception {
       
   Connection conn = null;
      Statement stmt = null;
      conn=databaseCreate(conn, stmt);
      fileRead(conn);
      findNearby(conn);
      System.out.println("Goodbye!");
      conn.close();
    }//end main
   public  static Connection databaseCreate(Connection conn, Statement stmt ) throws Exception {
       try{
      //STEP 2: Register JDBC driver
      
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);

      //STEP 4: Execute a query
      System.out.println("Creating database...");
      stmt = conn.createStatement();
      String sql = "DROP DATABASE IF EXISTS INFO";
      stmt.executeUpdate(sql);
      sql = "CREATE DATABASE INFO";
      stmt.executeUpdate(sql);
      
      System.out.println("Database created successfully...");
       System.out.println("Creating table in given database...");
      stmt = conn.createStatement();
      
      sql="USE INFO";
      stmt.executeQuery(sql);
      
      sql = "CREATE TABLE CITY " +
                   "(locId INTEGER not NULL, " +
                   " country VARCHAR(50), " + 
                   " region VARCHAR(50), " + 
                   " city VARCHAR(50), " + 
                   " postalCode VARCHAR(50), " +
                   " latitude DOUBLE, " +
                   " longitude DOUBLE, " +
                   " metroCode INTEGER DEFAULT NULL, " +
                   " areaCode INTEGER DEFAULT NULL, " +
                   " PRIMARY KEY ( locId ))"; 

      stmt.executeUpdate(sql);
      System.out.println("Created table in given database...");
      
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      //end finally try
   }//end try
       
       
     return conn;  
   }
   public static void fileRead( Connection conn ) throws Exception {

        BufferedReader CSVFile = 
        new BufferedReader(new FileReader("C:\\GeoLiteCity-Location.csv"));
        Statement stmt = conn.createStatement();
        CSVFile.readLine();
        CSVFile.readLine();
        String dataRow = CSVFile.readLine(); // Read first line.
        // The while checks to see if the data is null. If 
        // it is, we've hit the end of the file. If not, 
        // process the data.
        String sql="";
        int i=0;
        while (dataRow != null){
            i++;
            if(i==500) break;
            dataRow=dataRow.replace(",,",",NULL,");
            dataRow=dataRow.replace(",)",",NULL)");
            sql="INSERT INTO CITY(locId,country,region,city,postalCode,latitude,longitude,metroCode,areaCode) VALUES("+dataRow+");";
            sql=sql.replace(",,",",NULL,");
            sql=sql.replace(",)",",NULL)");
//            String[] dataArray = dataRow.split(",");
//            for (String item:dataArray) { 
//                System.out.print(item + "\t"); 
//            }
//            System.out.println(); // Print the data line.
            stmt.executeUpdate(sql);
            dataRow = CSVFile.readLine(); // Read next line of data.
        }
        // Close the file once all data has been read.
        CSVFile.close();

        // End the printout with a blank line.
        System.out.println();
        stmt.close();

    } 
   public static Double[] Coordinates( String c,Statement st ) throws SQLException{
        String sql="SELECT latitude,longitude from CITY where city=\"" +c+"\"";
        ResultSet rs = st.executeQuery(sql);
        Double longitude=0.0;
        Double latitude=0.0;
        while(rs.next()){
            latitude=rs.getDouble("latitude");
            longitude=rs.getDouble("longitude");
              
         }
         Double[] myList=new Double[2];
         myList[0]=latitude;
         myList[1]=longitude;
         return myList;
       
       
   }
   public static void findNearby( Connection conn ) throws Exception {
      Statement stmt = conn.createStatement();
      Scanner sc=new Scanner(System.in);
      String sql="";
      System.out.println("Press 1 to search using city name and 2 for latitude longitude"); 
      String input=sc.nextLine();
      String lat1="";
      String long1="";
      if(input.equals("1")){
          System.out.println("Enter City");
          String c=sc.nextLine();
          Double[] list=Coordinates(c,stmt);
          lat1=list[0].toString();
          long1=list[1].toString();
      }
      else{
        
     
        System.out.println("Enter Latitude");
        lat1=sc.nextLine();
        System.out.println("Enter Longitude");
        long1=sc.nextLine();
        
      }
      sql="SELECT city FROM CITY where DEGREES(ACOS(COS(RADIANS("+lat1+")) * COS(RADIANS(latitude)) *\n" +
"             COS(RADIANS("+long1+") - RADIANS(longitude)) +\n" +
"             SIN(RADIANS("+lat1+")) * SIN(RADIANS(latitude)))) < 5";
      
       ResultSet rs = stmt.executeQuery(sql);
      //STEP 5: Extract data from result set
        while(rs.next()){
         //Retrieve by column name
         String city = rs.getString("city");
         System.out.println(", City: " + city);
      }
      rs.close();
      stmt.close();
       
       
   }
   
   
} // CSVRead 
