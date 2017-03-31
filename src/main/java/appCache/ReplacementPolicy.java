package appCache;

import java.util.Hashtable;

public interface ReplacementPolicy {
	public void setSize(int size);
	public void setCacheHashtable(Hashtable<String, CacheObject> c);
	public void add(String id, CacheObject t);
}
