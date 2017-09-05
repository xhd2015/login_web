package fulton.util.pc_java.web_proxy;

import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpStatus;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import fulton.util.java.tuples.Single;
import fulton.util.java.utils.FileUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentEncoder;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

public class HowToUse2 {

	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		if(args.length < 4)
		{
			System.out.println("usage : proxy 5535 username password proxy_script");
			return;
		}
		int port=Integer.parseInt(args[0]);
		HttpProxyServer server=DefaultHttpProxyServer.bootstrap()
				.withPort(port)
				.withFiltersSource(new HttpFiltersSourceAdapter(){

					@Override
					public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
						// TODO Auto-generated method stub
						System.out.println("=========new request===============");
						System.out.println(originalRequest);
						if(originalRequest.getUri().startsWith(
								"http://resources.crossrider.com/apps/68915/resources/meta/0?"))
						{
							System.out.println("============noooooo==================");
							return new HttpFiltersAdapter(originalRequest, ctx){

								@Override
								public HttpResponse proxyToServerRequest(HttpObject httpObject) {
									// TODO Auto-generated method stub
									HttpResponse resp=new DefaultHttpResponse(HttpVersion.HTTP_1_1,
											HttpResponseStatus.OK);
									resp.headers().add("Content-Length","0");
									resp.headers().add("Connection","Close");
									return resp;
								}
								
							};
						}else if("http://jwts.hit.edu.cn/loginLdap?fake-cross=true".equals(originalRequest.getUri())){
							System.out.println("=============get fake-cross script==================");
							return new HttpFiltersAdapter(originalRequest, ctx){

								

								@Override
								public HttpResponse clientToProxyRequest(HttpObject httpObject) {
									// TODO Auto-generated method stub
									
									InputStream is=null;
									ByteBuf buf=null;
									
									try {
										is = new FileInputStream(new File(args[3]));
									
									
										buf=ByteBufUtil.threadLocalDirectBuffer().capacity(is.available());
										int curByte=-1;
										while( (curByte=is.read())!=-1)
										{
											buf.writeByte(curByte);
										}
										is.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										
									}
									
									FullHttpResponse resp=new 
											DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
													HttpResponseStatus.OK,buf
													);
									
									
									resp.headers().add("Content-Length",""+buf.capacity());
									resp.headers().add("Connection","Close");
									resp.headers().add("Content-Type","text/html; charset=UTF-8");
									
									return resp;
								}

								
								
							};
							
						}else if("http://jwts.hit.edu.cn/loginLdapQian".equals(originalRequest.getUri()))
						{
							final String inserted=String.format(FileUtils.getStringOfInputStream(
									HowToUse.class.getResourceAsStream("insert_newest.js"),-1,"utf8"),
									args[1],args[2]);
							System.out.println(inserted);
							final String locater="$().ready(function(){";
							byte[] decodedOri=null;
							byte[] decodedLocOri=null;
							try {
								decodedOri = inserted.getBytes("utf8");
								decodedLocOri = locater.getBytes("utf8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							final Single<Integer> order=new Single<>(-1);
							final Single<Integer> accLength=new Single<>(0);
							final byte[] decoded=decodedOri;
							final byte[] decodedLoc=decodedLocOri;
							return new HttpFiltersAdapter(originalRequest, ctx){

								@Override
								public HttpObject proxyToClientResponse(HttpObject httpObject) {
									// TODO Auto-generated method stub
									System.out.println("=====p2c=====");
									order.first++;
									System.out.println("===order==="+order.first);
									
									System.out.println(httpObject);
									if(order.first==0)
									{
										HttpResponse response=(HttpResponse) httpObject;
										response.headers().set("Content-Length",
												Long.valueOf(response.headers().get("Content-Length"))+decoded.length
												);	
									}else{
										HttpContent content=(HttpContent) httpObject;
										System.out.println("current length:"+ content.content().capacity());
										if(accLength.first!=null)
										{
										accLength.first+=content.content().capacity();
										if(accLength.first>1024)
										{
											accLength.first=null;
											ByteBuf x=content.content().duplicate();
											x.retain();
											ByteBuf newBuf=
													ByteBufUtil.threadLocalDirectBuffer().capacity(x.capacity()+
															decoded.length
															);
											
											int curIndex=0;
											while(x.isReadable())
											{
												byte curByte=x.readByte();
												if(curIndex<decodedLoc.length)
												{
													if(curByte==decodedLoc[curIndex])
														curIndex++;
													else
														curIndex=0;
												}else if(curIndex==decodedLoc.length)
												{
													System.out.println("index is "+x.readerIndex());
													for(int i=0;i<decoded.length;i++)
														newBuf.writeByte(decoded[i]);
													curIndex++;
												}
												newBuf.writeByte(curByte);
												
											}
											
											
											
											DefaultHttpContent newContent=new DefaultHttpContent(newBuf);
									
											
											return newContent;
										}
										}
											
									}

									return super.proxyToClientResponse(httpObject);
								}
								
							};
						}
						return super.filterRequest(originalRequest, ctx);
					}
					
				}).start();
	}

}
