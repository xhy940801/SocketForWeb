package com.xiao.Socket.WebCilent;

public class HttpResponse
{
	private int status = -1;
	private String version = null;
	private java.util.Date date = null, expires = null;
	private String dataType = null,charset = null;
	private CookieList cookieList;
	private String otherInfomation = null;
	private String body = null;
	
	public HttpResponse()
	{
		this.cookieList = new CookieList();
	}
	
	public final int getStatus()
	{
		return status;
	}
	
	public final void setStatus(int status)
	{
		this.status = status;
	}
	
	public final String getVersion()
	{
		return version;
	}
	
	public final void setVersion(String version)
	{
		this.version = version;
	}
	
	public final java.util.Date getDate()
	{
		return date;
	}
	
	public final void setDate(java.util.Date date)
	{
		this.date = date;
	}
	
	public final java.util.Date getExpires()
	{
		return expires;
	}
	
	public final void setExpires(java.util.Date expires)
	{
		this.expires = expires;
	}
	
	public final String getDataType()
	{
		return dataType;
	}
	
	public final void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public final String getCharset()
	{
		return charset;
	}
	
	public final void setCharset(String charset)
	{
		this.charset = charset;
	}
	
	public final CookieList getCookieList()
	{
		return cookieList;
	}
	
	public final String getOtherInfomation()
	{
		return otherInfomation;
	}
	
	public final void setOtherInfomation(String otherInfomation)
	{
		this.otherInfomation = otherInfomation;
	}
	
	public final String getBody()
	{
		return body;
	}
	
	public final void setBody(String body)
	{
		this.body = body;
	}
}
