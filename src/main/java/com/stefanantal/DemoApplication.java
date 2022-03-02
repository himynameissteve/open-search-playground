package com.stefanantal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stefanantal.dto.Ps5Game;
import com.stefanantal.service.OpenSearchService;
import java.io.File;
import java.io.IOException;
import java.util.List;
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

    ObjectMapper objectMapper = new ObjectMapper();

    try {
      List<Ps5Game> sampleData =
          objectMapper.readValue(new File("data/data.json"), new TypeReference<List<Ps5Game>>() {});
      // TODO: Replace this with a bulk operation as soon as the OpenSearch Library supports that.
      sampleData.forEach(ps5Game -> openSearchService.addDocumentToIndex(index, ps5Game));
    } catch (IOException e) {
      e.printStackTrace();
    }

    log.info("Finished importing test data into OpenSearch.");
  }
}
