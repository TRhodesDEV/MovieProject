import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwipeApp extends Application {

    private static final String API_KEY = System.getenv("TMDB_API_KEY");
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private List<VBox> movieCards = new ArrayList<>();
    private Map<VBox, Movie> cardToMovieMap = new HashMap<>();

    User user1 = new User("Travis");
    User user2 = new User("Abby");

    User currentUser = user1;

    @Override
    public void start(Stage stage) {
        // Create a StackPane
        StackPane cardPane = new StackPane();
        cardPane.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20;");

        // Load movies
        List<Movie> movies = fetchMovies();
        for (Movie movie : movies) {
            VBox card = createCard(movie);
            card.setOpacity(0); //Hide cards as they are loaded
            movieCards.add(card);
            cardPane.getChildren().add(card); // Add cards to the StackPane
        }

        //Show top card
        if(!movieCards.isEmpty()){
            movieCards.get(movieCards.size() - 1).setOpacity(1); // Unhide top card
        }

        // Create Buttons
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        Button showLikedMoviesButton = new Button("Show Matches");

        // Set button events
        yesButton.setOnAction(event -> swipeCard(cardPane, 400)); // Click Yes
        noButton.setOnAction(event -> swipeCard(cardPane, -400)); // Click No
        showLikedMoviesButton.setOnAction(event -> showLikedMovies());

        //Arrange buttons
        HBox buttonBox = new HBox(10, yesButton, noButton, showLikedMoviesButton);
        buttonBox.setStyle("-fx-alignment: center;");
        VBox layout = new VBox(10, cardPane, buttonBox);
        layout.setStyle("-fx-alignment: center; -fx-spacing: 10;");

        // Create the scene and show the stage
        Scene scene = new Scene(layout, 500, 700);
        stage.setScene(scene);
        stage.setTitle("Movie Cards");
        stage.show();
    }

    //Create movie card for sliding

    private VBox createCard(Movie movie) {

        ImageView posterView  = new ImageView(movie.getPoster());
        posterView.setFitWidth(300);
        posterView.setFitHeight(400);
        posterView.setPreserveRatio(true);

        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 5;");

        Label descriptionLabel = new Label(movie.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        VBox card = new VBox(10, posterView, titleLabel, descriptionLabel);
        card.setStyle("-fx-alignment: center;");

        cardToMovieMap.put(card, movie);

        return card;
    }

    private void swipeCard(StackPane cardPane, double endX) {
        if (!cardPane.getChildren().isEmpty()) {
            VBox topCard = (VBox) cardPane.getChildren().get(cardPane.getChildren().size() - 1);

            int index = cardPane.getChildren().size() - 1;
            Movie currentMovie = cardToMovieMap.get(topCard);

            if(endX > 0){
                user1.likedMovies.add(currentMovie);
            } else {
                user1.dislikedMovies.add(currentMovie);
            }

            TranslateTransition transition = new TranslateTransition(Duration.millis(300), topCard);
            transition.setToX(endX); // Move the card left or right


            transition.setOnFinished(e -> {

                cardPane.getChildren().remove(topCard); // Remove the card after animation
                movieCards.remove(topCard);

                if(!cardPane.getChildren().isEmpty()){
                    VBox nextCard = (VBox) cardPane.getChildren().get(cardPane.getChildren().size() - 1);
                    nextCard.setOpacity(1); //make next card visible
                }
            });

            transition.play();
        }
    }

    private List<Movie> fetchMovies() {
        List<Movie> movies = new ArrayList<>();
        try {
            // Build API URL
            String urlString = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
            URL url = new URL(urlString);

            // Connect and fetch JSON data
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            // Parse JSON response
            StringBuilder response = new StringBuilder();
            int read;
            while ((read = reader.read()) != -1) {
                response.append((char) read);
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");

            // Extract poster paths and description
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                String posterPath = movie.getString("poster_path");
                String overview = movie.getString("overview");
                String title = movie.getString("title");
                String imageUrl = BASE_IMAGE_URL + posterPath;

                Image posterImage = new Image(imageUrl, true);
                movies.add(new Movie(posterImage, overview, title));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    private void showLikedMovies() {

        // Create a new window for showing liked movies
        Stage likedMoviesStage = new Stage();
        likedMoviesStage.setTitle("Liked Movies");

        // Create a layout that lists liked movies
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        // Label for every liked movie
        for(Movie movie : user1.likedMovies) {
            Label movieLabel = new Label(movie.getTitle());
            movieLabel.setWrapText(true);
            movieLabel.setStyle("-fx-font-size: 14px;");
            layout.getChildren().add(movieLabel);
        }

        // No movies liked case
        if(user1.likedMovies.isEmpty()) {
            layout.getChildren().add(new Label("You didn't like any movies!"));
        }

        // Add scrolling pane so user can see selections better
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        // Set scene and show stage
        Scene scene = new Scene(scrollPane, 400, 300);
        likedMoviesStage.setScene(scene);
        likedMoviesStage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}

