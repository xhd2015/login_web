package fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import fulton.util.java.optional_exceptions.ErrorWrappedException;

public class WholeBufferedReader extends BufferedReader{

	public WholeBufferedReader(Reader in) {
		super(in);
	}
	
	public StringBuilder readAll()
	{
		StringBuilder sb=new StringBuilder();
		String s=null;
		try {
			while( (s=readLine())!=null)
				sb.append(s).append(System.lineSeparator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ErrorWrappedException(e);
		}
		return sb;
	}

}
