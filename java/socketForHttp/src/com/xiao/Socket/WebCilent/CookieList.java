package com.xiao.Socket.WebCilent;

import java.util.ArrayList;

public class CookieList
{
	private ArrayList<CookieInfo> arrayList;
	public CookieList()
	{
		this.arrayList = new ArrayList<CookieInfo>(3);
	}
	
	public void addCookie(CookieInfo cookie)
	{
		this.arrayList.add(cookie);
	}
	
	public CookieInfo[] getValidCookieArray(String url,boolean checkDate,boolean secure)
	{
		ArrayList<CookieInfo> validCookies = new ArrayList<CookieInfo>(this.arrayList.size());
		for(int i=0;i<this.arrayList.size();++i)
		{
			if(this.arrayList.get(i).isValid(url, checkDate, secure))
				validCookies.add(this.arrayList.get(i));
		}
		return (CookieInfo[])validCookies.toArray();
	}
}
