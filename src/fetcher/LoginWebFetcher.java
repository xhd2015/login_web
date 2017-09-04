package fetcher;

import fulton.util.java.js.JavaScriptEngineInterface;

public  abstract class LoginWebFetcher extends WebFetcher{
	
	public abstract boolean isLogin();
	public abstract void login();
	public abstract void logout();


	public abstract String getLoginEntryURI();
	public abstract String getLoginURI();
	public abstract String getCookie();//we assume that you own a cookie
	/**
	 *   by calling this method, you set an already-logged-in cookie.
	 * @param cookie
	 */
	public abstract void setLoggedInCookie(String cookie);
	public abstract void setUserInfo(String userName,String password);
	
	public abstract JavaScriptEngineInterface getJSEngine();
	/**
	 *   this is always cachable.
	 * @return
	 */
	public abstract String[]  getAllContextScripts();
	
	
}
