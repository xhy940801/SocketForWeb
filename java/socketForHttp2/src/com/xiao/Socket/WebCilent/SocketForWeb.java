package com.xiao.Socket.WebCilent;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
/**
 * @author xiao
 * @version 1.0
 * This class can catch the html from web,just like HttpCilent.
 */

public class SocketForWeb
{
	private static int defaultHttpPort = 80;
	private static int defaultHttpsPort = 443;
	private static String defaultAccept = "*/*";
	private static String defaultAcceptLanguage = "zh-cn";
	
	private int bufLength = 10000;
	private int timeOut = 2000;
	
	private final String host;
	private Socket webSocket;
	private final InetAddress addr;
	
	private BufferedWriter bufWriter;
	private AsciiReader bufReader;
	/**
	 * One object of SocketForWeb must has a fixed host
	 * @param url the host url(like www.baidu.com) The function will automatic turn the host name to a ip address.
	 */
	public SocketForWeb(String url) throws UnknownHostException
	{
		host = url;
		webSocket = null;
		addr = InetAddress.getByName(url);
	}
	/**
	 * One object of SocketForWeb must has a fixed host
	 * @param url the host url(like "www.baidu.com") The function will automatic turn the host name to a ip address.
	 * @param ipAddr a string base ip address name(like "127.0.0.1")
	 */
	public SocketForWeb(String url,String ipAddr) throws UnknownHostException
	{
		host = url;
		webSocket = null;
		addr = InetAddress.getByName(ipAddr);
	}
	/**
	 * One object of SocketForWeb must has a fixed host
	 * @param url the host url(like "www.baidu.com") The function will automatic turn the host name to a ip address.
	 * @param ipAddr a InetAddress object
	 */
	public SocketForWeb(String url,InetAddress ipAddr)
	{
		host = url;
		webSocket = null;
		addr = ipAddr;
	}
	/**
	 * This function will simple call SocketForWeb.postForHttp(path, null, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse postForHttp(String path) throws AnalysisError, IOException, WebCilentException
	{
		return this.postForHttp(path, null, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	/**
	 * This function will simple call SocketForWeb.postForHttp(path, keyValuePairs, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse postForHttp(String path,KeyValuePair[] keyValuePairs,CookieInfo[] cookies) throws AnalysisError, IOException, WebCilentException
	{
		return this.postForHttp(path, keyValuePairs, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	/**
	 * This function will simple call SocketForWeb.postForHttp(path, null, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse postForHttp(String path,CookieInfo[] cookies) throws AnalysisError, IOException, WebCilentException
	{
		return this.postForHttp(path, null, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	/**
	 * This function will simple call SocketForWeb.postForHttp(path, keyValuePairs, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse postForHttp(String path,KeyValuePair[] keyValuePairs) throws AnalysisError, IOException, WebCilentException
	{
		return this.postForHttp(path, keyValuePairs, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	/**
	 * This function will return a HttpResponse object, which include all the information the class catch from web.
	 * 
	 * @param path A path from host to you want(if you want to browse www.baidu.com, you must set the host to "www.baidu.com" in contructer and the path must be "/".if you want to browse www.baidu.com/s you must set the path to "/s")
	 * @param keyValuePairs The request which you want to send
	 * @param Cookies You can get it from HttpResponse.getCookieList().getValidCookieArray();
	 * @param accept The http head "Accept".default is "*(This is null)/*" you can change the SocketForWeb.defaultAccept
	 * @param acceptLanguage default is "zh-cn" you can change the SocketForWeb.defaultAcceptLanguage
	 * @param port the port for http, default is 80 you can change the SocketForWeb.defaultHttpPort
	 * 
	 * @see com.xiao.Socket.WebCilent.CookieInfo
	 * @see com.xiao.Socket.WebCilent.KeyValuePair
	 */
	public HttpResponse postForHttp(String path,KeyValuePair[] keyValuePairs,CookieInfo[] cookies,String accept,String acceptLanguage,int port) throws AnalysisError, IOException, WebCilentException
	{
		String req = this.setHttpHeadForPost(path, keyValuePairs, cookies, accept, acceptLanguage);
		return this.sendAndGetResponse(req, port);
	}
	/**
	 * This function will return a HttpResponse object, which include all the information the class catch from web.
	 * 
	 * @param path A path from host to you want(if you want to browse www.baidu.com, you must set the host to "www.baidu.com" in contructer and the path must be "/".if you want to browse www.baidu.com/s you must set the path to "/s")
	 * @param keyValuePairs The request which you want to send
	 * @param Cookies You can get it from HttpResponse.getCookieList().getValidCookieArray();
	 * @param accept The http head "Accept".default is "*(This is null)/*" you can change the SocketForWeb.defaultAccept
	 * @param acceptLanguage default is "zh-cn" you can change the SocketForWeb.defaultAcceptLanguage
	 * @param port the port for http, default is 80 you can change the SocketForWeb.defaultHttpPort
	 * 
	 * @see com.xiao.Socket.WebCilent.CookieInfo
	 * @see com.xiao.Socket.WebCilent.KeyValuePair
	 */
	public HttpResponse getForHttp(String path,KeyValuePair[] keyValuePairs,CookieInfo[] cookies,String accept,String acceptLanguage,int port) throws AnalysisError, IOException, WebCilentException
	{
		String req = this.setHttpHeadForGet(path, keyValuePairs, cookies, accept, acceptLanguage);
		return this.sendAndGetResponse(req, port);
	}
	/**
	 * This function will simple call SocketForWeb.getForHttp(path, null, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse getForHttp(String path) throws AnalysisError, IOException, WebCilentException
	{
		return this.getForHttp(path, null, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	/**
	 * This function will simple call SocketForWeb.getForHttp(path, keyValuePairs, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse getForHttp(String path, KeyValuePair[] keyValuePairs) throws AnalysisError, IOException, WebCilentException
	{
		return this.getForHttp(path, keyValuePairs, null, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	/**
	 * This function will simple call SocketForWeb.getForHttp(path, null, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse getForHttp(String path, CookieInfo[] cookies) throws AnalysisError, IOException, WebCilentException
	{
		return this.getForHttp(path, null, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	/**
	 * This function will simple call SocketForWeb.getForHttp(path, keyValuePairs, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	 */
	public HttpResponse getForHttp(String path, KeyValuePair[] keyValuePairs, CookieInfo[] cookies) throws AnalysisError, IOException, WebCilentException
	{
		return this.getForHttp(path, keyValuePairs, cookies, SocketForWeb.defaultAccept, SocketForWeb.defaultAcceptLanguage, SocketForWeb.defaultHttpPort);
	}
	
	protected HttpResponse sendAndGetResponse(String req, int port) throws AnalysisError, IOException, WebCilentException
	{
		HttpResponse HR = new HttpResponse();
		this.sendRequestAndAnalysisStatus(req, HR, port);
		int contentLength = this.analysisHead(HR);
		byte[] bodyBytes;
		if(contentLength < 0)
			bodyBytes = this.getBodyBytesByChunk();
		else
			bodyBytes = this.getBodyBytesByContentLength(contentLength);
		if(HR.getContentEncoding() != null)
			bodyBytes = this.decodeBody(HR, bodyBytes);
		HR.setBody(bodyBytes);
		return HR;
	}
	
	protected byte[] decodeBody(HttpResponse HR, byte[] bodyBytes) throws AnalysisError
	{
		if(HR.getContentEncoding().equals("gzip"))
		{
			try
			{
				bodyBytes = this.gzipDecode(bodyBytes);
				HR.setContentEncoding("");
				return bodyBytes;
			} catch (IOException e)
			{
				e.printStackTrace();
				throw new AnalysisError("Have an io error in gzip decorder\r\nFunction: protected byte[] gzipDecode(byte[] byteGzip) throws IOException"
						,ConstValue.CLASSNAME.SOCKETFORWEB|ConstValue.FUNCTIONNAME.GZIPDECODE|
						ConstValue.ERRORTYPE.IOERROR|ConstValue.OTHERINFORMATION.NONE);
			}
		}
		else
			return bodyBytes;
	}
	
	protected byte[] gzipDecode(byte[] byteGzip) throws IOException
	{
		ByteArrayInputStream bIS = new ByteArrayInputStream(byteGzip);
		GZIPInputStream gzipIS = new GZIPInputStream(bIS);
		ByteArray decodeByte = new ByteArray(byteGzip.length << 1);
		
		byte[] buffer = new byte[byteGzip.length];
		int count;
		while((count = gzipIS.read(buffer, 0, buffer.length)) != -1)
			decodeByte.add(buffer, 0, count);
		return decodeByte.toArray();
	}
	
	protected byte[] getBodyBytesByContentLength(int length) throws IOException, WebCilentException
	{
		byte[] byteArray = new byte[length];
		for(int i=0;i<length;)
		{
			i += this.bufReader.read(byteArray, i, length - i);
			if(i == -1)
			{
				throw new WebCilentException("The http response from web has a unexpected end!\r\nFunction: protected byte[] getBodyBytesByContentLength(int length) throws IOException, WebCilentException"
						,ConstValue.CLASSNAME.SOCKETFORWEB|ConstValue.FUNCTIONNAME.GETBODYBYTESBYCONTENTLENGTH|
						ConstValue.ERRORTYPE.UNEXPECTEND|ConstValue.OTHERINFORMATION.NONE);
			}
		}
		return byteArray;
	}
	
	protected byte[] getBodyBytesByChunk() throws IOException, WebCilentException
	{
		ByteArray byteArray = new ByteArray(this.bufLength);
		while(true)
		{
			String strLength = this.bufReader.readLine();
			if(strLength == null)
			{
				throw new WebCilentException("The http response from web has a unexpected end!\r\nFunction: protected byte[] getBodyByteByChunk() throws IOException, WebCilentException"
						,ConstValue.CLASSNAME.SOCKETFORWEB|ConstValue.FUNCTIONNAME.GETBODYBYTESBYCHUNK|
						ConstValue.ERRORTYPE.UNEXPECTEND|ConstValue.OTHERINFORMATION.NONE);
			}
			if(this.weekEqual(strLength, ""))
				continue;
			int length; 
			if((length = Integer.parseInt(strLength.replaceAll("^\\s*|\\s*$", ""),16)) > 0)
			{
				byte[] strChunk = new byte[length];
				for(int i=0;i<length;)
				{
					i += this.bufReader.read(strChunk, i, length - i);
				}
				byteArray.add(strChunk, 0, length);
			}
			else
				break;
		}
		return byteArray.toArray();
	}
	
	protected String setHttpHeadForPost(String path,KeyValuePair[] keyValuePairs,CookieInfo[] cookies,String accept,String acceptLanguage)
	{
		StringBuffer strBuf = new StringBuffer(350);
		StringBuffer kvBuf = new StringBuffer(50);
		if(keyValuePairs != null && keyValuePairs.length > 0)
		{
			kvBuf.append(keyValuePairs[0].key);
			kvBuf.append('=');
			kvBuf.append(keyValuePairs[0].value);
			for(int i=1;i<keyValuePairs.length;++i)
			{
				kvBuf.append('&');
				kvBuf.append(keyValuePairs[i].key);
				kvBuf.append('=');
				kvBuf.append(keyValuePairs[i].value);
			}
		}
		strBuf.append("POST "+path+ " HTTP/1.1\r\n");
		strBuf.append("Accept: " + accept + "\r\n");
		strBuf.append("Accept-Language: " + acceptLanguage + "\r\n");
		strBuf.append("Accept-Encoding: gzip\r\n");
		strBuf.append("Content-Type: application/x-www-form-urlencoded\r\n");
		strBuf.append("Connection: Keep-Alive\r\n");
		strBuf.append("Host: " + this.host + "\r\n");
		strBuf.append("Content-Length: " + String.valueOf(kvBuf.length()) + "\r\n");
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
		strBuf.append(kvBuf);
		return strBuf.toString();
	}
	
	protected String setHttpHeadForGet(String path,KeyValuePair[] keyValuePairs,CookieInfo[] cookies,String accept,String acceptLanguage)
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
		strBuf.append("Accept-Encoding: gzip\r\n");
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
	
	protected String getFirstLine() throws IOException
	{
		String firstLine;
		while(true)
		{
			firstLine = this.bufReader.readLine();
			if(firstLine == null)
			{
				//
			}
			else if(this.weekEqual(firstLine, ""))
				continue;
			else
				return firstLine;
		}
	}
	
	protected void sendRequestAndAnalysisStatus(String req, HttpResponse HR, int port) throws WebCilentException
	{
		try
		{
			if(this.webSocket == null)
			{
				this.socketConnection(port);
				this.bufWriter.write(req);
				this.bufWriter.flush();
				String status = this.getFirstLine();
				this.analysisStatus(status, HR);
			}
			else
			{
				this.bufWriter.write(req);
				this.bufWriter.flush();
				String status = this.getFirstLine();
				if(status != null)
				{
					this.analysisStatus(status, HR);
				}
				else
				{
					this.webSocket.close();
					this.socketConnection(port);
					status = this.getFirstLine();
					if(status != null)
						this.analysisStatus(status, HR);
					else
						throw new WebCilentException("The http response from web has a unexpected end!\r\nFunction: protected void sendRequestAndAnalysisStatus(String req, HttpResponse HR, int port)"
								,ConstValue.CLASSNAME.SOCKETFORWEB|ConstValue.FUNCTIONNAME.SENDREQUESTANDANALYSISSTATUS|
								ConstValue.ERRORTYPE.UNEXPECTEND|ConstValue.OTHERINFORMATION.NONE);
				}
			}
		} catch(IOException e)
		{
			e.printStackTrace();
			throw new WebCilentException("Connection error!\r\nFunction: protected void sendRequestAndAnalysisStatus(String req, HttpResponse HR, int port)"
					,ConstValue.CLASSNAME.SOCKETFORWEB|ConstValue.FUNCTIONNAME.SENDREQUESTANDANALYSISSTATUS|
					ConstValue.ERRORTYPE.CONNECTIONERROR|ConstValue.OTHERINFORMATION.NONE);
		} catch (NumberFormatException e)
		{
			e.printStackTrace();
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
		this.webSocket.setSoTimeout(this.timeOut);
		this.bufWriter = new BufferedWriter(new OutputStreamWriter(this.webSocket.getOutputStream()));
		this.bufReader = new AsciiReader(new BufferedInputStream(this.webSocket.getInputStream()));
	}
	
	protected int analysisHead(HttpResponse HR) throws IOException, WebCilentException
	{
		int contentLength = -1;
		boolean inContinue = true;
		StringBuffer strBuffForOtherInfo= new StringBuffer(50);
		while(inContinue)
		{
			String line = this.bufReader.readLine();
			if(line == null)
			{
				throw new WebCilentException("The http response from web has a unexpected end!\r\nFunction: protected int analysisHead(HttpResponse HR) throws IOException, WebCilentException"
						,ConstValue.CLASSNAME.SOCKETFORWEB|ConstValue.FUNCTIONNAME.ANALYSISHEAD|
						ConstValue.ERRORTYPE.UNEXPECTEND|ConstValue.OTHERINFORMATION.NONE);
			}
			String[] arr = line.split(":",2);
			try
			{
				arr[0] = arr[0].replaceFirst("\\s*$", "");
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
				case "content-encoding":
					if(arr.length > 1)
						HR.setContentEncoding(arr[1].toLowerCase());
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
			catch(AnalysisError | ParseException | IOException p)
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

	public final int getBufLength()
	{
		return bufLength;
	}

	public final void setBufLength(int bufLength)
	{
		this.bufLength = bufLength;
	}
	
	public final int getTimeOut()
	{
		return timeOut;
	}

	public final void setTimeOut(int timeOut)
	{
		this.timeOut = timeOut;
	}

	public static String getDefaultAccept()
	{
		return defaultAccept;
	}

	public static void setDefaultAccept(String defaultAccept)
	{
		SocketForWeb.defaultAccept = defaultAccept;
	}

	public static String getDefaultAcceptLanguage()
	{
		return defaultAcceptLanguage;
	}

	public static void setDefaultAcceptLanguage(String defaultAcceptLanguage)
	{
		SocketForWeb.defaultAcceptLanguage = defaultAcceptLanguage;
	}
	
	public static int getDefaultHttpPort()
	{
		return defaultHttpPort;
	}
	
	public static void setDefaultHttpPort(int defaultHttpPort)
	{
		SocketForWeb.defaultHttpPort = defaultHttpPort;
	}
	
	public static int getDefaultHttpsPort()
	{
		return defaultHttpsPort;
	}
	
	public static void setDefaultHttpsPort(int defaultHttpsPort)
	{
		SocketForWeb.defaultHttpsPort = defaultHttpsPort;
	}
}
