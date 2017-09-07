package com.otsi.action;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

public class FieldNameInfo {
String index;
String feild;
Integer size;
String chartType;
String typeOfQuery;
public String getIndex() {
	return index;
}
public void setIndex(String index) {
	this.index = index;
}
public String getFeild() {
	return feild;
}
public void setFeild(String feild) {
	this.feild = feild;
}
public Integer getSize() {
	return size;
}
public void setSize(Integer size) {
	this.size = size;
}
public String getChartType() {
	return chartType;
}
public void setChartType(String chartType) {
	this.chartType = chartType;
}

public String getTypeOfQuery() {
	return typeOfQuery;
}
public void setTypeOfQuery(String typeOfQuery) {
	this.typeOfQuery = typeOfQuery;
}
@Override
public String toString() {
	return "FieldNameInfo [index=" + index + ", feild=" + feild + ", size=" + size + ", chartType=" + chartType
			+ ", typeOfQuery=" + typeOfQuery + "]";
}
static Connection con=null;

static ResultSet rs=null;
static Statement stmt=null;
static ArrayList<FieldNameInfo> getFeildInfo=new ArrayList();
static{
	
	try {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/proj1","root","root");
		stmt=con.createStatement();

		rs=stmt.executeQuery("select * from charttable");
	
		while(rs.next()){
			FieldNameInfo bean=new  FieldNameInfo();
			bean.setIndex(rs.getString(1));
			bean.setFeild(rs.getString(2));
			bean.setSize(rs.getInt(3));
			bean.setChartType(rs.getString(4));
			bean.setTypeOfQuery(rs.getString(5));
			getFeildInfo.add(bean);
			
			}
		System.out.println(getFeildInfo+"  data");
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	
	
}

public  static ArrayList<FieldNameInfo> getData(){

	return getFeildInfo;
	
}

public static void main(String[] args) {
	System.out.println("main");
	TransportClient client = GetClientObjectUtil.getObject();
	String fieldKeyWord="c_district_name.keyword";
	ArrayList datachart=new ArrayList<>();
	HashMap chartData=new HashMap<>();
	
System.out.println(" field keyword "+fieldKeyWord);
SearchResponse curnt_st_dist_count =null;
BoolQueryBuilder qb =  QueryBuilders.boolQuery()
.mustNot( QueryBuilders.termQuery(fieldKeyWord, ""))    
.mustNot( QueryBuilders.termQuery(fieldKeyWord, "0"))
.must( QueryBuilders.termQuery("c_state_name.keyword", "Delhi")); 
		curnt_st_dist_count =client.prepareSearch("rtaproject")
			       .setQuery(qb)
			       .addAggregation( AggregationBuilders.terms("termName").field(fieldKeyWord))
			       .execute()
			       .actionGet();
	
 
		Terms  termsDt =	curnt_st_dist_count.getAggregations().get("termName");
	
	List<Bucket> buckets_Dt = termsDt.getBuckets();
	
	for (Bucket bucket : buckets_Dt) {

			long count=bucket.getDocCount();
			String key=bucket.getKeyAsString();
			HashMap< String, Object> data=new HashMap<>();
			data.put( "name", key);
			data.put("count", count);
		    datachart.add(data);
		    
	}
	System.out.println( datachart);
}


}
