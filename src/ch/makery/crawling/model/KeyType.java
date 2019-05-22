package ch.makery.crawling.model;

import ch.makery.crawling.model.Goods;

import java.util.ArrayList;

public class KeyType {
	private final static ArrayList<String> EXPRESSAGE = getExpressage();
	private final static ArrayList<String> PRIDE = getPride();
	private final static ArrayList<String> QUALITY	= getQuality();
	private final static ArrayList<String> PACKAGE = getPackage();
	private final static ArrayList<String> CLOTHES = getClothes();
	private final static ArrayList<String> WEAR = getWear();
	private final static ArrayList<String> SIZE = getSize();
	private final static ArrayList<String> TYPE = getType();
	private final static ArrayList<String> TASTE = getTaste();
	private final static ArrayList<String> FACADE = getFacade();

	private static ArrayList<String> getExpressage(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("快递");
		list.add("发货");
		list.add("物流");
		list.add("运送");
		list.add("速度");
		return list;
	}

	private static ArrayList<String> getPride(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("价格");
		list.add("便宜");
		list.add("性价比");
		list.add("物美价廉");
		return list;
	}

	private static ArrayList<String> getQuality(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("质量");
		return list;
	}

	private static ArrayList<String> getPackage(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("包装");
		return list;
	}

	private static ArrayList<String> getClothes(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("舒服");
		list.add("舒适");
		return list;
	}

	private static ArrayList<String> getWear(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("穿");
		list.add("帅气");
		list.add("漂亮");
		return list;
	}

	private static ArrayList<String> getSize(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("尺码");
		list.add("合身");
		list.add("合体");
		return list;
	}

	private static ArrayList<String> getType(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("款式");
		list.add("版型");
		list.add("样式");
		list.add("设计");
		return list;
	}

	private static ArrayList<String> getFacade(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("外观");
		list.add("风格");
		list.add("炫酷");
		list.add("工艺");
		return list;
	}

	private static ArrayList<String> getTaste(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("口感");
		list.add("味道");
		list.add("新鲜");
		list.add("美味");
		return list;
	}

	public static ArrayList<String> getKeyType(String s) {
		if (EXPRESSAGE.contains(s)) return EXPRESSAGE;
		if (FACADE.contains(s)) return FACADE;
		if (PACKAGE.contains(s)) return PACKAGE;
		if (PRIDE.contains(s)) return PRIDE;
		if (QUALITY.contains(s)) return QUALITY;
		if (SIZE.contains(s)) return SIZE;
		if (TASTE.contains(s)) return TASTE;
		if (TYPE.contains(s)) return TYPE;
		if (WEAR.contains(s)) return WEAR;
		return null;
	}

	public static long wordCounter(Goods good, String keyWord) {
		ArrayList<String> list = getKeyType(keyWord);
		if(list == null)
			return good.getComments().stream().filter(e -> e.contains(keyWord)).count();
		else return good.getComments().stream().filter(e -> {
			for(String s : list) {
				if(e.contains(s)) return true;
			}return false;
		}).count();
	}

	public static Boolean isSuit(String comment, String keyWord) {
		ArrayList<String> list = getKeyType(keyWord);
		if(list == null) return comment.contains(keyWord);
		else {
			for(String s : list) {
				if(comment.contains(s)) return true;
			}
			return false;
		}
	}

}