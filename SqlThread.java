import java.applet.Applet;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import netscape.javascript.JSObject;

class SqlThread extends Thread {
  private Applet applet;
  private String conStr; 
  private String sqlStr;
  private String JsCallback;
    private Connection con;

    public SqlThread(Applet applet, String host, String database, String user, String passwd,String sqlStr,String JsCallback){
	this.applet = applet;
	this.conStr= "jdbc:mysql://"+host+"/"+database+"?user="	+user+"&password="+passwd;
	this.sqlStr = sqlStr;
	this.JsCallback = JsCallback;
	this.con = null;
    }

    private void con2Mysql(){
	String msg="";
	try {
      // driver load
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      // Connect to MySQL
      this.con = DriverManager.getConnection(this.conStr);
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
    }

  //session close
  public void closeCon(){
    String msg = "";
    try{
      this.con.close();
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

  public void run() {
    long threadId = Thread.currentThread().getId();
    try {
      sleep(3000);
    } catch (InterruptedException e) {
    }
    System.out.println("Thread # " + Long.toString(threadId) + " is doing this task");

    // execute sql
    JSONArray result = executeSql(this.sqlStr);
    //run js function through callback,parametrs are json data and thread's id.
    JSObject obj = JSObject.getWindow(this.applet);
    obj.call(this.JsCallback, new Object[] { result, threadId });
    // con close
    this.closeCon();

  }

  public JSONArray executeSql( String sqlStr ){
    String msg = "";
    JSONArray result = new JSONArray();
    try {
       this.con2Mysql();
      // Statement generation
      Statement stmt = this.con.createStatement();
      
      // execute SQL
      ResultSet rs = stmt.executeQuery(sqlStr);    
      result = convert1(rs);
      // session close
      rs.close();
      stmt.close();
      return result;
    }catch (SQLException e){
      msg = e.getMessage();
      e.printStackTrace();
    }catch (Exception e){
      msg = "fail";
      System.out.println(msg);
      e.printStackTrace();
    }
  
    return result;
  }
  
  public static JSONArray convert1( ResultSet rs )
    throws SQLException, JSONException
  {
    JSONArray json = new JSONArray();
    ResultSetMetaData rsmd = rs.getMetaData();
    
    while(rs.next()) {
      int numColumns = rsmd.getColumnCount();
      JSONObject obj = new JSONObject();
      
      for (int i=1; i<numColumns+1; i++) {
        String column_name = rsmd.getColumnName(i);
        
        if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
          obj.put(column_name, rs.getArray(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
          obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
          obj.put(column_name, rs.getBoolean(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
          obj.put(column_name, rs.getBlob(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
          obj.put(column_name, rs.getDouble(column_name)); 
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
         obj.put(column_name, rs.getFloat(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
          obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
          obj.put(column_name, rs.getNString(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
          obj.put(column_name, rs.getString(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
          obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
          obj.put(column_name, rs.getInt(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
          obj.put(column_name, rs.getDate(column_name));
        }
        else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
          obj.put(column_name, rs.getTimestamp(column_name));   
        }
        else{
          obj.put(column_name, rs.getObject(column_name));
        }
      }
      
      json.put(obj);
    }
    
    return json;
  } 


}