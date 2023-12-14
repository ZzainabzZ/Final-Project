import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class Controller {

    @FXML
    private Text nameOfCity;

    @FXML
    private Canvas clock;

    @FXML
    private Label digitalClock;

    @FXML
    private Label differentTime;

    @FXML
    private RadioButton twelveHourClock;

    @FXML
    private RadioButton twentyFourHourClock;

    private Preferences preferences;

    private static final String API_URL = "http://worldtimeapi.org/api/timezone/Asia/Colombo";

    private String getColomboTime() throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());


            WorldTimeResponse worldTimeResponse = new Gson().fromJson(reader, WorldTimeResponse.class);



            String dateTime = worldTimeResponse.getDateTime();
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME);
            return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void initialize() {
        preferences = Preferences.userNodeForPackage(Controller.class);

        initializeRadioButtons();
        startClockUpdateTimer();
    }

    private void initializeRadioButtons() {}

    private void startClockUpdateTimer() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    String cityTime = getColomboTime();
                    displayCityTime(cityTime);

                    String localTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String difference = calculateTimeDifference(localTime, cityTime);
                    displayTimeDifference(difference);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.start();
    }

    private void displayCityTime(String cityTime) {
        String formattedTime;
        if (twelveHourClock.isSelected()) {
            formattedTime = format12HourTime(cityTime);
        } else {
            formattedTime = format24HourTime(cityTime);
        }
        digitalClock.setText("Time: " + formattedTime);
    }

    private String format12HourTime(String time) {
        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
        return localTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
    }

    private String format24HourTime(String time) {
        return time;
    }

    private void displayTimeDifference(String difference) {
        differentTime.setText("Time Difference: " + difference);
    }




    private String calculateTimeDifference(String localTime, String colomboTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");


        LocalTime local = LocalTime.parse(localTime, formatter);
        LocalTime colombo = LocalTime.parse(colomboTime, formatter);


        Duration duration = Duration.between(local, colombo);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}