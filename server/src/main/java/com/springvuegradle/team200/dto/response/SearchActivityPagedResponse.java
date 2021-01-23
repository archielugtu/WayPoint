package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchActivityPagedResponse {
    @JsonProperty("results")
    private List<SearchActivityResponse> results;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("totalElements")
    private int items;

    public SearchActivityPagedResponse(List<SearchActivityResponse> results, int totalPages) {
        this.results = results;
        this.totalPages = totalPages;
        this.items = results.size();
    }

    public List<SearchActivityResponse> getResults() {
        return results;
    }

    public void setResults(List<SearchActivityResponse> results) {
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
