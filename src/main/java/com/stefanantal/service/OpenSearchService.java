package com.stefanantal.service;

import com.stefanantal.properties.OpenSearchAuthProperties;
import com.stefanantal.properties.OpenSearchConnectionProperties;
import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.base.RestClientTransport;
import org.opensearch.client.base.Transport;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._global.DeleteResponse;
import org.opensearch.client.opensearch._global.IndexRequest;
import org.opensearch.client.opensearch._global.IndexResponse;
import org.opensearch.client.opensearch.indices.CreateRequest;
import org.opensearch.client.opensearch.indices.CreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpenSearchService {

  private final Logger log = LoggerFactory.getLogger(OpenSearchService.class);

  private final OpenSearchAuthProperties openSearchAuthProperties;
  private final OpenSearchConnectionProperties openSearchConnectionProperties;
  private RestClient restClient;
  private OpenSearchClient client;

  public OpenSearchService(
      OpenSearchAuthProperties openSearchAuthProperties,
      OpenSearchConnectionProperties openSearchConnectionProperties) {
    this.openSearchAuthProperties = openSearchAuthProperties;
    this.openSearchConnectionProperties = openSearchConnectionProperties;
  }

  private void openConnection() {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(
            openSearchAuthProperties.getUser(), openSearchAuthProperties.getPassword()));

    restClient =
        RestClient.builder(
                new HttpHost(
                    openSearchConnectionProperties.getHost(),
                    openSearchConnectionProperties.getPort(),
                    openSearchConnectionProperties.getScheme()))
            .setHttpClientConfigCallback(
                httpAsyncClientBuilder ->
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
            .build();

    Transport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
    client = new OpenSearchClient(transport);
  }

  private void closeConnection() {
    if (restClient != null) {
      try {
        restClient.close();
      } catch (IOException e) {
        log.error("There was an error during an attempt to close a connection.");
        e.printStackTrace();
      }
    }
  }

  /**
   * Create an OpenSearch index with a given name. The OpenSearch target instance will be taken from
   * environment or application properties.
   *
   * @param indexName to be given.
   * @return true if operation was successful.
   */
  public boolean createIndex(String indexName) {
    openConnection();

    CreateRequest createIndexRequest = new CreateRequest.Builder().index(indexName).build();
    try {
      CreateResponse response = client.indices().create(createIndexRequest);
    } catch (IOException e) {
      log.error(
          String.format("There was an error during an attempt to create the %s index.", indexName));
      e.printStackTrace();
    }

    closeConnection();

    // TODO: Figure out what values response can have and react accordingly.
    return true;
  }

  /**
   * Deletes a document from an index.
   *
   * @param indexName to be deleted from.
   * @param id of the document to be deleted.
   * @return true if operation was successful.
   */
  public boolean deleteDocumentFromIndex(String indexName, String id) {
    openConnection();

    if (indexName == null || indexName.isBlank()) {
      throw new IllegalArgumentException("An index name must be given for a delete operation.");
    }

    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException("An document id must be given to delete it.");
    }

    try {
      DeleteResponse response = client.delete(builder -> builder.index(indexName).id(id));
    } catch (IOException e) {
      log.error(
          String.format(
              "There was an error during an attempt to delete a document with the id %s from the index %s.",
              id, indexName));
      e.printStackTrace();
    }

    closeConnection();

    // TODO: Figure out what values response can have and react accordingly.
    return true;
  }

  /**
   * Adds a document to an index.
   *
   * @param indexName to be added to.
   * @param id of the document to be given. Must not contain only whitespace characters or be empty.
   * @param document to be added to the index.
   * @param <T> type of the document.
   * @return true if operation was successful.
   */
  public <T> boolean addDocumentToIndex(String indexName, String id, T document) {
    return addToIndex(indexName, id, document);
  }

  /**
   * Adds a document to an index.
   *
   * @param indexName to be added to.
   * @param document to be added to the index.
   * @param <T> type of the document.
   * @return true if operation was successful.
   */
  public <T> boolean addDocumentToIndex(String indexName, T document) {
    return addToIndex(indexName, null, document);
  }

  /**
   * Adds a document to an index.
   *
   * @param indexName to be added to.
   * @param id of the document to be given. If null, empty or only whitespaces, an id will be
   *     generated in OpenSearch.
   * @param document to be added to the index.
   * @param <T> type of the document.
   * @return true if operation was successful.
   */
  private <T> boolean addToIndex(String indexName, String id, T document) {
    openConnection();

    IndexRequest<T> indexRequest;

    if (id == null || id.isBlank()) {
      indexRequest = new IndexRequest.Builder<T>().index(indexName).value(document).build();
    } else {
      indexRequest = new IndexRequest.Builder<T>().index(indexName).id(id).value(document).build();
    }

    try {
      IndexResponse response = client.index(indexRequest);
    } catch (IOException e) {
      log.error(
          String.format(
              "There was an error during an attempt to add a document with the id %s to the index %s.",
              id, indexName));
      e.printStackTrace();
    }

    closeConnection();

    // TODO: Figure out what values response can have and react accordingly.
    return true;
  }
}
