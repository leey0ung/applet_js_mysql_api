applet = document.getElementById('mysql-connection');
//class
function SocialSqlDb(){
  this.con=null;
}


//SocialSqlDb.prototype.executeSql = function( str ){
//    return applet.executeSql( this.con, str );
//}

//SocialSqlDb.prototype.executeSql = function( str, funcName ){
//    return applet.executeSql( this.con, str, funcName );
//}

SocialSqlDb.prototype.executeSqlArray = function( str ){
    return applet.executeSqlArray( this.con, str );
}


SocialSqlDb.prototype.close = function(){
    applet.closeCon( this.con );
}

function connectDatabase( host, port, database,user,password ){
  var db = new SocialSqlDb();
  db.con = applet.con2Mysql( host, database, user, password );
  return db;
}


SocialSqlDb.prototype.runSqlThread = function(host, database, user, password,sqlStr,callback){
    applet.runSqlThread(host,database,user,password,sqlStr,callback);
}

// arguments object: 呼び出し元から渡された変数を格納する
function display(){
    // convert to JSON Object Array
    var jsObj = $.parseJSON(arguments[0]);
    var thread_id = arguments[1];

    //create the div and table of thread
    $("#sqlOutput").append($("<div id=thread_"+thread_id+"></div>").append("<table></table>"));
    
    // iterate  JSON Array
    var count = 0;
    $("#thread_"+thread_id+" table").append($("<thead></thead>").append($("<tr></tr>")));
    $.each(jsObj,function(){
	if(count == 0){
	    // add head of table
	    for(var key in this){
		$("#thread_"+thread_id+" table thead tr").append($("<th></th>").text(key));
	    }
	    $("#thread_"+thread_id+" table").append($("<tbody></tbody>"));
	}
	var tr = $("#thread_"+thread_id+" table tbody").append($("<tr></tr>"));
	for(var key in this){
	    tr.append($("<td></td>").text(this[key]));
	   }
	count++;
});
}