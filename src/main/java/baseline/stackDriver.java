package baseline;


// [START monitoring_quickstart]
    import com.google.api.Metric;
    import com.google.api.MonitoredResource;

// Imports the Google Cloud client library
    import com.google.cloud.monitoring.spi.v3.MetricServiceClient;

    import com.google.monitoring.v3.CreateTimeSeriesRequest;
    import com.google.monitoring.v3.Point;
    import com.google.monitoring.v3.ProjectName;
    import com.google.monitoring.v3.TimeInterval;
    import com.google.monitoring.v3.TimeSeries;
    import com.google.monitoring.v3.TypedValue;
    import com.google.protobuf.util.Timestamps;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

public class stackDriver {
  public static void main(String... args) throws Exception {
    // Your Google Cloud Platform project ID
    String projectId = System.getProperty("projectId");

    if (projectId == null) {
      System.err.println("Usage: QuickstartSample -DprojectId=YOUR_PROJECT_ID");
      return;
    }

    // Instantiates a client
    MetricServiceClient metricServiceClient = MetricServiceClient.create();

    // Prepares an individual data point
    TimeInterval interval = TimeInterval.newBuilder()
        .setEndTime(Timestamps.fromMillis(System.currentTimeMillis()))
        .build();
    TypedValue value = TypedValue.newBuilder()
        .setDoubleValue(123.45)
        .build();
    Point point = Point.newBuilder()
        .setInterval(interval)
        .setValue(value)
        .build();

    List<Point> pointList = new ArrayList<>();
    pointList.add(point);

    ProjectName name = ProjectName.create(projectId);

    // Prepares the metric descriptor
    Map<String, String> metricLabels = new HashMap<String, String>();
    metricLabels.put("store_id", "Pittsburg");
    Metric metric = Metric.newBuilder()
        .setType("custom.googleapis.com/stores/daily_sales")
        .putAllLabels(metricLabels)
        .build();

    // Prepares the monitored resource descriptor
    Map<String, String> resourceLabels = new HashMap<String, String>();
    resourceLabels.put("project_id", projectId);
    MonitoredResource resource = MonitoredResource.newBuilder()
        .setType("global")
        .putAllLabels(resourceLabels)
        .build();

    // Prepares the time series request
    TimeSeries timeSeries = TimeSeries.newBuilder()
        .setMetric(metric)
        .setResource(resource)
        .addAllPoints(pointList)
        .build();
    List<TimeSeries> timeSeriesList = new ArrayList<>();
    timeSeriesList.add(timeSeries);

    CreateTimeSeriesRequest request = CreateTimeSeriesRequest.newBuilder()
        .setNameWithProjectName(name)
        .addAllTimeSeries(timeSeriesList)
        .build();

    // Writes time series data
    metricServiceClient.createTimeSeries(request);

    System.out.printf("Done writing time series data.%n");

    metricServiceClient.close();
  }
}
// [END monitoring_quickstart]

