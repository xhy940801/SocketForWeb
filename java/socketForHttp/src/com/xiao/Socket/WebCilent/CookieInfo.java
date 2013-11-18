package com.xiao.Socket.WebCilent;

import java.text.ParseException;
import java.util.Locale;

public class CookieInfo
{
	private String key = null,value = null;
	private java.util.Date expires = null;
	private String path = null;
	private String domain = null;
	private boolean secure = false,httpOnly = false;
	
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
	
	public final boolean getHttpOnly()
	{
		return this.httpOnly;
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
	
	public final void setHttpOnly(boolean h)
	{
		this.httpOnly = h;
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
	
	public void set(String setCookieStr) throws AnalysisError
	{
		Condition[] conditions = this.turnToArrayString(setCookieStr);
		if(conditions.length < 1 || conditions[0].kv.length < 2)
			throw new AnalysisError("The input string ("+setCookieStr+") is not a standard set-cookie form (in http response)",
					ConstValue.COOKIEINFO|ConstValue.SET_SETCOOKIESTR|ConstValue.FORMERROR|ConstValue.NONE);
		this.value = conditions[0].kv[1];
		for(int i=0;i<conditions.length;++i)
		{
			switch(conditions[i].kv[0].toLowerCase())
			{
			case "path":
				this.path = conditions[i].kv[1];
				break;
			case "domain":
				this.domain = conditions[i].kv[1];
				break;
			case "expires":
				if(this.expires != null)
				{
					try
					{
						this.expires = this.stringToDate(conditions[i].kv[1]);
					} catch (ParseException e)
					{
						e.printStackTrace();
						this.expires = null;
					}
				}
				break;
			case "secure":
				this.secure = true;
				break;
			case "httponly":
				this.httpOnly = true;
				break;
			case "max-age":
				this.expires = new java.util.Date();
				this.expires.setTime(this.expires.getTime()+Integer.parseInt(conditions[i].kv[1])*1000);
				break;
			}
		}
	}
	
	//Other public function
	
	//Check is or not valid
	public boolean isValid(String url,boolean checkDate,boolean secure)
	{
		if(checkDate && this.expires.before(new java.util.Date()))
			return false;
		if(this.secure && !secure)
			return false;
		String[] pathAndDomain = url.split("/", 2);
		if(this.domain != null)
		{
			if(pathAndDomain[0].equals('.'))
			{
				if(!pathAndDomain[0].matches("^.*"+this.domain+"$"))
					return false;
			}
			else
			{
				if(!pathAndDomain[0].matches("^"+this.domain+"$"))
					return false;
			}
		}
		if(this.path != null && pathAndDomain.length > 1 && !pathAndDomain[1].matches("^"+this.path.replaceFirst("^/", "")+"(|/.*)$"))
			return false;
		return true;
	}
	
	//The construct
	
	public CookieInfo()
	{
		
	}
	
	public CookieInfo(String setCookieStr) throws AnalysisError
	{
		this.set(setCookieStr);
	}
	
	public CookieInfo(String key,String value,java.util.Date expires,String path,String domain,boolean secure)
	{
		this.set(key, value, expires, path, domain, secure);
	}
	
	//private function
	
	//Depart the string
	private Condition[] turnToArrayString(String str)
	{
		String[] arrStr = str.split(";");
		Condition[] conditions = new Condition[arrStr.length];
		for(int i=0;i<arrStr.length;++i)
		{
			conditions[i] = new Condition();
			arrStr[i] = arrStr[i].replaceAll("^ +| +$", "");
			conditions[i].kv = arrStr[i].split("=",2);
		}
		return conditions;
	}
	
	//Turn the string to date
	private java.util.Date stringToDate(String str) throws ParseException
	{
		str = str.replaceAll(" +", " ");
		str = str.replaceAll(" ,|, ", ",");
		java.text.SimpleDateFormat simpleDateFormat;
		if(str.matches("^ *[a-zA-Z]+,\\d{2}-[a-zA-Z]+-\\d{2,4} \\d{2}:\\d{2}:\\d{2} [a-zA-Z]+ *$"))
			simpleDateFormat = new java.text.SimpleDateFormat("E,d-MMM-y H:m:s z",Locale.US);
		else if(str.matches("^ *[a-zA-Z]+,\\d{2} [a-zA-Z]+ \\d{2,4} \\d{2}:\\d{2}:\\d{2} [a-zA-Z]+ *$"))
			simpleDateFormat = new java.text.SimpleDateFormat("E,d MMM y H:m:s z",Locale.US);
		else if(str.matches("^ *[a-zA-Z]+,\\d{2}-\\d{2}+-\\d{2,4} \\d{2}:\\d{2}:\\d{2} [a-zA-Z]+ *$"))
			simpleDateFormat = new java.text.SimpleDateFormat("E,d-M-y H:m:s z",Locale.US);
		else
			simpleDateFormat = new java.text.SimpleDateFormat("E,d MMM y H:m:s Z",Locale.US);
		return simpleDateFormat.parse(str);
	}
}

class Condition
{
	public String[] kv = null;
}
