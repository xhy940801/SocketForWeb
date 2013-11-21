import java.io.IOException;
import java.net.UnknownHostException;

import com.xiao.Socket.WebCilent.CookieInfo;
import com.xiao.Socket.WebCilent.HttpResponse;
import com.xiao.Socket.WebCilent.KeyValuePair;
import com.xiao.Socket.WebCilent.SocketForWeb;


public class test
{

	public static void main(String[] args) throws IOException
	{
		SocketForWeb s = new SocketForWeb("www.baidu.com");
		HttpResponse HR = s.GetForHttp("/", (KeyValuePair[])null, (CookieInfo[])null, "*/*", "zh-cn",80);
		System.out.println(HR.getBody());
		String str = new String(HR.getBody().getBytes("US-ASCII"),"UTF-8");
		System.out.println(str);
	}

}
