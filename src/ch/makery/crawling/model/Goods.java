package ch.makery.crawling.model;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Goods {
    private final StringProperty itemId;
    private final StringProperty sellerId;
    private final StringProperty goodsPicture;
    private final StringProperty goodsName;
    private final StringProperty price;
    private final StringProperty commentsNumber;

    private List<String> keyWord = new ArrayList<>();
    private List<String> comments = new ArrayList<>();

    public List<String> getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(List<String> keyWord) {
        this.keyWord = keyWord;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Goods() {
        this(null, null, null, null, null, null);
    }

    /*
     * Constructor with some initial data
     *
     * @param goodsPicture
     * @param goodsName
     * @param price
     * @param commentsNumber
     * */
    public Goods(String itemId, String sellerId, String goodsPicture, String goodsName, String price, String commentsNumber) {
        this.itemId = new SimpleStringProperty(itemId);
        this.sellerId = new SimpleStringProperty(sellerId);
        this.goodsPicture = new SimpleStringProperty(goodsPicture);
        this.goodsName = new SimpleStringProperty(goodsName);
        this.price = new SimpleStringProperty(price);
        this.commentsNumber = new SimpleStringProperty(commentsNumber);
    }

    public String getItemId() {
        return itemId.get();
    }

    public StringProperty itemIdProperty() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId.set(itemId);
    }

    public String getSellerId() {
        return sellerId.get();
    }

    public StringProperty sellerIdProperty() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId.set(sellerId);
    }

    public String getGoodsPicture() {
        return goodsPicture.get();
    }

    public StringProperty goodsPictureProperty() {
        return goodsPicture;
    }

    public void setGoodsPicture(String goodsPicture) {
        this.goodsPicture.set(goodsPicture);
    }

    public String getGoodsName() {
        return goodsName.get();
    }

    public StringProperty goodsNameProperty() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName.set(goodsName);
    }

    public String getPrice() {
        return price.get();
    }

    public StringProperty priceProperty() {
        return price;
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public String getCommentsNumber() {
        return commentsNumber.get();
    }

    public StringProperty commentsNumberProperty() {
        return commentsNumber;
    }

    public void setCommentsNumber(String commentsNumber) {
        this.commentsNumber.set(commentsNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof Goods) {
            Goods test = (Goods) o;
            return itemId.get().equals(test.itemId.get()) && sellerId.get().equals(test.sellerId.get())
                    && goodsPicture.get().equals(test.goodsPicture.get()) && goodsName.get().equals(test.goodsName.get())
                    && price.get().equals(test.price.get()) && commentsNumber.get().equals(test.commentsNumber.get());
        }
        return false;
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(itemId.get());
        sb.append(sellerId.get());
        sb.append(goodsPicture.get());
        sb.append(goodsName.get());
        sb.append(price.get());
        sb.append(commentsNumber.get());
        char[] charArr = sb.toString().toCharArray();
        int hash = 0;
        for (char c : charArr)
            hash = 31 * hash + c;
        return hash;
    }
}
