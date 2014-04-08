import java.io.IOException;
import java.util.Date;

import com.xiao.Socket.WebClient.AnalysisError;
import com.xiao.Socket.WebClient.HttpClient;
import com.xiao.Socket.WebClient.HttpResponse;
import com.xiao.Socket.WebClient.HttpsClient;
import com.xiao.Socket.WebClient.WebCilentException;


public class test
{

	@SuppressWarnings({ "rawtypes", "unused" })
	public static void main(String[] args) throws IOException, AnalysisError, WebCilentException
	{
		Date d1 = new Date();
		Class a = String.class;
		Object hehe2 = new String("hehehe");
		System.out.println(a.cast(hehe2));
		HttpClient s = new HttpClient("127.0.0.1");
/*		KeyValuePair[] kvp = new KeyValuePair[2];
		for(int i=0;i<kvp.length;++i)
			kvp[i] = new KeyValuePair();
		kvp[0].set("hehe", "¹þ¹þ");
		kvp[1].set("haha", "0");*/
		HttpResponse HR = s.doGet("/furnishing_data/obj_3d_mesh/aplanar_1.obj");
		Date d2 = new Date();
		System.out.println();
		System.out.println(d2.getTime() - d1.getTime());
		System.out.println();
		String hehe;
		System.out.println(hehe = HR.turnByteToString());
	}
}