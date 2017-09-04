package utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EngineUtils {
	public static void main(String...args) throws ScriptException {
		ScriptEngineManager manager=new ScriptEngineManager();
		ScriptEngine js=manager.getEngineByName("JavaScript");
		
		String scriptFile="D:\\installed\\cygwin\\home\\13774\\temp\\security.js";
		String s=FileUtils.readFile(scriptFile,"utf8");
	
		js.eval("var window={};");
		js.eval(s);
		js.eval("with(window){var key = new RSAUtils.getKeyPair('10001', '', "+
				"'d689ddd5394862b76a71ad9c1233a662d60c06ad53cc"+
				"03dd33c73332f23db06d279d9aaf85bec2e0ac991c6ffee00fe80e9a0b8c3e703dbc569ad6b47d657563');\n"+
				"var rsaUsercode = RSAUtils.encryptedString(key, '1144420112');\n"+
		        "var rsaPassword = RSAUtils.encryptedString(key, 'Xhd11');\n"+
				"}"
				);
		Object o=js.eval("'usercode=' + rsaUsercode + '&password=' + rsaPassword + '&code='");
		System.out.println(o);
	}
}
