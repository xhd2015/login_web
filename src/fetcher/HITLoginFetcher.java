package fetcher;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fulton.util.java.js.JavaScriptEngineInterface;
import fulton.util.java.optional_exceptions.ErrorWrappedException;
import fulton.util.java.utils.StringUtils;
import utils.FileUtils;
import utils.NetUtils;


public class HITLoginFetcher extends LoginWebFetcher{
	
	private String mCookie;
	private String mUserName;
	private String mPassword;
	private String[] mJsContext;
	private JavaScriptEngineInterface mJs;
	private boolean mMayLoggedIn=false;
	
	public static final String sSecurityScript="D:\\Pool\\eclipse-workspace_New\\fetcher\\src\\res\\security.js";
	public static final String sLoginEntryUri="http://jwts.hit.edu.cn/loginLdapQian";
	public static final String sLoginUri="http://jwts.hit.edu.cn/loginLdap";
	public static final String sViewUserInfoUri="http://jwts.hit.edu.cn/xswhxx/queryXswhxx";
	/**
	 *  you may append 'xx' -- ÏÞÑ¡ to it.
	 */
	public static final String sChooseCoursesUri="http://jwts.hit.edu.cn/xsxk/queryXsxk?pageXklb=";
	
	/**
	 *  this requires data like:
	 *   pageXnxq  x-(x+1) 1-Çï,2-´º 3-ÏÄ
	 *  
	 *  'rwh=&zy=&qz=&token=&
	 *  pageXklb=xx&
	 *  pageXnxq=2017-20181
	 *  &pageKkxiaoqu=&pageKkyx=&pageNj=&pageYxdm=&pageZydm=&pageBjdm=&pageKcmc='
	 */
	public static final String sChosenCoursesUri="http://jwts.hit.edu.cn/xsxk/queryYxkc";
	/**
	 *  providing "xx" and "2016-20171"
	 */
	public static final String sFormatterOfChosenCoursesUriData="rwh=&zy=&qz=&token=&pageXklb=%s&pageXnxq=%s"
			+ "&pageKkxiaoqu=&pageKkyx=&pageNj=&pageYxdm=&pageZydm=&pageBjdm=&pageKcmc=";
	
	public static final String sViewGrades="http://jwts.hit.edu.cn/cjcx/queryQmcj";
	/**
	 *  currently no parameters.
	 */
	public static final String sFormmaterOfViewGradesData="pageXnxq=&pageBkcxbj=&pageSfjg=&pageKcmc=";
	
	
	
	public static String fetchKey(Document doc)
	{
//		Elements e=doc.select("script");
//		System.out.println(e.size());
//		System.out.println(e.get(3).data());
		return doc.select("script").get(3).data();
	}
	
	public static String[] fetchSetCookies(HttpResponse resp)
	{
		Header[] headers=resp.getHeaders("Set-Cookie");
		String[] reStrings=new String[headers==null?0:headers.length];
		int index=0;
		if(headers!=null)
			for(Header h:headers)
				reStrings[index++]=h.getValue().substring(0,h.getValue().indexOf(';'));
		return reStrings;
	}

	@Override
	public boolean isLogin() {
		// TODO Auto-generated method stub
		return mMayLoggedIn;
	}

	@Override
	public void login() {
		//fetch cookie & key
		//
		HttpResponse resp=NetUtils.get(getLoginEntryURI());
		Document doc=Jsoup.parse(NetUtils.getContentString(resp));
		String data=this.getEncrptedFormData(fetchKey(doc),mUserName,mPassword);
		this.mCookie=StringUtils.join("; ",fetchSetCookies(resp));
		HttpResponse confirmResp=NetUtils.post(getLoginURI(), data, "Cookie",
				this.mCookie);
		if(NetUtils.getContentString(confirmResp).contains("id=\"updMm\""))
		{
			this.mMayLoggedIn=true;
		}else{
			this.mCookie=null;
			this.mMayLoggedIn=false;
		}
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLoginEntryURI() {
		// TODO Auto-generated method stub
		return sLoginEntryUri;
	}

	@Override
	public String getCookie() {
		// TODO Auto-generated method stub
		return mCookie;
	}

	@Override
	public void setLoggedInCookie(String cookie) {
		// TODO Auto-generated method stub
		mCookie=cookie;
		mMayLoggedIn=true;
	}

	@Override
	public void setUserInfo(String userName, String password) {
		// TODO Auto-generated method stub
		this.mUserName=userName;
		this.mPassword=password;
	}
	
	/**
	 *  get the form keys.
	 * @param jsKey
	 * @param username
	 * @param password
	 * @return
	 */
	public String getEncrptedFormData(String jsKey,String username,String password)
	{
		JavaScriptEngineInterface js=getJSEngine();
		js.eval("var window={};");
		for(String s:getAllContextScripts())
			js.eval("window",s);
		js.eval("window",jsKey);
		js.eval("window","var rsaUsercode = RSAUtils.encryptedString(key, \""+username+"\");\n"+
				"var rsaPassword = RSAUtils.encryptedString(key, \""+password+"\");\n");
		String res=(String) js.eval("\"usercode=\" + rsaUsercode + \"&password=\" + rsaPassword + \"&code=\"");
		return res;
	}

	@Override
	public JavaScriptEngineInterface getJSEngine() {
		// TODO Auto-generated method stub
		if(mJs==null)
		{
			ScriptEngineManager manager=new ScriptEngineManager();
			final ScriptEngine js=manager.getEngineByName("JavaScript");
			mJs=new JavaScriptEngineInterface() {
				
				@Override
				public Object eval(String s) {
					// TODO Auto-generated method stub
					try {
						return js.eval(s);
					} catch (ScriptException e) {
						// TODO Auto-generated catch block
						throw new ErrorWrappedException(e);
					}
				}
	
				@Override
				public Object eval(String withVar,String s) {
					// TODO Auto-generated method stub
					return eval("with("+withVar+"){"+s+"}");
				}
	
				@Override
				public Object evalMulitLines(String withVar, String... lines) {
					// TODO Auto-generated method stub
					return eval(withVar,StringUtils.join("\n",lines));
				}
				
			};
		}
		return mJs;
	}

	@Override
	public String[] getAllContextScripts() {
		// TODO Auto-generated method stub
		if(mJsContext==null)
		{
			mJsContext=new String[]{
					FileUtils.readFile(sSecurityScript,"utf8")
			};
		}
		return mJsContext;
	}
	


	@Override
	public String getLoginURI() {
		// TODO Auto-generated method stub
		return sLoginUri;
	}
	
	
	public static void HowToUse()
	{
		//==== init a logger.
		HITLoginFetcher hit=new HITLoginFetcher();
		
		
		//== down here, fill in you usercode & password
		//== Note:for security reason, the original usercode & password are masked.
		hit.setUserInfo("114********","**********");
		
		//== post a request to login the page
		hit.login();
		
		//== Am I logged in?
		if(hit.isLogin())
		{
			//=== take a look at your personal information
			show(sViewUserInfoUri,"Cookie",hit.getCookie());
			
			//===  see how many courses you have chosen
			showPost(sChosenCoursesUri,String.format(sFormatterOfChosenCoursesUriData,"xx","2017-20181"),					
					"Cookie",hit.getCookie());
			
			//===  see your grades through the whole life of university.
			showPost(sViewGrades,sFormmaterOfViewGradesData,"Cookie",hit.getCookie());
			
			
		}else{//==bad luck, seems that or your userinfo is not correct, 
			//== or the pages have been updated so that the original workaround fails.
			//==== if you find it is the latter reason that fails it, inform us to make an update.
			
			System.out.println("Sorry,login failed");
		}
	}
	public static void show(String uri,String...headers)
	{
		HttpResponse response=NetUtils.get(uri,headers);
		System.out.println(NetUtils.getContentString(response));
	}
	public static void showPost(String uri,String data,String...headers)
	{
		HttpResponse response=NetUtils.post(uri,data,headers);
		System.out.println(NetUtils.getContentString(response));
	}
	
	
	
	
	
}
