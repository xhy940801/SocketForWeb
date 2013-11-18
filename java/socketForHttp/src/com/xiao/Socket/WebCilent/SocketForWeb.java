package com.xiao.Socket.WebCilent;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class SocketForWeb
{
	private String host;
	private Socket webSocket;
	private InetAddress addr;
	public SocketForWeb(String url) throws UnknownHostException
	{
		host = url;
		webSocket = null;
		addr = InetAddress.getByName(url);
	}
	
	public SocketForWeb(String url,String ipAddr) throws UnknownHostException
	{
		host = url;
		webSocket = null;
		addr = InetAddress.getByName(ipAddr);
	}
	
	public SocketForWeb(String url,InetAddress ipAddr) throws UnknownHostException
	{
		host = url;
		webSocket = null;
		addr = ipAddr;
	}
	
	public HttpResponse GetForHttp()
	{
		HttpResponse HR = new HttpResponse();
		return HR;
	}
}
