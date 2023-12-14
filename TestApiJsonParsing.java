import com.google.gson.Gson;

public class TestApiJsonParsing {
    private long unixtime;
    private String utc_offset;

    // Add a constructor that takes parameters
    public TestApiJsonParsing(long unixtime, String utc_offset) {
        this.unixtime = unixtime;
        this.utc_offset = utc_offset;
    }

    // Add getters for unixtime and utc_offset
    public long getUnixtime() {
        return unixtime;
    }

    public String getUtc_offset() {
        return utc_offset;
    }

    public static void main(String[] args) {
        String jsonData = """
                {"unixtime":1702384544,"utc_offset":"+05:30"}
                """;

        Gson gson = new Gson();
        TestApiJsonParsing testApiJsonParsing = gson.fromJson(jsonData, TestApiJsonParsing.class);

        // Now you can use the 'testApiJsonParsing' object as needed
        System.out.println("Unixtime: " + testApiJsonParsing.getUnixtime());
        System.out.println("UTC Offset: " + testApiJsonParsing.getUtc_offset());
    }
}
