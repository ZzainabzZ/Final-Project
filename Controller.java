import com.google.gson.Gson;
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
        nameOfCity.setText("Colombo");

        initializeRadioButtons();
        startClockUpdateTimer();
    }

    private void initializeRadioButtons() {
        ToggleGroup toggleGroup = new ToggleGroup();
        twelveHourClock.setToggleGroup(toggleGroup);
        twentyFourHourClock.setToggleGroup(toggleGroup);

        String lastSelected = preferences.get("lastSelected", "twentyFourHourClock");
        if (lastSelected.equals("twelveHourClock")) {
            twelveHourClock.setSelected(true);
        } else {
            twentyFourHourClock.setSelected(true);
        }

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == twelveHourClock) {
                preferences.put("lastSelected", "twelveHourClock");
            } else {
                preferences.put("lastSelected", "twentyFourHourClock");
            }
        });
    }


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
                    drawAnalogClock(cityTime);



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

    private void drawAnalogClock(String time) {
        GraphicsContext gc = clock.getGraphicsContext2D();
        gc.clearRect(0, 0, clock.getWidth(), clock.getHeight());


        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
        int hours = localTime.getHour();
        int minutes = localTime.getMinute();
        int seconds = localTime.getSecond();


        double centerX = clock.getWidth() / 2;
        double centerY = clock.getHeight() / 2;
        double radius = Math.min(centerX, centerY) - 10;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);


        double hourAngle = Math.toRadians((hours % 12 + minutes / 60.0) * 30);
        drawClockHand(gc, centerX, centerY, radius * 0.5, hourAngle);


        double minuteAngle = Math.toRadians(minutes * 6);
        drawClockHand(gc, centerX, centerY, radius * 0.7, minuteAngle);


        double secondAngle = Math.toRadians(seconds * 6);
        drawClockHand(gc, centerX, centerY, radius * 0.9, secondAngle);
    }

    private void drawClockHand(GraphicsContext gc, double centerX, double centerY, double length, double angle) {
        double endX = centerX + length * Math.sin(angle);
        double endY = centerY - length * Math.cos(angle);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(centerX, centerY, endX, endY);
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