package com.xiao.Socket.WebCilent;

public class CookieInfo
{
	private String key,value;
	private java.util.Date expires;
	private String path;
	private String domain;
	private boolean secure;
	
	//The getter!
	
	public final String getKey()
	{
		return this.key;
	}
	
	public final String getValue()
	{
		return this.value;
	}
	
	public final java.util.Date getExpires()
	{
		return this.expires;
	}
	
	public final String getPath()
	{
		return this.path;
	}
	
	public final String getDomain()
	{
		return this.domain;
	}
	
	public final boolean getSecure()
	{
		return this.secure;
	}
	
	//The setter!
	
	public final void setKey(String k)
	{
		this.key = k;
	}
	
	public final void setValue(String v)
	{
		this.value = v;
	}
	
	public final void setDate(java.util.Date e)
	{
		this.expires = e;
	}
	
	public final void setPath(String p)
	{
		this.path = p;
	}
	
	public final void setDomain(String d)
	{
		this.domain= d;
	}
	
	public final void setSecure(boolean s)
	{
		this.secure = s;
	}
	
	public void set(String key,String value,java.util.Date expires,String path,String domain,boolean secure)
	{
		this.key = key;
		this.value = value;
		this.expires = expires;
		this.path = path;
		this.domain = domain;
		this.secure = secure;
	}
	
	public void set(String setCookieStr)
	{
		
	}
	//The construct
	
	public CookieInfo()
	{
		this.expires = null;
	}
	
	public CookieInfo(String setCookieStr)
	{
		this.set(setCookieStr);
	}
	
	public CookieInfo(String key,String value,java.util.Date expires,String path,String domain,boolean secure)
	{
		this.set(key, value, expires, path, domain, secure);
	}
}
