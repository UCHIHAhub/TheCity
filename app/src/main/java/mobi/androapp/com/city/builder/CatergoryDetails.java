package mobi.androapp.com.city.builder;

import java.io.Serializable;

public class CatergoryDetails implements Serializable {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    String slug;
    String title;
    String count;
    public CatergoryDetails(String id, String slug, String title,  String count) {
        this.id = id;
        this.slug = slug;
        this.title = title;

        this.count = count;
    }

}
