package com.recsysclient.utility;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import android.util.Log;

import com.recsysclient.entity.ContextInfo;
import com.recsysclient.entity.PoI;

public class IntentHelper {

    private static IntentHelper instance;
    private static Hashtable<String, Object> hash;
    private static ContextInfo ci;
    private static Set<PoI> ps;


    private static IntentHelper getInstance() {
        if(instance==null) {
            instance = new IntentHelper();
        }
        return instance;
    }

    public static void addObjectForKey(String key, Object object) {
        getInstance().hash.put(key, object);
    }
    
    public static void addSetOfObjectForKey(String key, Set object) {
    	Set cloned = new HashSet(object);
        getInstance().hash.put(key, cloned);
    }

    public static Object getObjectForKey(String key) {
        IntentHelper helper = getInstance();
        Object data = helper.hash.get(key);
        helper.hash.remove(key);
        if(helper.hash.isEmpty())
        	helper = null;
        return data;
    }

	public static ContextInfo getCi() {
		return ci;
	}

	public static void setCi(ContextInfo ci) {
		IntentHelper.ci = ci;
	}

	public static Set<PoI> getPs() {
		return ps;
	}

	public static void setPs(Set<PoI> pois) {
		/*for(PoI p : ps)
			Log.e("IH", p.toString());
		*/
		IntentHelper.ps = new HashSet<PoI>();
		for(PoI p : pois)
			IntentHelper.ps.add(p);
		Log.w("IHHHH","asasda");
	}
}