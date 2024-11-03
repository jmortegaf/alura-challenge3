package com.alura.alura_challenge3.models;

import java.util.List;

public class QueryResponse {
    private Integer count;
    private String next;
    private String previous;
    private List<Book> results;

    public QueryResponse(){}

    public QueryResponse(QueryResponseData data){
        this.count=data.count();
        this.next= data.next();
        this.previous= data.previous();
//        this.results=data.results();
    }

}
