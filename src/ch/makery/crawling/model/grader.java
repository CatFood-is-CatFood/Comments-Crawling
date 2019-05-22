package ch.makery.crawling.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.makery.crawling.MainApp;

public class grader {
	static final String[] adverb = {"差","很差","不行","有问题","不对劲","垃圾","一般","差评","讨厌","TM","草","滚","艹","不相符"};

	public static Boolean isBad (String comment) {
		Boolean isBad = false;
		for(String s : adverb) {
			if (comment.contains(s)) isBad = true;
		}
		return isBad;
	}

	public static double gradeCount(Goods good, MainApp mainApp) {
		double grade = 80;
		double number = 0;
		if(!good.getCommentsNumber().equals("-1"))
			number = Double.parseDouble(good.getCommentsNumber());
		if(number <= 500) grade += number / 100;
		else grade += 5;

		Long badComment = good.getComments().stream().filter(grader::isBad).count();
		grade -= badComment / 10;

		for(int i = 0; i < mainApp.getLevelOne().size(); i++) {
			Long l = KeyType.wordCounter(good, mainApp.getLevelOne().get(i));
			grade += l / 10;
		}
		for(int i = 0; i < mainApp.getLevelTwo().size(); i++) {
			Long l = KeyType.wordCounter(good, mainApp.getLevelOne().get(i));
			grade += l / 20;
		}
		for(int i = 0; i < mainApp.getLevelThree().size(); i++) {
			Long l = KeyType.wordCounter(good, mainApp.getLevelOne().get(i));
			grade += l / 30;
		}
		//System.out.println(grade);
		return grade;
	}

	//统计差评数量
	public static Long badCount(Goods good) {
		Long l = good.getComments().stream().filter(e -> grader.isBad(e)).count();
		return l;
	}

}
