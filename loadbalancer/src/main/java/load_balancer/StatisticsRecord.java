package load_balancer;


public class StatisticsRecord {
    String app_id;
    int requests_per_second;

    public StatisticsRecord(String appId, int requestsPerSecond) {
        this.app_id = appId;
        this.requests_per_second = requestsPerSecond;
    }
}
