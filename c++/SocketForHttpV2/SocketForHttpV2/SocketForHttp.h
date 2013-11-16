#pragma once
/*
	This class is using a choke version of socket.For more friendly user experience,I suggest you use it as a separate thread.
*/
#include <WINSOCK2.H>
#include <string>
#include <stdio.h>
#include <sstream>
#include <vector>

#include "SomeStructOfSocketForHttp.h"

#pragma comment(lib,"ws2_32.lib")

#define SFH_SOCKET_LOSINGCONNECT -100
#define SFH_HTTPHEAD_ANALYSISFAIL -101
#define SFH_HTTPCHUNKED_ANALYSISFAIL -102
#define SFH_SENDREEQUEST_SENDFAIL_OVERTIMES -103

class SocketForHttp
{
#ifdef SFH_DEBUG
	std::string errorInformation;
#endif
	static const int bufLength = 1024;
	static const int retryTimes = 5;
	char receiveBuf[bufLength];
	int receiveBufLength,curPoint;
	LPHOSTENT lphostent;
	std::string hostUrl;
	SOCKET clientSocket;
public:
	SocketForHttp(std::string url);
	~SocketForHttp(void);
	int getRequest(HttpResponse *pHR,const char* requestData = "",const CookieData_NK *pC = 0,int cLength = 0,const char* accept = "*/*",const char* acceptLanguage = "zh-cn");
	int postRequest(HttpResponse *pHR,PostName_Key *pPNK,int PNKLenth,const char* url = "",const CookieData_NK *pC = 0,int cLength = 0,const char* accept = "*/*",const char* acceptLanguage = "zh-cn");
private:
	void setRequestForGet(std::string &req,const char* requestData,const CookieData_NK *pC,int cLength,const char* accept,const char* acceptLanguage);
	void setRequestForPost(std::string &req,PostName_Key *pPNK,int PNKLenth,const char* url,const CookieData_NK *pC,int cLength,const char* accept,const char* acceptLanguage);
	int sendHttpRequest(std::string& req);
	int setSocket(void);
	int recvAndAnalysis(HttpResponse *pHR,bool *keepAlive);
	char recvNext(int* err);

	int getStatus(HttpResponse *pHR);
	int getContentType(HttpResponse* pHR);
	int getTransferEncoding(HttpResponse* pHR);
	int getContentLength(HttpResponse* pHR);
	int getDate(HttpResponse* pHR);
	int getBodyByLength(HttpResponse* pHR);
	int getBodyByChunked(HttpResponse* pHR);
	int getCookie(HttpResponse* pHR);
	int getConnectionMethod(bool * keepAlive);

	int str16ToInt(const char * str16);
	int str10ToInt(const char * str16);
	std::string intToStr10(int a);
	std::string urlEncording(const char* str);

	int turnToNextLine();

	bool analysisSomeInformationOfCookie(std::string &I,int *err);
	bool turnNextCookieInformation(int *err);
	void transferToLowerCase(std::string& str);
};