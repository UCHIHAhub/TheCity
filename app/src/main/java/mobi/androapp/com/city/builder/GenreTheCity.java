package mobi.androapp.com.city.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GenreTheCity implements Serializable {

    public CatergoryDetails getCatergoryDetails() {
        return catergoryDetails;
    }

    public void setCatergoryDetails(CatergoryDetails catergoryDetails) {
        this.catergoryDetails = catergoryDetails;
    }



    public HashMap<String, Object> getCatergories() {
        return catergories;
    }

    public void setCatergories(HashMap<String, Object> catergories) {
        this.catergories = catergories;
    }
    CatergoryDetails catergoryDetails;
    HashMap<String, Object> catergories ;

    public ArrayList<GenreThe> getGenreThes() {
        return genreThes;
    }

    public void setGenreThes(ArrayList<GenreThe> genreThes) {
        this.genreThes = genreThes;
    }

    ArrayList<GenreThe> genreThes;
//    public GenreTheCity(ArrayList<FirebaseMoviesRef> movieArray, String genre) {
//        this.movieArray = movieArray;
//        this.genre = genre;
//    }


}
