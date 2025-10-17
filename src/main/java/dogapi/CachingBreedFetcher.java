package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String breedKey = breed.toLowerCase();

        // Return cached value if available
        if (cache.containsKey(breedKey)) {
            return cache.get(breedKey);
        }

        // Not cached — make an underlying API call
        callsMade++;

        try {
            List<String> subBreeds = fetcher.getSubBreeds(breedKey);
            cache.put(breedKey, subBreeds);  // cache only successful results
            return subBreeds;
        } catch (BreedNotFoundException e) {
            // Do not cache failures
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}