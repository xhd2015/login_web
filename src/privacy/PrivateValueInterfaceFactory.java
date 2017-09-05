package privacy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.PreferencesFactory;

import fulton.util.java.optional_exceptions.ErrorWrappedException;

public class PrivateValueInterfaceFactory {

	public static final String INTERFACE_FULTON="fulton";
	
	public static PrivateValueInterface getInterface(String name)
	{
		
		try {
			Class clz=null;
			if(INTERFACE_FULTON.equals(name))
			{
				clz=Class.forName("privacy.of_fulton.FultonValueInterface");
			}else{
				return null;
			}
			Method m=clz.getMethod("getInstance");
			return (PrivateValueInterface) m.invoke(null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new ErrorWrappedException(e);
		}
	}
	
	public static void main(String...args)
	{
		HowToUse();
	}
	public static void HowToUse()
	{
		PrivateValueInterface pi=PrivateValueInterfaceFactory.getInterface(INTERFACE_FULTON);
		System.out.println(pi.getValue(PrivateValueInterface.USER_DEFAULT_PASSWORD));
	}
}
