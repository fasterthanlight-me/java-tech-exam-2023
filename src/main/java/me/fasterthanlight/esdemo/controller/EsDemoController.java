package me.fasterthanlight.esdemo.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.fasterthanlight.esdemo.model.GenericDocument;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@ApiResponse(responseCode = "500", description = "Internal server error")
@RequestMapping("/api/demo/indices/{indexName}")
@AllArgsConstructor
public class EsDemoController {

    private static final String ID_FIELD_NAME = "_id";

    private ElasticsearchClient elasticsearchClient;
    private ElasticsearchOperations elasticsearchOperations;

    @ApiResponse(responseCode = "201", description = "Index successfully created")
    @ApiResponse(responseCode = "400", description = "Index already exists in the elasticsearch cluster")
    @SneakyThrows
    @PostMapping
    public ResponseEntity<Void> createIndex(@PathVariable String indexName) {
        if (isIndexExists(indexName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        CreateIndexRequest request = CreateIndexRequest.of(builder -> builder.index(indexName));
        CreateIndexResponse createIndexResponse = elasticsearchClient.indices().create(request);

        if (createIndexResponse.acknowledged()) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiResponse(responseCode = "201", description = "Document for the index successfully created")
    @ApiResponse(responseCode = "400", description = "Index does not exist in the elasticsearch cluster")
    @PostMapping("/documents")
    public ResponseEntity<String> createDocument(@PathVariable String indexName, @RequestBody Object data) {
        checkIndexExistence(indexName);

        GenericDocument<Object> document = new GenericDocument<>(data);
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setObject(document);

        elasticsearchOperations.index(indexQuery, IndexCoordinates.of(indexName));

        return ResponseEntity.status(HttpStatus.CREATED).body(document.getId());
    }

    @ApiResponse(responseCode = "200", description = "Document data returned by Id")
    @ApiResponse(responseCode = "400", description = "Document does not exist by specified id")
    @GetMapping("/documents/{documentId}")
    public GenericDocument getDocumentById(@PathVariable String indexName, @PathVariable String documentId) {
        //In case there is a need to avoid providing 'indexName' then we can use relation db for mapping documentId to index
        checkIndexExistence(indexName);

        Criteria criteria = new Criteria(ID_FIELD_NAME)
                .is(documentId);
        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<GenericDocument> products = elasticsearchOperations
                .search(searchQuery,
                        GenericDocument.class,
                        IndexCoordinates.of(indexName));

        return products.stream().findFirst().orElseThrow(() -> new ErrorResponseException(HttpStatus.BAD_REQUEST)).getContent();
    }

    @SneakyThrows
    private boolean isIndexExists(String indexName) {
        return elasticsearchClient.indices().exists(builder -> builder.index(indexName)).value();
    }

    private void checkIndexExistence(String indexName) {
        if (!isIndexExists(indexName)) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }
    }
}
