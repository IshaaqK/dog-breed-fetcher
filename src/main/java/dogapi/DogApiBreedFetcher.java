package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new BreedNotFoundException("API request failed with status " + response.code());
            }

            JSONObject json = new JSONObject(response.body().string());
            String status = json.getString("status");

            if (!status.equals("success")) {
                throw new BreedNotFoundException("Breed not found: " + breed);
            }

            JSONArray subBreedArray = json.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>();

            for (int i = 0; i < subBreedArray.length(); i++) {
                subBreeds.add(subBreedArray.getString(i));
            }

            return subBreeds;
        } catch (IOException e) {
            throw new BreedNotFoundException("Error fetching data for breed: " + breed);
        }
    }
}
