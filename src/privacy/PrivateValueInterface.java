package privacy;

public interface PrivateValueInterface {
	
	String USER_ID="userId";
	String USER_DEFAULT_PASSWORD="userDefaultPassword";
	
	public Object getValue(String key);
}
