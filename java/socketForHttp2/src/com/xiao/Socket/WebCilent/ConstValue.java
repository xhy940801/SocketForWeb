package com.xiao.Socket.WebCilent;

public class ConstValue
{
	//Class name
	class CLASSNAME
	{
		public static final int COOKIEINFO = 0X01000000;
		public static final int SOCKETFORWEB = 0X02000000;
	}
	
	//function name
	class FUNCTIONNAME
	{
		public static final int SET_SETCOOKIESTR = 0x00010000;
		public static final int STRINGTODATE = 0x00020000;
		public static final int GZIPDECODE = 0x00030000;
		public static final int SENDREQUESTANDANALYSISSTATUS = 0x00040000;
		public static final int ANALYSISHEAD = 0x00050000;
		public static final int SENDANDGETRESPONSE = 0x00060000;
		public static final int GETBODYBYTESBYCHUNK = 0x00070000;
		public static final int GETBODYBYTESBYCONTENTLENGTH = 0x00080000;
	}
	
	//ErrorType
	class ERRORTYPE
	{
		public static final int FORMERROR = 0X00000100;
		public static final int IOERROR = 0X00000200;
		public static final int UNEXPECTEND = 0X00000300;
		public static final int CONNECTIONERROR = 0X00000400;
		public static final int UNKNOWNCONTENTENCORDING = 0X00000400;
	}
	
	//Other information
	class OTHERINFORMATION
	{
		public static final int NONE = 0x00000000;
	}
}