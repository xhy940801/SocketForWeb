#include "SocketForHttp.h"


SocketForHttp::SocketForHttp(std::string url)
	: hostUrl(url),clientSocket(0),curPoint(0),receiveBufLength(0)
{
	WORD versionRequired;
	WSADATA wsaData;
	versionRequired=MAKEWORD(1,1);
	int err = WSAStartup(versionRequired,&wsaData);
#ifdef SFH_DEBUG
	switch (err)
	{
	case 0:
		break;
	case WSASYSNOTREADY:
		errorInformation += "Fuction WSAStartup Error : The underlying network subsystem is not ready for network communication.\n";
	case WSAVERNOTSUPPORTED:
		errorInformation += "Fuction WSAStartup Error : The version of Windows Sockets support requested is not provided by this particular Windows Sockets implementation.\n";
	case WSAEINPROGRESS:
		errorInformation += "Fuction WSAStartup Error : A blocking Windows Sockets 1.1 operation is in progress.\n";
	case WSAEPROCLIM:
		errorInformation += "Fuction WSAStartup Error : A limit on the number of tasks supported by the Windows Sockets implementation has been reached.\n";
	case WSAEFAULT:
		errorInformation += "Fuction WSAStartup Error : The lpWSAData parameter is not a valid pointer.\n";
	}
#endif
}


SocketForHttp::~SocketForHttp(void)
{
}


int SocketForHttp::getRequest(HttpResponse *pHR,const char* requestData,const CookieData_NK *pC,int cLength,const char* accept,const char* acceptLanguage)
{
	std::string head;
	setRequestForGet(head,requestData,pC,cLength,accept,acceptLanguage);
	int err = sendHttpRequest(head);
	if(err != 0)
	{
		this->receiveBufLength = 0;
		return err;
	}
	bool keepAlive = false;
	err = recvAndAnalysis(pHR,&keepAlive);
	if(keepAlive == false)
	{
		closesocket(clientSocket);
		clientSocket = 0;
	}
	if(err != 0)
		return err;
	else
		return 0;
}


void SocketForHttp::setRequestForGet(std::string &req,const char* requestData,const CookieData_NK *pC,int cLength,const char* accept,const char* acceptLanguage)
{
	req = "GET /";
	req += requestData;
	req +=" HTTP/1.1\r\nAccept:";
	req += accept;
	req += "\r\nAccept-Language: ";
	req += acceptLanguage;
	req += "\r\n"
		"Connection: Keep-Alive\r\n"
		"Host: ";
	req += this->hostUrl;
	req += "\r\n";
	if(cLength > 0)
	{
		req += "Cookie:";
		for(int i=0;i<cLength;++i)
		{
			req += ' ';
			req += pC[i].name;
			req += '=';
			req += pC[i].data;
			req += ';';
		}
		req[req.size()-1] = '\r';
		req += '\n';
	}
	req += "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\r\n\r\n";
}


int SocketForHttp::sendHttpRequest(std::string& req)
{
	for(int i=0;i<retryTimes;++i)
	{
		if(this->clientSocket == 0)
		{
			setSocket();
		}
		send(clientSocket,req.data(),req.size(),0);
		receiveBufLength = recv(clientSocket,(LPSTR)receiveBuf,sizeof(receiveBuf),0);
		if(receiveBufLength <= 0)
		{
			closesocket(clientSocket);
			setSocket();
		}
		else
		{
			this->curPoint = 0;
			return 0;
		}
	}
	return SFH_SENDREEQUEST_SENDFAIL_OVERTIMES;
}


int SocketForHttp::setSocket(void)
{
	lphostent=gethostbyname(this->hostUrl.data());
	if(lphostent==NULL)
	{
		int err = WSAGetLastError();
#ifdef SFH_DEBUG
		switch (err)
		{
		case WSANOTINITIALISED:
			errorInformation += "Function gethostbyname Error : A successful WSAStartup call must occur before using this function.\n";
			break;
		case WSAENETDOWN:
			errorInformation += "Function gethostbyname Error : The network subsystem has failed.\n";
			break;
		case WSAHOST_NOT_FOUND:
			errorInformation += "Function gethostbyname Error : Authoritative answer host not found.\n";
			break;
		case WSATRY_AGAIN:
			errorInformation += "Function gethostbyname Error : Nonauthoritative host not found, or server failure.\n";
			break;
		case WSANO_RECOVERY:
			errorInformation += "Function gethostbyname Error : A nonrecoverable error occurred.\n";
			break;
		case WSANO_DATA:
			errorInformation += "Function gethostbyname Error : The requested name is valid, but no data of the requested type was found."
				"This error is also returned if the name parameter contains a string representation of an IPv6 address or an illegal IPv4 address."
				"This error should not be interpreted to mean that the name parameter contains a name string that has been validated for a particular protocol"
				"(an IP hostname, for example). Since Winsock supports multiple name service providers, a name may potentially be valid for one provider and not accepted by another provider.\n";
			break;
		case WSAEINPROGRESS:
			errorInformation += "Function gethostbyname Error : A blocking Windows Sockets 1.1 call is in progress, or the service provider is still processing a callback function.\n";
			break;
		case WSAEFAULT:
			errorInformation += "Function gethostbyname Error : The name parameter is not a valid part of the user address space.\n";
			break;
		case WSAEINTR:
			errorInformation += "Function gethostbyname Error : A blocking Windows Socket 1.1 call was canceled through WSACancelBlockingCall.\n";
			break;
		}
#endif
		return err;
	}

	clientSocket = socket(AF_INET,SOCK_STREAM,0);

	SOCKADDR_IN clientsock_in;
	clientsock_in.sin_addr=*((LPIN_ADDR)*(lphostent->h_addr_list));
	clientsock_in.sin_family=AF_INET;
	clientsock_in.sin_port=htons(80);
	int nRet = connect(clientSocket,(SOCKADDR*)&clientsock_in,sizeof(SOCKADDR));
	if(nRet == SOCKET_ERROR)
	{
		int err =  WSAGetLastError();
#ifdef SFH_DEBUG
		errorInformation += "Function connect Error : Error Code";
		errorInformation += IntToString(err);
		errorInformation += "\n";
#endif
		closesocket(clientSocket);
		return err;
	}
	return 0;
}


int SocketForHttp::recvAndAnalysis(HttpResponse *pHR,bool *keepAlive)
{
	char c;
	int err;
	if((err = this->getStatus(pHR)) != 0)
		return err;
	while(1)
	{
		std::string sign;
		while((c = recvNext(&err)) != ':' && c != '\n')
		{
			if(err != 0)
				return err;
			else if(c == '\r')
				continue;
			else if(c != ' ')
				sign += c;
		}

		this->transferToLowerCase(sign);

		if(sign == "content-type")
		{
			if((err = this->getContentType(pHR)) != 0)
				return err;
		}
		else if(sign == "transfer-encoding")
		{
			if((err = this->getTransferEncoding(pHR)) != 0)
				return err;
		}
		else if(sign == "content-length")
		{
			if((err = this->getContentLength(pHR)) != 0)
				return err;
		}
		else if(sign == "date")
		{
			if((err = this->getDate(pHR)) != 0)
				return err;
		}
		else if(sign == "set-cookie")
		{
			if((err = this->getCookie(pHR)) != 0)
				return err;
		}
		else if(sign == "connection")
		{
			if((err = this->getConnectionMethod(keepAlive)) != 0)
				return err;
		}
		else if(sign == "")
		{
			if(pHR->length == -1)
			{
				if((err = this->getBodyByChunked(pHR)) != 0)
					return err;
				else
					return 0;
			}
			else if(pHR->length > 0)
			{
				if((err = this->getBodyByLength(pHR)) != 0)
					return err;
				else
					return 0;
			}
			else
			{
#ifdef SFH_DEBUG
				errorInformation += "Analysis the http head error : Could not make sure the methon of receiving(Chunked or Content-Length).\n";
#endif
				return SFH_HTTPHEAD_ANALYSISFAIL;
			}
		}
		else
		{
			if((err = this->turnToNextLine()) != 0)
				return err;
		}
	}
	return 0;
}


char SocketForHttp::recvNext(int* err)
{
	if(curPoint<receiveBufLength)
	{
		*err = 0;
		return receiveBuf[curPoint++];
	}
	else
	{
		receiveBufLength = recv(clientSocket,(LPSTR)receiveBuf,sizeof(receiveBuf),0);
		if(receiveBufLength > 0)
		{
			*err = 0;
			curPoint = 0;
			return receiveBuf[curPoint++];
		}
		else if(receiveBufLength == 0)
		{
			*err = SFH_SOCKET_LOSINGCONNECT;
			return 0;
		}
		else
		{
			*err = WSAGetLastError();
			return 0;
		}
	}
}


int SocketForHttp::getStatus(HttpResponse *pHR)
{
	char c;
	int err;
	while((c = recvNext(&err)) == '\r' || c == '\n' || c == ' ' || c == '\t');
	if(err != 0)
		return err;
	else
		pHR->version += c;
	while((c = recvNext(&err)) != ' ')
	{
		if(err == 0)
		{
			pHR->version += c;
		}
		else
			return err;
	}
	std::stringstream ss;
	while((c = recvNext(&err)) != ' ')
	{
		if(err == 0)
		{
			ss<<c;
		}
		else
			return err;
	}
	ss>>pHR->status;
	while((c = recvNext(&err)) != '\n')
	{
		if(err != 0)
			return err;
	}
	return 0;
}


int SocketForHttp::getContentType(HttpResponse* pHR)
{
	std::string str;
	char c;
	int err;
	c = recvNext(&err);
	if(err != 0)
		return err;
	else if(c != ' ')
		pHR->contentType.type += c;
	if((c = recvNext(&err)) != ' ')
	{
		if(err == 0)
		{
			if(c == '\n')
				return 0;
			pHR->contentType.type += c;
		}
		else
			return err;
	}
	while((c = recvNext(&err)) != ';')
	{
		if(err == 0)
		{
			if(c == '\n')
				return 0;
			pHR->contentType.type += c;
		}
		else
			return err;
	}
	if((c = recvNext(&err)) != ' ')
	{
		if(err == 0)
		{
			if(c == '\n')
				return 0;
			str += c;
		}
		else
			return err;
	}
	while((c = recvNext(&err)) != '=')
	{
		if(err == 0)
		{
			if(c == '\n')
				return 0;
			str += c;
		}
		else
			return err;
	}
	if(str == "charset")
	{
		while((c = recvNext(&err)) != '\n')
		{
			if(c == '\r')
				continue;
			if(err == 0)
				pHR->contentType.charset += c;
			else
				return err;
		}
	}
	return 0;
}


int SocketForHttp::getTransferEncoding(HttpResponse* pHR)
{
	char c;
	int err;
	while((c = recvNext(&err)) != '\n')
	{
		if(err != 0)
			return err;
	}
	pHR->length = -1;
	return 0;
}


int SocketForHttp::getDate(HttpResponse* pHR)
{
	int err;
	char c;
	c = recvNext(&err);
	if(err != 0)
		return err;
	if(c != ' ')
		pHR->date += c;
	while((c = recvNext(&err)) != '\n')
	{
		if(c == '\r')
			continue;
		if(err == 0)
			pHR->date += c;
		else
			return err;
	}
	return 0;
}


int SocketForHttp::getBodyByChunked(HttpResponse* pHR)
{
	while(1)
	{
		std::string strn;
		int number;
		int err;
		char c;
		while((c = recvNext(&err)) == '\r' || c == '\n');
		if(err != 0)
			return err;
		else
			strn += c;
		while((c = recvNext(&err)) != '\n')
		{
			if(err != 0)
				return err;
			else
				strn += c;
		}
		number = this->str16ToInt(strn.c_str());
		if(number == 0)
			break;
		else if(number < 0)
		{
#ifdef SFH_DEBUG
			this->errorInformation += "Anaysis Fail : In getBodyByChunked function!\n";
#endif
			return SFH_HTTPCHUNKED_ANALYSISFAIL;
		}
		for(int i=0;i<number;++i)
		{
			c = recvNext(&err);
			if(err != 0)
				return err;
			else
				pHR->body += c;
		}
		pHR->length += number;
	}
	return 0;
}


int SocketForHttp::str16ToInt(const char * str16)
{
	std::stringstream ss;
	int n;
	ss<<str16;
	ss>>std::hex>>n;
	return n;
}


int SocketForHttp::getBodyByLength(HttpResponse* pHR)
{
	pHR->body.reserve(pHR->length);
	for(int i=0;i<pHR->length;++i)
	{
		int err;
		char c = recvNext(&err);
		if(err != 0)
			return err;
		else
			pHR->body += c;
	}
	pHR->body += '\0';
	return 0;
}


int SocketForHttp::getContentLength(HttpResponse* pHR)
{
	int err;
	char c;
	std::string strLength;
	c = recvNext(&err);
	while((c = recvNext(&err)) != '\n')
	{
		if(err == 0)
			strLength += c;
		else
			return err;
	}
	pHR->length = this->str10ToInt(strLength.c_str());
	return 0;
}


int SocketForHttp::str10ToInt(const char * str10)
{
	std::stringstream ss;
	int n;
	ss<<str10;
	ss>>n;
	return n;
}


int SocketForHttp::turnToNextLine()
{
	char c;
	int err;
	while(1)
	{
		c = this->recvNext(&err);
		if(err != 0)
			return err;
		else
		{
			if(c == '\n')
				return 0;
		}
	}
	return 0;
}


int SocketForHttp::getCookie(HttpResponse *pHR)
{
	int err;
	char c;
	while((c = recvNext(&err)) == ' ');
	CookieData cookie;
	cookie.cNK.name += c;
	while((c = recvNext(&err)) != '=')
	{
		if(err != 0)
			return err;
		else if(c == '\n')
		{
#ifdef SFH_DEBUG
			errorInformation += "Warning : Analysis Set-Cookie in Http head fail!\n";
#endif
			pHR->cookies.addCookieData(cookie);
			return 0;
		}
		cookie.cNK.name += c;
	}

	while((c = recvNext(&err)) != ';')
	{
		if(err != 0)
			return err;
		else if(c == '\r')
			continue;
		else if(c == '\n')
		{
			pHR->cookies.addCookieData(cookie);
			return 0;
		}
		cookie.cNK.data += c;
	}

	while(1)
	{
		std::string sign;
		while(1)
		{
			c = recvNext(&err);
			if(err == 0)
			{
				if(c == ' ')
					continue;
				else if(c == '\n')
				{
					pHR->cookies.addCookieData(cookie);
					return 0;
				}
				else
				{
					sign += c;
					break;
				}
			}
			else
				return err;
		}

		while((c = recvNext(&err)) != '=')
		{
			if(err != 0)
				return err;
			else if(c == '\r')
				continue;
			else if(c == '\n')
			{
				pHR->cookies.addCookieData(cookie);
				return 0;
			}
			sign += c;
		}

		this->transferToLowerCase(sign);

		if(sign == "path")
		{
			if(analysisSomeInformationOfCookie(cookie.cO.path,&err) == false)
			{
				pHR->cookies.addCookieData(cookie);
				return err;
			}
		}
		else if(sign == "domain")
		{
			if(analysisSomeInformationOfCookie(cookie.cO.domain,&err) == false)
			{
				pHR->cookies.addCookieData(cookie);
				return err;
			}
		}
		else if(sign == "expires")
		{
			if(analysisSomeInformationOfCookie(cookie.cO.expires,&err) == false)
			{
				pHR->cookies.addCookieData(cookie);
				return err;
			}
		}
		else if(sign == "secure")
		{
			if(analysisSomeInformationOfCookie(cookie.cO.secure,&err) == false)
			{
				pHR->cookies.addCookieData(cookie);
				return err;
			}
		}
		else if(sign == "max-age")
		{
			std::string maxage;
			if(analysisSomeInformationOfCookie(maxage,&err) == false)
			{
				cookie.cO.maxAge = this->str10ToInt(maxage.c_str());
				pHR->cookies.addCookieData(cookie);
				return err;
			}
			cookie.cO.maxAge = this->str10ToInt(maxage.c_str());
		}
		else
		{
			if(turnNextCookieInformation(&err) == false)
			{
				pHR->cookies.addCookieData(cookie);
				return err;
			}
		}
	}
}


bool SocketForHttp::analysisSomeInformationOfCookie(std::string &I,int *err)
{
	char c;
	while((c = recvNext(err)) != ';')
	{
		if(*err != 0)
			return false;
		else if(c == '\r')
			continue;
		else if(c == '\n')
		{
			return false;
		}
		I += c;
	}
	return true;
}


bool SocketForHttp::turnNextCookieInformation(int *err)
{
	char c;
	while((c = recvNext(err)) != ';')
	{
		if(*err != 0)
			return false;
		else if(c == '\n')
		{
			return false;
		}
	}
	return true;
}


void SocketForHttp::transferToLowerCase(std::string& str)
{
	for(int i=0;i<str.size();++i)
	{
		if(str[i] <= 'Z' && str[i] >= 'A')
			str[i] += 'a' - 'A';
	}
}


int SocketForHttp::getConnectionMethod(bool * keepAlive)
{
	char c;
	int err;
	std::string sign;
	while((c = recvNext(&err)) == ' ');
	if(err != 0)
		return err;
	else
		sign += c;
	while(1)
	{
		c = this->recvNext(&err);
		if(err != 0)
			return err;
		else
		{
			if(c == '\n')
			{
				this->transferToLowerCase(sign);
				if(sign == "keep-alive")
					*keepAlive = true;
				else
					*keepAlive = false;
				return 0;
			}
			else if(c == '\r')
				continue;
			else
				sign += c;
		}
	}
	return 0;
}


int SocketForHttp::postRequest(HttpResponse *pHR,PostName_Key *pPNK,int PNKLenth,const char* url,const CookieData_NK *pC,int cLength,const char* accept,const char* acceptLanguage)
{
	std::string head;
	setRequestForPost(head,pPNK,PNKLenth,url,pC,cLength,accept,acceptLanguage);
	int err = sendHttpRequest(head);
	if(err != 0)
	{
		this->receiveBufLength = 0;
		return err;
	}
	bool keepAlive = false;
	err = recvAndAnalysis(pHR,&keepAlive);
	if(keepAlive == false)
	{
		closesocket(clientSocket);
		clientSocket = 0;
	}
	if(err != 0)
		return err;
	else
		return 0;
}


void SocketForHttp::setRequestForPost(std::string &req,PostName_Key *pPNK,int PNKLenth,const char* url,const CookieData_NK *pC,int cLength,const char* accept,const char* acceptLanguage)
{
	std::string str;
	if(PNKLenth >= 1)
	{
		str += this->urlEncording((pPNK[0].name).c_str());
		str += '=';
		str += this->urlEncording((pPNK[0].key).c_str());
		for(int i=1;i<PNKLenth;++i)
		{
			str += '&';
			str += this->urlEncording((pPNK[i].name).c_str());
			str += '=';
			str += this->urlEncording((pPNK[i].key).c_str());
		}
	}

	req = "POST /";
	req += url;
	req +=" HTTP/1.1\r\nAccept:";
	req += accept;
	req += "\r\nAccept-Language: ";
	req += acceptLanguage;
	req += "\r\n"
		"Connection: Keep-Alive\r\n"
		"Content-Type: application/x-www-form-urlencoded\r\n"
		"Host: ";
	req += hostUrl;
	req += "\r\n";
	req += "Content-Length: ";
	req += this->intToStr10(str.size());
	req += "\r\n";
	if(cLength > 0)
	{
		req += "Cookie:";
		for(int i=0;i<cLength;++i)
		{
			req += ' ';
			req += pC[i].name;
			req += '=';
			req += pC[i].data;
			req += ';';
		}
		req[req.size()-1] = '\r';
		req += '\n';
	}
	req +=	"User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\r\n\r\n";
	req += str;
}


std::string SocketForHttp::urlEncording(const char* str)
{
	std::stringstream ss;
	for(int i=0;str[i];++i)
	{
		if((str[i]<= 'z' && str[i]>= 'A') ||(str[i]<= '9' && str[i]>= '0'))
			ss<<str[i];
		else if(str[i]== ' ')
			ss<<'+';
		else
			ss<<'%'<<std::hex<<(int)(str[i] & 255);
	}
	std::string rStr;
	ss>>rStr;
	return rStr;
}


std::string SocketForHttp::intToStr10(int a)
{
	std::stringstream ss;
	ss<<a;
	std::string Ts;
	ss>>Ts;
	return Ts;
}