package fulton.util.pc_java;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import fulton.util.java.js.BaseJavaScriptEngineInterfaceImpl;
import fulton.util.java.optional_exceptions.ErrorWrappedException;

public class JsNashornWrapper extends BaseJavaScriptEngineInterfaceImpl {

	private static ScriptEngineManager mEngineManager;
	private ScriptEngine mJs;
	
	public JsNashornWrapper() {
		mJs=getScriptEngineManager().getEngineByName("JavaScript");
	}
	
	@Override
	public Object eval(String s) {
		// TODO Auto-generated method stub
		try {
			return mJs.eval(s);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			throw new ErrorWrappedException(e);
		}
	}
	
	public static ScriptEngineManager getScriptEngineManager()
	{
		if(mEngineManager==null)
			mEngineManager=new ScriptEngineManager();
		return mEngineManager;
	}

}
