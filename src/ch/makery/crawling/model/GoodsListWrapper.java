package ch.makery.crawling.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "goods")
public class GoodsListWrapper {

    private List<Goods> chosenGoods;
    private Map<String,String> cookies;
    private List<String> levelOne;
    private List<String> levelTwo;
    private List<String> levelThree;

    @XmlElement(name = "chosenGood")
    public List<Goods> getChosenGoods() {
        return chosenGoods;
    }

    public void setChosenGoods(List<Goods> chosenGoods) {
        this.chosenGoods = chosenGoods;
    }

    @XmlElement(name = "cookies")
    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    @XmlElement(name = "levelOne")
    public List<String> getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(List<String> levelOne) {
        this.levelOne = levelOne;
    }

    @XmlElement(name = "levelTwo")
    public List<String> getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(List<String> levelTwo) {
        this.levelTwo = levelTwo;
    }

    @XmlElement(name = "levelThree")
    public List<String> getLevelThree() {
        return levelThree;
    }

    public void setLevelThree(List<String> levelThree) {
        this.levelThree = levelThree;
    }
}
