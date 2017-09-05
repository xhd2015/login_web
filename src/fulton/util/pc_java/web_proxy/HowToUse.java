package fulton.util.pc_java.web_proxy;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HowToUse {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HttpProxyServer server=DefaultHttpProxyServer.bootstrap()
				.withPort(5535)
				.withFiltersSource(new HttpFiltersSourceAdapter(){

					@Override
					public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
						// TODO Auto-generated method stub
						System.out.println(originalRequest);
						
						if(
								"jwts.hit.edu.cn".equals(originalRequest.headers().get("Host")) ||  (
								originalRequest.headers().contains("x-set-cross") ||
								"x-set-cross".equals(originalRequest.headers().get("Access-Control-Request-Headers"))
								))
						{
							System.out.println("entering filter");
						return new HttpFiltersAdapter(originalRequest, ctx){
							@Override
							public HttpObject proxyToClientResponse(HttpObject httpObject) {
								System.out.println("calling p2c");
								if(httpObject instanceof HttpResponse)
								{
									HttpResponse response=(HttpResponse) httpObject;
									if(!HttpMethod.OPTIONS.equals(originalRequest.getMethod()))
									{
										response.headers().add("Access-Control-Allow-Origin","*");
							  			response.headers().add("Access-Control-Allow-Headers","*");
									 // response.headers().add("Access-Control-Expose-Headers","*");
									 // response.headers().add("Access-Control-Allow-Credentials","true");
									}
								}
								System.out.println(httpObject);
								return httpObject;
							}

							@Override
							public HttpResponse proxyToServerRequest(HttpObject httpObject) {
								// TODO Auto-generated method stub
//								HttpObject
								System.out.println("calling p2s");
								System.out.println("obj==ori?"+(httpObject==originalRequest));
								if(HttpMethod.OPTIONS.equals(originalRequest.getMethod())){
									System.out.println("options");
									DefaultHttpResponse response=
											new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
									response.headers().add("Access-Control-Allow-Origin","*");
									response.headers().add("Access-Control-Allow-Headers","x-set-cross");
									  response.headers().add("Access-Control-Expose-Headers","*");
									  //response.headers().add("Access-Control-Allow-Credentials","true");
									response.headers().add("Connection","close");
									response.headers().add("Content-Length", "0");
									return response;
								}else{
									return super.proxyToServerRequest(httpObject);
								}
							}
							
							
							
						};
						}else{
							return super.filterRequest(originalRequest,ctx);
						}
					}
					
				})
				.start();
	}

}
