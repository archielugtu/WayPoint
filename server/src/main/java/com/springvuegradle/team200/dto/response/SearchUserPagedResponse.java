package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchUserPagedResponse {

    @JsonProperty("results")
    private List<SearchUserResponse> results;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("totalElements")
    private int items;

    public SearchUserPagedResponse(List<SearchUserResponse> results, int totalPages) {
        this.results = results;
        this.totalPages = totalPages;
        this.items = results.size();
    }

    public List<SearchUserResponse> getResults() {
        return results;
    }

    public void setResults(List<SearchUserResponse> results) {
        this.results = results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }
}

