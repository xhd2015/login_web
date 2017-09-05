package privacy;

public class FlexiableValue {
	private Object mData;
	
	
	public FlexiableValue(Object mData) {
		super();
		this.mData = mData;
	}
	public String toString()
	{
		return String.valueOf(mData);
	}
	public Integer toInteger()
	{
		return Integer.valueOf(toString());
	}
	
}
