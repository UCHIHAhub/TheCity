package mobi.androapp.com.city.builder;

import java.io.Serializable;
import java.util.HashMap;

public class GenreThe implements Serializable {




    public HashMap<String, Object> getCatergories() {
        return catergories;
    }

    public void setCatergories(HashMap<String, Object> catergories) {
        this.catergories = catergories;
    }
    HashMap<String, Object> catergories ;
//    public GenreTheCity(ArrayList<FirebaseMoviesRef> movieArray, String genre) {
//        this.movieArray = movieArray;
//        this.genre = genre;
//    }


}
