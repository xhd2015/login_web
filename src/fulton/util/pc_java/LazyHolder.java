package fulton.util.pc_java;

public class LazyHolder<E> {
	private E e;
	private boolean mInited;
	private OnGetValue<E> mGetter;
	
	public interface OnGetValue<E>{
		E get();
	}
	
	public LazyHolder(OnGetValue<E> getter)
	{
		if(getter==null)
			throw new NullPointerException();
		mGetter=getter;
		mInited=false;
	}

	public E getE() {
		return e;
	}

	public E get() {
		if(!mInited)
			e=mGetter.get();
		return e;
	}
	
	
}
