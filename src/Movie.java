import javafx.scene.image.Image;

public class Movie {

    private Image poster;
    private String description;
    private String title;

    public Movie(Image poster, String description, String title){
        this.poster = poster;
        this.description = description;
        this.title = title;
    }

    public Image getPoster(){
        return poster;
    }

    public String getDescription(){
        return description;
    }

    public String getTitle(){
        return title;
    }
}
