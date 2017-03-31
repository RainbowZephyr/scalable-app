package appCache;

import java.util.Hashtable;

public class AppCache<T> {
	@SuppressWarnings("rawtypes")
	Hashtable<String, CacheObject> storage;
	ReplacementPolicy rp;
	public AppCache(int size){
		this(new LFUReplacementPolicy(), size);
		storage = new Hashtable<String, CacheObject>();
		rp.setCacheHashtable(storage);
	}
	
	public AppCache(ReplacementPolicy rp, int size){
		this.rp = rp;
		rp.setSize(size);
	}
	
	public synchronized void add(String key, T t){
		CacheObject<T> cacheObject = new CacheObject<T>(t);
		rp.add(key, cacheObject);
	}
	
	public T get(String key){
		T t = (T) storage.get(key).getT();
		return t;
	}
	
	
	public static void main(String[] args) {
		AppCache<Integer> x = new AppCache<Integer>(3);
		x.add("1", 101);
		x.get("1");
		System.out.println(x.storage);
		x.add("2", 202);
		System.out.println(x.storage);
		x.add("3", 303);
		System.out.println(x.storage);
		x.add("4", 404);
		System.out.println(x.storage);
	}
}
