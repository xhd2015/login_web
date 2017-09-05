package examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import fulton.util.java.fetcher.HITLoginFetcher;
import fulton.util.java.optional_exceptions.ErrorWrappedException;
import fulton.util.java.utils.FileUtils;
import fulton.util.pc_java.LazyHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

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
			System.out.println("usage: $jar [login|proxy|cross-domain] ...");
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
			}else if(args[0].equalsIgnoreCase("cross-domain")){
				cross_domain(newArgs);
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
		
	}

	
	/**
	 *  
	 * @param args
	 *       port script-file
	 */
	public static void cross_domain(String[] args)
	{
		if(args.length<2)
		{
			System.err.println("usage: cross-domain PORT SCRIPT-FILE\n"+
								"example: cross-domain 5535  index.html \n"+
								"  this will open a proxy server on port 5535,and any request to "+
								"*/cross-domain will be redirected to index.html(i.e. that file's content"+
								" is returned as the response\n"
					);
			
		}
		int port=-1;
		LazyHolder<ByteBuf> scontent=null;
		try{
			port=Integer.valueOf(args[0]);
			final File f=new File(args[1]);
			scontent=new LazyHolder<>(new LazyHolder.OnGetValue<ByteBuf>() {
				public ByteBuf get()
				{
					InputStream is=null;
					try{
						is=new FileInputStream(f);
						ByteBuf buf=ByteBufUtil.threadLocalDirectBuffer().capacity(is.available());
						buf.writeBytes(is, is.available());
					
						return buf;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						throw new ErrorWrappedException(e);
					}finally{
						if(is!=null)
							try {
								is.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								
							}
					}
				}
			});
		}catch (Exception e) {
			// TODO: handle exception
			System.err.println("You have some error in input,details:");
			e.printStackTrace();
			return;
		}
		final LazyHolder<ByteBuf> content=scontent;
		HttpProxyServer server=DefaultHttpProxyServer.bootstrap()
				.withPort(port)
				.withFiltersSource(new HttpFiltersSourceAdapter(){

					@Override
					public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
						System.out.println("=========new request===============");
						System.out.println(originalRequest);

						final String refer=originalRequest.headers().get("Referer");
						final String uri=originalRequest.getUri();
						
						if(uri!=null && uri.endsWith("cross-domain") )
						{
								System.out.println("=============get cross-domain script==================");
								return new HttpFiltersAdapter(originalRequest, ctx){
	
									@Override
									public HttpObject proxyToClientResponse(HttpObject httpObject) {
										
										FullHttpResponse resp=new 
												DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
														HttpResponseStatus.OK,
														content.get().duplicate()
														);
										
										resp.headers().add("Content-Length",""+content.get().capacity());
										resp.headers().add("Connection","Close");
										resp.headers().add("Content-Type","text/html; charset=UTF-8");
										
										return resp;
									}
								};
//						}else if(refer!=null && refer.endsWith("cross-domain")){
//							System.out.println("=============refer cross-domain script==================");
//							return new HttpFiltersAdapter(originalRequest, ctx){
//
//								@Override
//								public HttpObject proxyToClientResponse(HttpObject httpObject) {
//									// TODO Auto-generated method stub
//									HttpResponse resp=new 
//											DefaultHttpResponse(HttpVersion.HTTP_1_1,
//													HttpResponseStatus.NOT_FOUND
//													);
//									
//									resp.headers().add("Connection","Close");
//									
//									return resp;
//								}
//								
//							};
							
						}else{
							return super.filterRequest(originalRequest, ctx);
						}
						
					}
					
				})
				.start();
	}
		
		
}
