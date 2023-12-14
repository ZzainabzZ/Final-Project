import com.google.gson.Gson;

public class TestApiJsonParsing {
    public static void main(String[] args) {
        String jsonData = """
                    {"abbreviation":"+0530",
                    "client_ip":"1.253.54.62",
                    "datetime":"2023-12-12T18:05:44.222633+05:30",
                    "day_of_week":2,
                    "day_of_year":346,
                    "dst":false,
                    "dst_from":null,
                    dst_offset":0,
                    "dst_until":null,
                    "raw_offset":19800,
                    "timezone":"Asia/Colombo",
                    "unixtime":1702384544,
                    "utc_datetime":
                    "2023-12-12T12:35:44.222633+00:00",
                    "utc_offset":"+05:30",
                    "week_number":50}
                    """;
        Gson gson = new Gson();
        WorldTime worldTime = gson.fromJson(jsonData, WorldTime.class);
   }
}