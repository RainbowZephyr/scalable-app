package appCache;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

class LFUReplacementPolicy implements ReplacementPolicy {
	
	int size;
	Hashtable<String, CacheObject> cacheObjects;
	
	public LFUReplacementPolicy(){
		this.size = 3;
	}
	
	public void setSize(int size) {
		this.size = size;
		
	}
	
	public void setCacheHashtable(Hashtable<String, CacheObject> cacheObjects){
		this.cacheObjects = cacheObjects;
	}

	public synchronized void add(String id, CacheObject newCacheObject) {
		if(!cacheObjects.containsKey(id) && cacheObjects.size() == this.size){
			Iterator<Entry<String, CacheObject>> i = cacheObjects.entrySet().iterator();
			int minFrequency = Integer.MAX_VALUE;
			String minFreqCacheObjectId = null;
			while(i.hasNext()){
				Entry<String, CacheObject> tempEntry = i.next();
				CacheObject tempObject = tempEntry.getValue();
				String tempId = tempEntry.getKey();
				if(tempObject.getFrequency() < minFrequency){
					minFreqCacheObjectId = tempId;
					minFrequency = tempObject.getFrequency();
				}
			}
			cacheObjects.remove(minFreqCacheObjectId);
			
		}
		cacheObjects.put(id, newCacheObject);
	}

	
}
