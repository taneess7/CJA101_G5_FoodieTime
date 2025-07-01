package com.foodietime.act.model;

import java.text.Normalizer;
import java.util.List;

public class StringUtil {
	// 將輸入標準化：全形轉半形、去除特殊符號、修剪空白 normalize 處理 人為資料輸入差異 
	public static boolean containsNormalized(String source, String keyword) {
        if (source == null || keyword == null) return false;
        String normalized = Normalizer.normalize(source.trim(), Normalizer.Form.NFKC);
        return normalized.contains(keyword);
    }
	
	
	public static boolean containsAnyNormalized(String source, List<String> targets) {
	    if (source == null || targets == null) return false;
	    String normalizedSource = Normalizer.normalize(source, Normalizer.Form.NFKC).trim();
	    return targets.stream().anyMatch(target ->
	        normalizedSource.contains(Normalizer.normalize(target, Normalizer.Form.NFKC).trim())
	    );
	}

}

