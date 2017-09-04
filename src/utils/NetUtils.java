package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fetcher.HITLoginFetcher;
import fulton.util.java.optional_exceptions.ErrorWrappedException;

public class NetUtils {
	
	public static String getStringOfInputStream(InputStream is,long length,String charset)
	{
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(is, charset==null?"utf8":charset));
			StringBuilder sbBuilder=new StringBuilder();
			String s=null;
			while( (s=reader.readLine())!=null)
				sbBuilder.append(s).append(System.lineSeparator());
			return sbBuilder.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static String toString(Content content)
	{
		return content.asString();
	}
	public static String getContentString(HttpResponse resp)
	{
		try {
			return getStringOfInputStream(resp.getEntity().getContent(),resp.getEntity().getContentLength(),
					null);
		} catch (UnsupportedOperationException | IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
	}
	
	
	public static void main(String...args) throws ClientProtocolException, IOException
	{
		testHIT();
	}
	public static void testHIT()
	{
		try {
			Response a=Request.Get("http://jwts.hit.edu.cn/loginLdapQian").execute();
			HttpResponse resp=a.returnResponse();
			Document doc=Jsoup.parse(getContentString(resp));
			System.out.println(HITLoginFetcher.fetchKey(doc));
			System.out.println(HITLoginFetcher.fetchSetCookies(resp));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Request setHeaders(Request request,String ...headers)
	{
		for(int i=0;i<headers.length;i+=2)
			request.addHeader(headers[i],headers[i+1]);
		return request;
	}
	
	public static HttpResponse get(String uri, String... headers)
	{
		Response a;
		try {
			a = setHeaders(Request.Get(uri),headers).execute();
			HttpResponse resp=a.returnResponse();
			return resp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ErrorWrappedException(e);
		}
	}
	
	public static HttpResponse post(String uri,String data,String...headers)
	{
		try{
			Request request=Request.Post(uri).bodyString(data,ContentType.APPLICATION_FORM_URLENCODED);
			setHeaders(request, headers);
			return request.execute().returnResponse();
		}catch (IOException e) {
			// TODO: handle exception
			throw new ErrorWrappedException(e);
		}
	}
	
	public static void useBuildAPI()
	{
		
	  try {
		Response a=Request.Get("http://jwts.hit.edu.cn/loginLdapQian").execute();
		HttpResponse resp=a.returnResponse();
		System.out.println(resp.getEntity().getContentEncoding());
		org.apache.http.Header[] cookies= resp.getHeaders("Set-Cookie");
		for(org.apache.http.Header h:cookies)
			System.out.println(h.getValue());
		System.out.println(getContentString(resp));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	public static void useTraditionalAPI() throws ClientProtocolException, IOException
	{
		CloseableHttpClient httpclient=HttpClients.createDefault();
		HttpGet get=new HttpGet("http://jwts.hit.edu.cn/loginLdapQian");
		CloseableHttpResponse response=httpclient.execute(get);
		try{
			System.out.println(response.getStatusLine());
			HttpEntity entity=response.getEntity();
			long len=entity.getContentLength();
			byte[] bytes=new byte[(int) len];
			entity.getContent().read(bytes);
			System.out.println(bytes);
		}catch (Exception e) {
			// TODO: handle exception
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
