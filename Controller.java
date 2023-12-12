import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;

public class Controller {

    @FXML
    private Text nameOfCity;

    @FXML
    private Canvas clock;

    @FXML
    private RadioButton twelveHourClock;

    @FXML
    private RadioButton twentyFourHourClock;

    @FXML
    private Label digitalClock;

    @FXML
    private Label differentTime;

    @FXML
    private Label lastedUpdate;

    private static final String API_URL = "http://worldtimeapi.org/api/timezone/Asia/Colombo";

}