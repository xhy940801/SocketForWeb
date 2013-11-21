import java.io.IOException;
import java.util.Date;

import com.xiao.Socket.WebCilent.AnalysisError;
import com.xiao.Socket.WebCilent.CookieInfo;
import com.xiao.Socket.WebCilent.HttpResponse;
import com.xiao.Socket.WebCilent.KeyValuePair;
import com.xiao.Socket.WebCilent.SocketForWeb;
import com.xiao.Socket.WebCilent.WebCilentException;


public class test
{

	public static void main(String[] args) throws IOException, AnalysisError, WebCilentException
	{
		Date d1 = new Date();
		SocketForWeb s = new SocketForWeb("www.sina.com.cn");
		HttpResponse HR = s.getForHttp("/", (KeyValuePair[])null, (CookieInfo[])null, "*/*", "zh-cn",80);
		Date d2 = new Date();
		System.out.println();
		System.out.println(d2.getTime() - d1.getTime());
		System.out.println();
		System.out.println(HR.turnByteToString());
	}

}

