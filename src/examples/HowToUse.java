package examples;

import java.io.File;
import java.io.InputStream;

import com.squareup.okhttp.internal.Util;

import fulton.util.java.fetcher.HITLoginFetcher;
import fulton.util.java.utils.FileUtils;
import fulton.util.pc_java.web_proxy.HowToUse2;

public class HowToUse {

	/**
	 *  args: name,password
	 *  output: logged in cookie, if failed,nothing
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 0)
		{
			System.out.println("usage: $jar [login|proxy] ...");
			return;
		}else{
			String[] newArgs=new String[args.length-1];
			for(int i=0;i<newArgs.length;i++)
				newArgs[i]=args[i+1];
			if(args[0].equalsIgnoreCase("login"))
			{
				login(newArgs);
			}else if(args[0].equalsIgnoreCase("proxy")){
				proxy(newArgs);
			}else{
				System.out.println("unsupported function:"+args[0]);
			}
		}
	}
	
	public static void login(String[] args)
	{
		if(args.length<2)
		{
			System.out.println("usage: login name password");
			return;
		}
		HITLoginFetcher hit=new HITLoginFetcher();
		InputStream is=HowToUse.class.getResourceAsStream("/res/security.js");
		hit.setSecurityScript(FileUtils.getStringOfInputStream(is,-1, "utf8"));
		hit.setUserInfo(args[0],args[1]);
		hit.login();
		if(hit.isLogin())
			System.out.println(hit.getCookie());
	}
	public static void proxy(String[] args)
	{
		HowToUse2.main(args);
	}

}
