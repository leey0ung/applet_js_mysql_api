import java.applet.*;

import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import netscape.javascript.JSObject;


public class MySQL extends Applet{
  //  Connection con;

  public Connection con2Mysql( String host, String database, String user, String passwd){
    String msg = "";
    String dst = "jdbc:mysql://"+host+"/"+database;
    try {
      // driver load
      Class.forName("com.mysql.jdbc.Driver").newInstance();
  
      // Connect to MySQL
      Connection con = DriverManager.getConnection(dst, user, passwd);
      return con;
    }catch (ClassNotFoundException e){
      msg = "fail driver loading 01";
      System.out.println(msg);
    }catch (SQLException e){
      msg = e.getMessage();
      e.printStackTrace();
    }catch (Exception e){
      msg = "fail driver loading 04";
      System.out.println(msg);
      e.printStackTrace();
    }
    return null;
  }

  public void executeSql( Connection con, String sqlStr, String functionName){
    execThread thread = new execThread(this,sqlStr,con,functionName);
    thread.start();
  }
  
    
    /**
     ** runSqlThread:sql実行するthread
     ** JsCallback: callbackで実行するjs関数
    **/
    public void runSqlThread(String host, String database, String user, String passwd,String sqlStr ,String JsCallback){
	SqlThread thread = new SqlThread(this, host, database, user, passwd, sqlStr, JsCallback);
	thread.start();
    }

  public String[][] executeSqlArray( Connection con, String sqlStr ){
    String msg = "";

    try {
    
      // Statement generation
      Statement stmt = con.createStatement();
      
      // execute SQL
      ResultSet rs = stmt.executeQuery(sqlStr);

      String result[][]  = convert2(rs);
      // session close
      rs.close();
      stmt.close();
      //System.out.println(result.toString());
      return result;
      
    }catch (SQLException e){
      msg = e.getMessage();
      e.printStackTrace();
    }catch (Exception e){
      msg = "fail";
      System.out.println(msg);
      e.printStackTrace();
    }
    return null;
  }

  public static String[][] convert2( ResultSet rs )
    throws SQLException
  {
    ResultSetMetaData rsmd = rs.getMetaData();
    rs.last();
    int numRows = rs.getRow();
    rs.first(); //first() method DBMS idependent
    int numColumns = rsmd.getColumnCount();
    String element[][] = new String[numRows+1][numColumns];
    
    //attribute name row +1
    for (int i=0; i<numColumns; i++) {
      String column_name = rsmd.getColumnName(i+1);
      element[0][i] = column_name;
    }
    //rs.first set 1, so after rs.next exec start 2 
    for (int i=0; i<numColumns; i++) {
      String column_name = rsmd.getColumnName(i+1);
      element[1][i] = rs.getString(column_name);
    }
    int rownum = 2;
    while(rs.next()){
      for (int i=0; i<numColumns; i++) {
        String column_name = rsmd.getColumnName(i+1);
        element[rownum][i] = rs.getString(column_name);
      }
      rownum = rownum + 1;
    }
    return element;
  } 

  //session close
  public void closeCon( Connection con ){
    String msg = "";
    try{
      con.close();
      System.out.println("session closed");
      return;
    }catch (SQLException e){
      msg = e.getMessage();
      e.printStackTrace();
    }catch (Exception e){
      msg = e.getMessage();     
      System.out.println(msg);
      e.printStackTrace();
    }
  }
  
  /*public void callbackMethod(String argument, String functionName) {
    execThread thread = new execThread(this,argument,functionName);
    thread.start();
    }*/

}
 