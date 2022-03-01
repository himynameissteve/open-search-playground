package com.stefanantal;

import com.stefanantal.dto.Ps5Game;
import com.stefanantal.dto.Release;
import com.stefanantal.service.OpenSearchService;
import java.time.LocalDate;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoApplication {

  private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

  private final OpenSearchService openSearchService;

  public DemoApplication(OpenSearchService openSearchService) {
    this.openSearchService = openSearchService;
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @PostConstruct
  void runAtStartup() {
    log.info("Start importing test data into OpenSearch.");

    String index = "ps5-games";
    openSearchService.createIndex(index);

    // Populate index with sample data
    Ps5Game first = new Ps5Game();
    first.setTitle("007 Racing");
    first.setDeveloper("Eutechnyx");
    first.setPublisher("EA Games");

    Release firstReleaseEurope = new Release();
    firstReleaseEurope.setRegion("Europe");
    firstReleaseEurope.setDate(LocalDate.of(2000, 12, 15));

    Release firstReleaseNorthAmerica = new Release();
    firstReleaseNorthAmerica.setRegion("North America");
    firstReleaseNorthAmerica.setDate(LocalDate.of(2000, 11, 20));

    Ps5Game second = new Ps5Game();
    second.setTitle("007: The World Is Not Enough");
    second.setDeveloper("Black Ops Entertainment");
    second.setPublisher("EA Games");

    Release secondReleaseEurope = new Release();
    secondReleaseEurope.setRegion("Europe");
    secondReleaseEurope.setDate(LocalDate.of(2000, 11, 17));

    Release secondReleaseNorthAmerica = new Release();
    secondReleaseNorthAmerica.setRegion("North America");
    secondReleaseNorthAmerica.setDate(LocalDate.of(2000, 11, 8));

    openSearchService.addDocumentToIndex(index, "1", first);
    openSearchService.addDocumentToIndex(index, "2", second);

    log.info("Finished importing test data into OpenSearch.");
  }
}
