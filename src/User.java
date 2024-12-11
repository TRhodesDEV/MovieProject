import java.util.ArrayList;
import java.util.List;

public class User {

        public String username;
        public List<Movie> likedMovies;

        public User(String username) {
                this.username = username;
                this.likedMovies = new ArrayList<>();
        }

        public String getUsername() {
                return username;
        }

        public List<Movie> getLikedMovies() {
                return likedMovies;
        }

        public void addLikedMovie(Movie movie) {
                likedMovies.add(movie);
        }


}
