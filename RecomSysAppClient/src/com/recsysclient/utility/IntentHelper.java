package com.recsysclient.utility;

import java.util.Hashtable;

public class IntentHelper {

    private static IntentHelper instance;
    private Hashtable<String, Object> hash;

    private IntentHelper() {
        hash = new Hashtable<String, Object>();
    }

    private static IntentHelper getInstance() {
        if(instance==null) {
            instance = new IntentHelper();
        }
        return instance;
    }

    public static void addObjectForKey(String key, Object object) {
        getInstance().hash.put(key, object);
    }

    public static Object getObjectForKey(String key) {
        IntentHelper helper = getInstance();
        Object data = helper.hash.get(key);
        /*helper.hash.remove(key);
        if(helper.hash.isEmpty())
        	helper = null;*/
        return data;
    }
}