#pragma once

struct PostName_Key
{
	std::string name,key;
};

struct CookieData_NK
{
	std::string name,data;
};

struct CookieData_Other
{
	std::string path,domain,expires,secure;
	_int64 maxAge;
	CookieData_Other()
		: maxAge(0)
	{
	}
};

struct CookieData
{
	CookieData_NK cNK;
	CookieData_Other cO;
};

class CookieInformation
{
public:
	std::vector<CookieData_NK> cookieData_NK;
	std::vector<CookieData_Other> cookieData_Other;
	void addCookieData(CookieData &c)
	{
		cookieData_NK.push_back(c.cNK);
		cookieData_Other.push_back(c.cO);
	}
	int size()
	{
		return cookieData_NK.size();
	}
};

struct ContentType
{
	std::string type,charset;
};

struct HttpResponse
{
	std::string body,version,date;
	ContentType contentType;
	CookieInformation cookies;
	_int64 length;
	int status;
};