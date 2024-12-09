import java.util.ArrayList;
import java.util.List;

public class User {

        public List<Movie> likedMovies = new ArrayList<>();
        public List<Movie> dislikedMovies = new ArrayList<>();

        User(){
        }

        public List<Movie> getLikedMovies() {
                return likedMovies;
        }

        public List<Movie> getDislikedMovies() {
                return dislikedMovies;
        }
}
