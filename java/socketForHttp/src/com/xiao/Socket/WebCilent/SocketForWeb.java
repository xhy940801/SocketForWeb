package com.xiao.Socket.WebCilent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class SocketForWeb
{
	public static final int defaultHttpPort = 80;
	public static final int defaultHttpsPort = 443;
	
	private String host;
	private Socket webSocket;
	private InetAddress addr;
	
	private BufferedWriter bufWriter;
	private BufferedReader bufReader;
	
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
	
	public HttpResponse GetForHttp(String path,KeyValuePair[] keyValuePairs,CookieInfo[] cookies,String accept,String acceptLanguage,int port) throws IOException
	{
		HttpResponse HR = new HttpResponse();
		String req = this.setHttpHead(path, keyValuePairs, cookies, accept, acceptLanguage);
		this.sendRequestAndAnalysisStatus(req, HR, port);
		int contentLength = this.analysisHead(HR);
		if(contentLength < 0)
			this.setBodyByChunk(HR);
		else
			this.setBodyByContentLength(HR, contentLength);
		return HR;
	}
	
	protected void setBodyByContentLength(HttpResponse HR, int length) throws IOException
	{
		char[] charArray = new char[length];
	//	StringBuffer bodyStrBuf = new StringBuffer(10000);
		for(int i=0;i<length-1;)
		{
			i += this.bufReader.read(charArray, i, length - i);
		}
		HR.setBody(new String(charArray));
	}
	
	protected void setBodyByChunk(HttpResponse HR) throws IOException
	{
		StringBuffer bodyStrBuf = new StringBuffer(10000);
		while(true)
		{
			String strLength = this.bufReader.readLine();
			if(strLength == null)
			{
				//Throw a exception
			}
			if(strLength.equals(""))
				continue;
			int length; 
			if((length = Integer.parseInt(strLength.replaceAll("^\\s*|\\s*$", ""),16)) > 0)
			{
				char[] strChunk = new char[length];
				for(int i=0;i<length-1;)
				{
					i += this.bufReader.read(strChunk, i, length - i);
				}
				bodyStrBuf.append(strChunk);
			}
			else
				break;
		}
		HR.setBody(bodyStrBuf.toString());
	}
	
	protected String setHttpHead(String path,KeyValuePair[] keyValuePairs,CookieInfo[] cookies,String accept,String acceptLanguage)
	{
		StringBuffer strBuf = new StringBuffer(350);
		strBuf.append("GET " + path);
		if(keyValuePairs != null && keyValuePairs.length > 0)
		{
			strBuf.append('?');
			strBuf.append(keyValuePairs[0].key);
			strBuf.append('=');
			strBuf.append(keyValuePairs[0].value);
			for(int i=1;i<keyValuePairs.length;++i)
			{
				strBuf.append('&');
				strBuf.append(keyValuePairs[i].key);
				strBuf.append('=');
				strBuf.append(keyValuePairs[i].value);
			}
		}
		
		strBuf.append(" HTTP/1.1\r\n");
		strBuf.append("Accept: " + accept + "\r\n");
		strBuf.append("Accept-Language: " + acceptLanguage + "\r\n");
		strBuf.append("Connection: Keep-Alive\r\n");
		strBuf.append("Host: " + this.host + "\r\n");
		if(cookies != null && cookies.length > 0)
		{
			strBuf.append("Cookie:");
			for(int i=0;i<cookies.length;++i)
			{
				strBuf.append(' ');
				strBuf.append(cookies[i].getKey());
				strBuf.append('=');
				strBuf.append(cookies[i].getValue());
				strBuf.append(';');
			}
			strBuf.setCharAt(strBuf.length()-1, '\r');
			strBuf.append('\n');
		}
		strBuf.append("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.48 Safari/537.36\r\n\r\n");
		return strBuf.toString();
	}
	
	protected void sendRequestAndAnalysisStatus(String req, HttpResponse HR, int port) throws IOException
	{
		if(this.webSocket == null)
		{
			this.socketConnection(port);
			this.bufWriter.write(req);
			this.bufWriter.flush();
			String status = this.bufReader.readLine();
			this.analysisStatus(status, HR);
		}
		else
		{
			this.bufWriter.write(req);
			this.bufWriter.flush();
			String status = this.bufReader.readLine();
			if(status != null)
			{
				this.analysisStatus(status, HR);
			}
			else
			{
				this.webSocket.close();
				this.socketConnection(port);
				status = this.bufReader.readLine();
				if(status != null)
					this.analysisStatus(status, HR);
	/*			else
					throw */
			}
		}
	}
	
	protected void analysisStatus(String statusStr, HttpResponse HR) throws NumberFormatException
	{
		String[] arr = statusStr.split(" ", 3);
		HR.setVersion(arr[0]);
		//Throw a exception
		HR.setStatus(Integer.parseInt(arr[1]));
	}
	
	protected void socketConnection(int port) throws IOException
	{
		this.webSocket = new Socket(this.addr,port);
		this.webSocket.setSoTimeout(2000);
		this.bufWriter = new BufferedWriter(new OutputStreamWriter(this.webSocket.getOutputStream()));
		this.bufReader = new BufferedReader(new InputStreamReader(this.webSocket.getInputStream(),"ascii"));
	}
	
	protected int analysisHead(HttpResponse HR) throws IOException
	{
		int contentLength = -1;
		boolean inContinue = true;
		StringBuffer strBuffForOtherInfo= new StringBuffer(50);
		while(inContinue)
		{
			String line = this.bufReader.readLine();
			if(line == null)
			{
				//Throw something
			}
			String[] arr = line.split(":",2);
			try
			{
				switch(arr[0].toLowerCase())
				{
				case "date":
					this.setDate(HR, arr[1]);
					break;
				case "connection":
					if(this.weekEqual(arr[1],"keep-alive"))
						break;
					else
					{
						this.webSocket.close();
						this.webSocket = null;
						break;
					}
				case "content-type":
					this.setContentType(HR, arr[1]);
					break;
				case "expires":
					this.setExpires(HR, arr[1]);
					break;
				case "content-length":
					contentLength = Integer.parseInt(arr[1].replaceAll("^\\s*|\\s*$", ""));
					break;
				case "set-cookie":
					HR.getCookieList().addCookie(new CookieInfo(arr[1]));
					break;
				case "":
					inContinue = false;
					break;
				default:
					strBuffForOtherInfo.append(arr[0]);
					strBuffForOtherInfo.append(":");
					strBuffForOtherInfo.append(arr[1]);
					strBuffForOtherInfo.append("\r\n");
					break;
				}
			}
			catch(ParseException | AnalysisError p)
			{
				p.printStackTrace();
			}
		}
		HR.setOtherInfomation(strBuffForOtherInfo.toString());
		return contentLength;
	}
	
	protected void setDate(HttpResponse HR, String strDate) throws ParseException
	{
		HR.setDate(this.turnStrTodate(strDate));
	}
	
	protected void setExpires(HttpResponse HR, String strDate) throws ParseException
	{
		HR.setExpires(this.turnStrTodate(strDate));
	}
	
	protected java.util.Date turnStrTodate(String strDate) throws ParseException
	{
		strDate = strDate.replaceFirst("^\\s*", "");
		strDate = strDate.replaceAll("\\s,|,\\s", ",");
		SimpleDateFormat simpleDateFormat;
		if(strDate.matches("^[a-zA-Z]+,\\d{2}-[a-zA-Z]+-\\d{2,4} \\d{2}:\\d{2}:\\d{2} [a-zA-Z]+ *$"))
			simpleDateFormat = new java.text.SimpleDateFormat("E,d-MMM-y H:m:s z",Locale.US);
		else if(strDate.matches("^[a-zA-Z]+,\\d{2} [a-zA-Z]+ \\d{2,4} \\d{2}:\\d{2}:\\d{2} [a-zA-Z]+ *$"))
			simpleDateFormat = new java.text.SimpleDateFormat("E,d MMM y H:m:s z",Locale.US);
		else if(strDate.matches("^[a-zA-Z]+,\\d{2}-\\d{2}+-\\d{2,4} \\d{2}:\\d{2}:\\d{2} [a-zA-Z]+ *$"))
			simpleDateFormat = new java.text.SimpleDateFormat("E,d-M-y H:m:s z",Locale.US);
		else
			simpleDateFormat = new java.text.SimpleDateFormat("E,d MMM y H:m:s Z",Locale.US);
		return simpleDateFormat.parse(strDate);
	}
	
	protected void setContentType(HttpResponse HR, String strContentType)
	{
		String[] arr = strContentType.split(";",2);
		if(arr.length < 1)
		{
			//Throw a exception
		}
		HR.setDataType(arr[0]);
		if(arr.length > 1)
		{
			String[] arrCharset = arr[1].split("=",2);
			if(arrCharset.length == 2 && this.weekEqual(arrCharset[0],"charset"))
				HR.setCharset(arrCharset[1]);
		}
	}
	
	protected boolean weekEqual(String str1, String str2)
	{
		return str1.replaceAll("^\\s*|\\s*$", "").toLowerCase().equals(str2.replaceAll("^\\s*|\\s*$", "").toLowerCase());
	}
}
