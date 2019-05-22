package ch.makery.crawling.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.ictclas4j.bean.SegResult;
import org.ictclas4j.segment.SegTag;

public class segmenter extends Thread {
	//自动过滤一些类似“的”之类不会用到的词汇
	//用老师给的分词之后会有/加上词性的后缀，要删去
	private static final String 停用词 = "停用词.txt";
	//根据输入的路径，读取评论，当选中之后缓存到class中
	public static List<String> fileExcludeStopWord(String srcFile){
		List<String> word = new ArrayList<>();
		try {
			BufferedReader srcFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(srcFile))));

			String paragraph;
			for(; (paragraph = srcFileBr.readLine()) != null;) {
				word.add(paragraph);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return word;
	}

	//原本是对于分词进行分类统计，删除分词之后没有使用。参数分别为词和
	public static List<Map.Entry<String,Long>> getWord(ArrayList<String> words){
		Map<String, Long> word = words
				.stream()
				.collect(Collectors.groupingBy(e -> e,  Collectors.counting()));

		List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(word.entrySet());
		Collections.sort(list, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
		for(Entry<String, Long> e : list) {
			System.out.println(e.getValue() + " " + e.getKey());
		}
		return list;
	}


	//用户自定义的标签方法，获取包含该关键词的评论
	//可能会更改入参
	public static List<String> customerCount(List<String> word, String keyWord){
		List<String> list = word.stream().filter(e -> e.contains(keyWord)).collect(Collectors.toList());
		return list;
	}
}
