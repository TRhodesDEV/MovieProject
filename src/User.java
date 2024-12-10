import java.util.ArrayList;
import java.util.List;

public class User {

        public String username;
        public List<Movie> likedMovies;
        public List<Movie> dislikedMovies;

        public User(String username){
                this.username = username;
                this.likedMovies = new ArrayList<>();
                this.dislikedMovies = new ArrayList<>();
        }

        public String getUsername(){
                return username;
        }

        public List<Movie> getLikedMovies() {
                return likedMovies;
        }

        public List<Movie> getDislikedMovies() {
                return dislikedMovies;
        }

        public void addLikedMovie(Movie movie){
                likedMovies.add(movie);
        }

        public void addDislikedMovie(Movie movie){
                dislikedMovies.add(movie);
        }
}
