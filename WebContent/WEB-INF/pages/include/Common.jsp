<%@ page import="java.util.*"  %>
<%@ page import="java.util.regex.*"  %>
<%!
Map<String, String> _skuYear =  new HashMap<String, String>();
%>
<%!
public String imgUrl = "http://static.lpmas.com/images/trendy/";

public Boolean _isTestSite(String url) {
	if (url.indexOf("test.") >= 0) {
		return true;
	}
	else {
		return false;
	}
}

public String _getBrandDomain(String currUrl) {
	String topDomain = _getTopDomain(currUrl);
	if (topDomain!=null && topDomain.trim().length()>0) return "."+topDomain;
	return "";
}

public String _getTopDomain(String uri) {
	String topDomain = null;
	if (uri==null || uri.trim().length()<=0)
		return topDomain;
	String domainRule = "(.*?://)?.*\\.(.*(\\.com|\\.net)(\\.cn)?)(:\\d+)?(/.*?|$)";
	Pattern domainPattern = Pattern.compile(domainRule, 2);
	Matcher matcher = domainPattern.matcher(uri);
	if (matcher.find())
		topDomain = matcher.group(2);
	else{
		domainRule = "(.*?://)?.*\\.(.*(\\.net|\\.cn|\\.cc|\\.tv))(:\\d+)?(/.*?|$)";
		domainPattern = Pattern.compile(domainRule, 2);
		matcher = domainPattern.matcher(uri);
		if (matcher.find())
			topDomain = matcher.group(2);
	}
	return topDomain;
}

public String _getSiteName(String currUrl, String siteType) {
	String test = _isTestSite(currUrl) ? "test." : "";
	String domain = _getBrandDomain(currUrl);
	if (siteType == "img2") test = "";
	return "//" + test + siteType + domain;
}

public String getImgPath(String sku, String imgSize, String imgType) {
	String url = "";
	return url;
}

public String getBrand(String sku) {
	return "";
}
%>

<%
Map _sName = new HashMap();
	_sName.put("_siteServerName",request.getServerName());
	_sName.put("www", _getSiteName(request.getServerName(), "www"));
	_sName.put("my", _getSiteName(request.getServerName(), "my"));
	_sName.put("passport", _getSiteName(request.getServerName(), "passport"));
	_sName.put("act", _getSiteName(request.getServerName(), "act"));
	_sName.put("pay", _getSiteName(request.getServerName(), "pay"));
	_sName.put("img2", _getSiteName(request.getServerName(), "static"));
%>