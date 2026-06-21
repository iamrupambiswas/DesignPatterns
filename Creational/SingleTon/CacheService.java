public class CacheService {
    private CacheService() {
        System.out.println("Cache service connected!");
    }

    private static final CacheService INSTANCE = new CacheService();

    public static CacheService getInstance() {
        return INSTANCE;
    }

    public <T> void cacheIt(T value) {
        System.out.println("Cached value: " + value);
    }
}
