package com.elex.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;

public class MahoutUtil {
	public static String getItemCount(Iterable<Text> values){
		HashMap<String, Integer> itemMap = new HashMap<String, Integer>();
		for(Text value:values){
			String tmp = value.toString();
			if(itemMap.get(tmp) != null){
				itemMap.put(tmp, itemMap.get(tmp) + 1);
			} else{
				itemMap.put(tmp, 1);
			}
		}
		StringBuffer result = new StringBuffer();
		for(Map.Entry<String, Integer> map:itemMap.entrySet()) {
			String item = map.getKey();
			int count = map.getValue();
			result.append(item + ":" + count + ",");
		}
		return result.toString();
	}
	public static float getWeight(Iterable<Text> values){
		HashMap<String, Integer> itemMap = new HashMap<String, Integer>();
		HashMap<String, Float> userMap = new HashMap<String, Float>();
		for(Text value:values){
			String tmp = value.toString();
			String []strSlit = tmp.split("\t");
			if(strSlit.length == 2){
				userMap.put(strSlit[0], Float.parseFloat(strSlit[1]));
			} else{
				if(itemMap.get(tmp) != null){
					itemMap.put(tmp, itemMap.get(tmp) + 1);
				} else{
					itemMap.put(tmp, 1);
				}
			}
		}
		float result = 0.0f;
		for(Map.Entry<String, Float> map:userMap.entrySet()){
			if(itemMap.get(map.getKey()) != null)
				result += map.getValue() * itemMap.get(map.getKey());
		}
		return result;
	}
}
