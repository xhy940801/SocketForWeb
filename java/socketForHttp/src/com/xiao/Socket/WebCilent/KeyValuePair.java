package com.xiao.Socket.WebCilent;

public class KeyValuePair
{
	public String key,value;
	
	KeyValuePair(String key)
	{
		this.key = key;
		this.value = "";
	}
	
	KeyValuePair(String key,String value)
	{
		this.key = key;
		this.value = value;
	}
}
