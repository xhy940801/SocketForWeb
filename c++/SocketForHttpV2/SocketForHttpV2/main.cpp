#include "SocketForHttp.h"
#include <fstream>
int main()
{
	HttpResponse HR,HR2;
	SocketForHttp SFH("www.sina.com.cn");
	int err = SFH.getRequest(&HR);
}