package com.xiao.Socket.WebCilent;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HttpsCilent extends HttpCilent
{

	public HttpsCilent()
	{
		
	}

	public HttpsCilent(String host) throws UnknownHostException,
			WebCilentException
	{
		super(host);
	}

	public HttpsCilent(String host, String ipAddr) throws WebCilentException
	{
		super(host, ipAddr);
	}

	public HttpsCilent(String host, InetAddress ipAddr)
			throws WebCilentException
	{
		super(host, ipAddr);
	}

	public HttpsCilent(String host, int port) throws UnknownHostException,
			WebCilentException
	{
		super(host, port);
	}

	public HttpsCilent(String host, String ipAddr, int port)
			throws WebCilentException
	{
		super(host, ipAddr, port);
	}

	public HttpsCilent(String host, InetAddress ipAddr, int port)
			throws WebCilentException
	{
		super(host, ipAddr, port);
	}
	
	protected WebCilentSocket getSocket()
	{
		return new HttpsSocket();
	}

}
