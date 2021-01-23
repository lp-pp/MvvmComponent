package com.lp.base.storage;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.mmkv.MMKV;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:  
 * <p>
 * 腾讯MMKV序列化存储工具类
 *
 * @author: lp
 * @date: 2020/10/15 
 * @version 1.0.0
 */
public class MmkvHelper {
    
    private static volatile MmkvHelper mMmkvHelper;
    private static MMKV mMmkv;

    private MmkvHelper() {
        mMmkv = MMKV.defaultMMKV();
    }

    /**
     * 获取单例对象
     *
     * @return 返回初始化后的单例对象
     */
    public static MmkvHelper getInstance() {
        if (mMmkvHelper == null) {
            synchronized(MmkvHelper.class) {
                if (mMmkvHelper == null) {
                    mMmkvHelper = new MmkvHelper();
                }
            }
        }
        return mMmkvHelper;
    }

    public MMKV getMmkv() {
        return mMmkv;
    }

    /**
     * 存入map集合
     *
     * @param key 标识
     * @param map 数据集合
     */
    public void setInfo(String key, Map<String, Object> map) {
        Gson gson = new Gson();
        JSONArray mJsonArray = new JSONArray();
        JSONObject object = null;
        try {
            object = new JSONObject(gson.toJson(map));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonArray.put(object);
        mMmkv.encode(key, mJsonArray.toString());
    }

    /**
     * 获取map集合
     *
     * @param key 标识
     */
    public Map<String, String> getInfo(String key) {
        Map<String, String> itemMap = new HashMap<>();
        String result = mMmkv.decodeString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemMap;
    }

    /**
     * 扩展MMKV类使其支持对象存储
     */
    public <T> T getObject(String key, Class<T> t) {
        String str = mMmkv.decodeString(key, null);
        if (!TextUtils.isEmpty(str)) {
            return new GsonBuilder().create().fromJson(str, t);
        } else {
            return null;
        }
    }

    public void putObject(String key, Object object) {
        String jsonString = new GsonBuilder().create().toJson(object);
        mMmkv.encode(key, jsonString);
    }

}