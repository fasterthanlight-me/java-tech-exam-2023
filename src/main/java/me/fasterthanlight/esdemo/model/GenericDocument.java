package me.fasterthanlight.esdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "dynamic-index", createIndex = false)
public class GenericDocument<T> {

    @Id
    private String id;

    private T data;

    public GenericDocument(T data) {
        this.data = data;
    }
}
