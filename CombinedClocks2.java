import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class CombinedClocks2 extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CombinedClocks2 clocks2 = new CombinedClocks2();
            clocks2.setVisible(true);
});
}
    private CombinedClocks2() {
        setTitle("Combined Clocks Application");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JFXPanel for JavaFX components
        JFXPanel jfxPanel = new JFXPanel();
        add(jfxPanel);

        // Set the layout as the content pane
        setContentPane(jfxPanel);

        // Schedule the clock update task every 1 minute
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateClockFromApi, 0, 1, TimeUnit.MINUTES);
    }
    public void updateClockFromApi() {
        try {
            // Uses the correct WorldTimeAPI endpoint for Sri Lanka (Asia/Colombo)
            String apiUrl = "https://worldtimeapi.org/api/timezone/Asia/Colombo";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Gson gson = new Gson();
                TestApiJsonParsing testApiJsonParsing = gson.fromJson(response.toString(), TestApiJsonParsing.class);

                // Calculate local time for Colombo based on Unix timestamp and UTC offset
                long unixTimestamp = testApiJsonParsing.getUnixtime();
                int utcOffsetSeconds = parseUtcOffset(testApiJsonParsing.getUtc_offset());

                LocalDateTime localDateTime = Instant.ofEpochSecond(unixTimestamp)
                        .atZone(ZoneId.of("UTC"))
                        .plusSeconds(utcOffsetSeconds)
                        .toLocalDateTime();

                // Update the JavaFX clock on the JavaFX Application Thread
                Platform.runLater(() -> {
                    ClockPanel clockPanel = createClockPanel(localDateTime.toLocalTime());
                    setContentPane(clockPanel);
                    revalidate();
});
}
        } catch (IOException e) {
            e.printStackTrace();
}
}

    private int parseUtcOffset(String utcOffset) {
        // Parse the UTC offset string and convert it to seconds
        String[] parts = utcOffset.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 3600 + minutes * 60;
}

    private ClockPanel createClockPanel(LocalTime localTime) {
        return new ClockPanel(localTime);
}

    static class ClockPanel extends JPanel {
        private LocalTime apiWorldTime;

        public ClockPanel(LocalTime apiWorldTime) {
            this.apiWorldTime = apiWorldTime;
}

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (apiWorldTime != null) {
                int width = getWidth();
                int height = getHeight();

                int centerX = width / 2;
                int centerY = height / 2;

                int radius = Math.min(width, height) / 2 - 10;

                g2.setColor(Color.WHITE);
                g2.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));

                // Draw clock numbers
                drawClockNumbers(g2, centerX, centerY, radius);

                // Draw hour hand
                int hour = apiWorldTime.getHour();
                drawHand(g2, centerX, centerY, hour % 12 * 30 + (int) (apiWorldTime.getMinute() * 0.5), radius * 0.5);

                // Draw minute hand
                int minute = apiWorldTime.getMinute();
                drawHand(g2, centerX, centerY, minute * 6, radius * 0.8);

                // Draw second hand
                int second = apiWorldTime.getSecond();
                drawHand(g2, centerX, centerY, second * 6, radius * 0.8);
}
}

        private void drawHand(Graphics2D g2, int centerX, int centerY, int angleDegrees, double length) {
            double angleRadians = Math.toRadians(-angleDegrees + 90);
            int x = (int) (centerX + length * Math.cos(angleRadians));
            int y = (int) (centerY - length * Math.sin(angleRadians));

            g2.drawLine(centerX, centerY, x, y);
}

        private void drawClockNumbers(Graphics2D g2, int centerX, int centerY, int radius) {
            Font font = new Font("Arial", Font.PLAIN, 16);
            g2.setFont(font);
            g2.setColor(Color.BLACK);

            for (int minute = 0; minute < 60; minute++) {
                double angleRadians = Math.toRadians(-minute * 6 + 90);
                int x1 = (int) (centerX + (radius - 15) * Math.cos(angleRadians));
                int y1 = (int) (centerY - (radius - 12) * Math.sin(angleRadians));

                int x2 = (int) (centerX + radius * Math.cos(angleRadians));
                int y2 = (int) (centerY - radius * Math.sin(angleRadians));

                if (minute % 5 == 0) {
                    // Draw hour lines
                    g2.drawLine(x1, y1, x2, y2);
                } else {
                    // Draw minute lines
                    g2.drawLine(x1, y1, x1 + 5, y1);
}
}

            for (int hour = 1; hour <= 12; hour++) {
                double angleRadians = Math.toRadians(-hour * 30 + 90);
                int x = (int) (centerX + (radius - 20) * Math.cos(angleRadians));
                int y = (int) (centerY - (radius - 20) * Math.sin(angleRadians));

                String number = Integer.toString(hour);
                FontMetrics metrics = g2.getFontMetrics(font);
                int numberWidth = metrics.stringWidth(number);
                int numberHeight = metrics.getHeight();

                g2.drawString(number, x - numberWidth / 2, y + numberHeight / 2);
}
}
}
}