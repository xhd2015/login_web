package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import fetcher.WholeBufferedReader;
import fulton.util.java.optional_exceptions.ErrorWrappedException;

public class FileUtils {
	
	/**
	 * 
	 * @param file
	 * @param charset  null= use default
	 * @param num   -1="all"
	 * @return
	 */
	public static String readFile(String file,String charset)
	{
		WholeBufferedReader reader=null;
		try {
		if(charset==null)
			reader=new WholeBufferedReader(new FileReader(file));
		else
			reader=new WholeBufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		return reader.readAll().toString();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new ErrorWrappedException(e);
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new ErrorWrappedException(e);
			}
		}
}
}
