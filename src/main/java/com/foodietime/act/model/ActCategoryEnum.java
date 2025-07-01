package com.foodietime.act.model;


import java.util.List;
import java.util.function.BiFunction;


import com.foodietime.product.model.ProductVO;


public enum ActCategoryEnum {
	

    // 類型 1: 商品名稱符合，List.of複數選項，避免無法解析活動
    NEW_PRODUCT(List.of("新品上市", "草莓優惠"), (prod, act) ->
        prod.getProdName().contains("草莓")
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice()
    ),

    ITALIAN_FEST(List.of("義式美食節"), (prod, act) ->
        prod.getProdName().contains("義大利麵")
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice()
    ),

    BBQ_DAY(List.of("燒烤日"), (prod, act) ->
        prod.getProdName().contains("烤")
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice()
    ),

    HOT_POT(List.of("火鍋季"), (prod, act) ->
        prod.getProdName().contains("火鍋")
            ? (int)(prod.getProdPrice() * act.getActDiscValue())
            : prod.getProdPrice()
    ),

	// 類型 2: 店家分類符合
	DRINK_DAY(List.of("飲品日"), (prod, act) -> 
	    prod.getStore().getStorCatName().contains("飲料")
	        ? (int)(prod.getProdPrice() * act.getActDiscValue())
	        : prod.getProdPrice()),

	VEG_REC(List.of("素食推薦"), (prod, act) -> 
	    prod.getStore().getStorCatName().contains("素食")
	        ? (int)(prod.getProdPrice() * act.getActDiscValue())
	        : prod.getProdPrice()),

	FASTFOOD_DISCOUNT(List.of("速食優惠"), (prod, act) -> 
	    prod.getStore().getStorCatName().contains("速食")
	        ? (int)(prod.getProdPrice() * act.getActDiscValue())
	        : prod.getProdPrice()),

	   // 類型 3: 通用折扣
    MEMBER_DAY(List.of("會員日"), (prod, act) ->
        (int)(prod.getProdPrice() * act.getActDiscValue())
    ),

    LIMITED_OFFER(List.of("季節優惠"), (prod, act) ->
        (int)(prod.getProdPrice() * act.getActDiscValue())
    ),

    // 類型 4: 購物車邏輯（顯示原價）
    BUY_ONE_GET_ONE(List.of("限時優惠","買一送一"), (prod, act) ->
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
        for (ActCategoryEnum e : values()) {
            if (e.aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(input))) {
                return e;
            }
        }
       // System.out.println("⚠ 沒有對應的商品，無法解析活動類型：" + input);
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


