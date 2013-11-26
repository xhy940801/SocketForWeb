import java.io.IOException;
import java.util.Date;

import com.xiao.Socket.WebCilent.AnalysisError;
import com.xiao.Socket.WebCilent.CookieInfo;
import com.xiao.Socket.WebCilent.HttpCilent;
import com.xiao.Socket.WebCilent.HttpResponse;
import com.xiao.Socket.WebCilent.HttpsCilent;
import com.xiao.Socket.WebCilent.KeyValuePair;
import com.xiao.Socket.WebCilent.WebCilentException;


public class test
{

	public static void main(String[] args) throws IOException, AnalysisError, WebCilentException
	{
		Date d1 = new Date();
		HttpCilent s = new HttpsCilent("xiaosgapp.appspot.com","www.google.com.hk");
		KeyValuePair[] kvp = new KeyValuePair[2];
/*		for(int i=0;i<kvp.length;++i)
			kvp[i] = new KeyValuePair();
		kvp[0].set("hehe", "¹þ¹þ");
		kvp[1].set("haha", "0");*/
		HttpResponse HR = s.doGet("/show");
		Date d2 = new Date();
		System.out.println();
		System.out.println(d2.getTime() - d1.getTime());
		System.out.println();
		String hehe;
		System.out.println(hehe = HR.turnByteToString());
		int a = hehe.charAt(0);
		a = 0;
	}
}