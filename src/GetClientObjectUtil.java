package com.otsi.action;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class GetClientObjectUtil {
	
	
static TransportClient client=null;
	
	static{
		// on startup

		try {
			System.out.println("Client Object");
		client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.80.15.102"), 9300)).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.80.15.106"), 9300));
			
		//	client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300)).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	
public static TransportClient  getObject(){
		return client;
	}

}
