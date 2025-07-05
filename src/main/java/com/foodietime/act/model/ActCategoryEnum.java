package com.foodietime.act.model;


import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;


import com.foodietime.product.model.ProductVO;


public enum ActCategoryEnum {
	

    // 類型 1: 商品類別或名稱符合，List.of複數選項，避免無法解析活動
    NEW_PRODUCT(List.of("新品上市", "草莓優惠"), (prod, act) ->
        prod.getProdName().contains("草莓")
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice()
    ),

    ITALIAN_FEST(List.of("義式美食節"), (prod, act) ->
    (prod.getProductCategory().getProdCate().contains("義式")||prod.getProdName().contains("義大利麵"))
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice()
    ),

    BBQ_DAY(List.of("燒烤日"), (prod, act) ->
    (prod.getProductCategory().getProdCate().contains("素")||prod.getProdName().contains("烤"))
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice()
    ),

    HOT_POT(List.of("火鍋季", "火鍋套餐"), (prod, act) ->
    (Optional.ofNullable(prod.getProductCategory().getProdCate()).orElse("").contains("火鍋") ||
     Optional.ofNullable(prod.getProdName()).orElse("").contains("鍋"))
    ? (int)(prod.getProdPrice() * act.getActDiscValue())
    : prod.getProdPrice()
    ),
    
    VEG_PROD(List.of("素食推廣"), (prod, act) -> 
        (prod.getProductCategory().getProdCate().contains("素")||prod.getProdName().contains("素"))
        ? (int)(prod.getProdPrice() * act.getActDiscValue())
        : prod.getProdPrice()
    ),
    
    FASTFOOD(List.of("速食優惠"), (prod, act) -> 
    (prod.getProductCategory().getProdCate().contains("美式")||prod.getProdName().contains("炸"))
    ? (int)(prod.getProdPrice() * act.getActDiscValue())
    : prod.getProdPrice()
    ),

 // 類型 2: 店家分類符合
    DRINK_DAY(List.of("優惠活動"), (prod, act) -> {
    	String catName = prod.getProductCategory().getProdCate(); // 改為商品分類名稱
    	    int price = prod.getProdPrice();
    	    double value = act.getActDiscValue();   // 折扣值
    	    Byte type = act.getActDiscount();    // 折扣類型：1 = 減價、0 = 百分比

    	    int finalPrice;

    	    if (type != null && type == 1) {
    	        // 減價（如折 5 元）
    	        finalPrice = price - (int) value;
    	    } else {
    	        // 百分比（如 0.85 表示 85 折）
    	        finalPrice = (int)(price * value);
    	    }

    	    // 最低價格不能小於 0
    	    finalPrice = Math.max(finalPrice, 0);

//    	    System.out.println("🧾 商品名稱: " + prod.getProdName());
//    	    System.out.println("🧾 店家分類: " + catName);
//    	    System.out.println("🧾 折扣方式: " + (type != null && type == 1 ? "減價" : "百分比"));
//    	    System.out.println("🧾 原價 = " + price + "，折扣價 = " + finalPrice);

    	    return catName != null && (catName.contains("甜") || catName.contains("飲")) 
    	        ? finalPrice 
    	        : price;
    	}),
    
    
    //名為 VEG_REC 的折扣規則，對應資料庫活動表中的 ACT_CATE 欄位，如果是"素食推廣"，就代表屬於這種活動類型。如果商品所屬的店家分類名稱（storCatName）中含有 素食，就給折扣，否則原價。」
    VEG_REC(List.of("素食推廣"), (prod, act) -> {
        return StringUtil.containsNormalized(prod.getStore().getStorCatName(), "素食")
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice();
    }),

    FASTFOOD_DISCOUNT(List.of("速食優惠"), (prod, act) -> {
        return StringUtil.containsNormalized(prod.getStore().getStorCatName(), "速食")
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice();
}),

	   // 類型 3: 通用折扣
    MEMBER_DAY(List.of("會員日"), (prod, act) ->
        (int)(prod.getProdPrice() * act.getActDiscValue())
    ),

    LIMITED_OFFER(List.of("季節優惠"), (prod, act) ->
        (int)(prod.getProdPrice() * act.getActDiscValue())
    ),

    // 類型 4: 購物車邏輯（顯示原價）
    BUY_ONE_GET_ONE(List.of("限時優惠","買一送一","開幕慶"), (prod, act) ->
        prod.getProdPrice()
    ),

    SECOND_HALF(List.of("第二件5折"), (prod, act) ->
        (int)(prod.getProdPrice() * act.getActDiscValue())
    );

	// 欄位
    private final List<String> aliases;
    private final BiFunction<ProductVO, ActVO, Integer> calculator;

    // 建構子
    ActCategoryEnum(List<String> aliases, BiFunction<ProductVO, ActVO, Integer> calculator) {
        this.aliases = aliases;
        this.calculator = calculator;
    }

    // 計算邏輯呼叫
    public int calculate(ProductVO prod, ActVO act) {
        return calculator.apply(prod, act);
    }

    // 轉換字串為 enum
    public static ActCategoryEnum from(String input) {
        if (input == null) return null;

        //從資料庫撈出來的 ACT_CATE 文字（像「限時優惠」、「素食推薦」、「飲品日」等），轉換成對應的 enum 物件
        String normalizedInput = Normalizer.normalize(input, Normalizer.Form.NFKC).trim();

        for (ActCategoryEnum e : values()) {
            for (String alias : e.aliases) {
                String normalizedAlias = Normalizer.normalize(alias, Normalizer.Form.NFKC).trim();
                if (normalizedInput.equalsIgnoreCase(normalizedAlias)) {
                	//System.out.println("✅ 成功對應活動類型：" + input + " → " + e.name());
                    return e;
                }
            }
        }
        
        //System.out.println("⚠ 沒有對應的商品，無法解析活動類型：" + input);
        return null;
    }

}
//		// 類型 2: 店家類別符合
//			     //方法1(storeVO沒有定義storeCatName)
//		if (actCate.contains("飲料")) {
//			return (prod, act) -> 
//			  Optional.ofNullable(prod.getStore())
//			  	.map(StoreVO::getStoreCate)
//			  	.map(StoreCateVO::getStorCatName)
//			  	.filter(name -> name.contains("飲料"))
//			  	.isPresent()
//			  	?(int)(prod.getProdPrice() * act.getActDiscValue())
//			  	: prod.getProdPrice();
//			}
//		
//		         //方法2
//		prod.getStore().getStorCatName() ✅（StoreVO重新定義了）
//	}
	
	
//	    優惠活動((prod, rule) -> prod.getProdPrice() - 5),
//	    新品上市((prod, rule) -> prod.getProdName().contains("草莓") ? (int)(prod.getProdPrice() * 0.9) : prod.getProdPrice()),
//	    限時優惠((prod, rule) -> prod.getProdPrice() / 2),
//	    會員日((prod, rule) -> (int)(prod.getProdPrice() * 0.85)),
//	    飲品日((prod, rule) -> prod.getProductCategory().getProdCate().contains("飲料") ? prod.getProdPrice() / 2 : prod.getProdPrice()),
//	    素食推薦((prod, rule) -> prod.getProductCategory().getProdCate().contains("素食") ? (int)(prod.getProdPrice() * 0.8) : prod.getProdPrice()),
//	    速食優惠((prod, rule) -> prod.getProductCategory().getProdCate().contains("速食") ? (int)(prod.getProdPrice() * 0.75) : prod.getProdPrice()),
//	    義式美食節((prod, rule) -> prod.getProdName().contains("義大利麵") ? (int)(prod.getProdPrice() * 0.9) : prod.getProdPrice()),
//	    燒烤日((prod, rule) -> prod.getProdName().contains("烤") ? (int)(prod.getProdPrice() * 0.75) : prod.getProdPrice()),
//	    火鍋季((prod, rule) -> prod.getProdName().contains("火鍋") ? prod.getProdPrice() - 50 : prod.getProdPrice());


