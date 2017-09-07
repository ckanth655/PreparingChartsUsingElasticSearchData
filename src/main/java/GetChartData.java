package com.otsi.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Order;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetChartData
 */
public class GetChartData extends HttpServlet {
	
	
   

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetChartData() {
    	
        super();
        
    }
    static TransportClient client=null;
	  
    static{
    	client  =GetClientObjectUtil.getObject();	
    }
    
  
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json");
	
		HttpSession session = request.getSession();
		
		ArrayList<FieldNameInfo> getFeildInfo=FieldNameInfo.getData();
		
		HashMap<String ,Object> chartsData=new HashMap<>();
			
		
			/**
			 * for each field getting chart data by calling respective method and stored in map and adding this data to chartData map
			 */
		String key=null,val=null;
		Enumeration<String> attributeNames =null;
	
		ArrayList<String> sesNames=new ArrayList<>();
		ArrayList<String> sesVal=new ArrayList<>();
		
		try{
			
			 key=request.getParameter("key").toString();
			val=request.getParameter("val").toString();
		}catch (Exception e) {
			Enumeration<String> sessionAttr = session.getAttributeNames();

			while (sessionAttr.hasMoreElements()) {
				
					session.removeAttribute(sessionAttr.nextElement());
				
			}
		}
		
		
		try{
			attributeNames = session.getAttributeNames();
		while(attributeNames.hasMoreElements()){
				String nextElement = attributeNames.nextElement();
				
				sesNames.add(nextElement);
				sesVal.add(session.getAttribute(nextElement).toString());
				
			}//while
				
		}catch (Exception e) {
			// TODO: handle exception
		}
		int index=0;
			for (FieldNameInfo fieldNameInfo : getFeildInfo) {
				index++;
				HashMap chartData=null;
				String fieldKeyWord=fieldNameInfo.getFeild();
				if(fieldNameInfo.getTypeOfQuery().equals("term")){
					 BoolQueryBuilder qb =  QueryBuilders.boolQuery()
							    .mustNot( QueryBuilders.termQuery(fieldKeyWord, ""))    
							    .mustNot( QueryBuilders.termQuery(fieldKeyWord, "0"))
							    .must( QueryBuilders.termQuery("c_state_name.keyword", "Delhi"));  
					 boolean flag=false;
					 try{
					
	qb.must(QueryBuilders.termQuery(key,val));
	System.out.println(key+" val "+val);
	
	for (int i = 0; i < sesNames.size(); i++) {
		 qb.must(QueryBuilders.termQuery(sesNames.get(i),sesVal.get(i)));	
		  System.out.println(sesNames.get(i) + " = " + sesVal.get(i));
	}
	
	
				
					 }catch (Exception e) {}
					 
//					 if(!flag){
							chartData = getChartDataTerm(fieldNameInfo,qb);
//						}	
				}//if
				/*else if(fieldNameInfo.getTypeOfQuery().equals("date")){
					chartData = getChartDataDate(fieldNameInfo);
				}*/
				chartsData.put(fieldNameInfo.getChartType()+index,chartData);
			}//foreach 
			try{
				session.setAttribute(key,val);
			}catch (Exception e) {
				// TODO: handle exception
			}
			
			Gson gson=new Gson();
		
			
			String json = gson.toJson(chartsData);
			
			
			response.getWriter().println(json);
				
	}//doGet()
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
public      HashMap   getChartDataTerm(FieldNameInfo object,BoolQueryBuilder qb){
	
	String fieldKeyWord=object.getFeild();
	ArrayList datachart=new ArrayList<>();
	HashMap chartData=new HashMap<>();
	chartData.put("charttype", object.getChartType());
	chartData.put("fieldinfo", object.getFeild());
System.out.println(" field keyword "+fieldKeyWord);
SearchResponse curnt_st_dist_count =null;
	if(object.getChartType()!="pie"){
		curnt_st_dist_count =client.prepareSearch(object.getIndex())
			       .setQuery(qb)
			       .addAggregation( AggregationBuilders.terms("termName").field(fieldKeyWord).size(object.getSize()))
			       .execute()
			       .actionGet();
	}else{
		curnt_st_dist_count =client.prepareSearch(object.getIndex())
			       .setQuery(qb)
			       .addAggregation( AggregationBuilders.terms("termName").field(fieldKeyWord))
			       .execute()
			       .actionGet();
	}
 
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
	 chartData.put("data", datachart);
	return chartData;
}//getChartDataTerm
public HashMap getChartDataDate(FieldNameInfo object){
	
	HashMap chartData=new HashMap<>();
	ArrayList datachart=new ArrayList<>();
	chartData.put("charttype",object.getChartType());
	chartData.put("fieldinfo", object);
	String field=object.getFeild();
	 BoolQueryBuilder qb =  QueryBuilders.boolQuery()
			    .mustNot( QueryBuilders.termQuery(field, ""))    
			    .mustNot( QueryBuilders.termQuery(field, "0"))
			    .must( QueryBuilders.termQuery("c_state_name.keyword", "Delhi"))
			    .filter( QueryBuilders.rangeQuery(field).format("dd/MM/yyyy||yyyy").gt("31/12/2013").lte("31/12/2017"))
			    ;  
	 Order order=Order.KEY_ASC;
	 AggregationBuilder aggrg=AggregationBuilders.dateHistogram("yr").field(field). minDocCount(5).interval(1).dateHistogramInterval(DateHistogramInterval.YEAR).order(order);
		
	SearchResponse curnt_st_year_count =client.prepareSearch(object.getIndex())
		       .setQuery(qb)
	          .addAggregation( aggrg)
		       .execute()
		       .actionGet();
					
				InternalDateHistogram   terms1 =curnt_st_year_count.getAggregations().get("yr");
						for (org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram.Bucket bucket2 : terms1.getBuckets()) {
					int key = ((org.joda.time.DateTime) bucket2.getKey()).getYear();
					long count=bucket2.getDocCount();
					HashMap< String, Object> data=new HashMap<>();
					data.put( "name", key);
					data.put("count", count);
				  datachart.add(data);
						}
						 chartData.put("data", datachart);
	return chartData;
}//getChartDataDate
	

}
