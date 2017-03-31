package appCache;

public class CacheObject<T> {
	private int frequency;
	private T t;
	
	public CacheObject(T t){
		this.t = t;
		this.frequency = 1;
	}

	public int getFrequency() {
		return frequency;
	}

	public T getT() {
		frequency++;
		return t;
	}
	
	public String toString(){
		return t.toString();
	}
}
