import org.assertj.core.api.AbstractAssert;
import org.json.JSONArray;

import java.util.Map;

public class JSONArrayAssert extends AbstractAssert<JSONArrayAssert, JSONArray> {

    public JSONArrayAssert(JSONArray actual) {
        super(actual, JSONArrayAssert.class);
    }

    public JSONArrayAssert containsPetNamed(String petName) {
        isNotNull();
        boolean containsPet = actual.toList().stream().map(Map.class::cast).anyMatch(pet 
            -> pet.get("name").equals(petName));

        if (!containsPet) {
            failWithMessage("Expected pet named \"%s\"", petName);
        }
        return this;
    }

}
