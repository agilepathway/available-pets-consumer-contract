import org.json.JSONArray;

public class Assertions {
    public static JSONArrayAssert assertThat(JSONArray actual) {
        return new JSONArrayAssert(actual);
    }

    // static factory methods of other assertion classes
}
