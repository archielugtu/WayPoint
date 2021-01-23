package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

public class ActivityHistoryResponse {

    @JsonProperty("activity_history")
    private List<SingleHistoryResponse> activityHistory;
    @JsonProperty("total_elements")
    private int totalElements;
    @JsonProperty("total_pages")
    private int totalPages;

    /**
     * Constructor
     */
    public ActivityHistoryResponse() {
        //left empty intentionally
    }

    public static ActivityHistoryResponse of(Page<SingleHistoryResponse> source) {
        ActivityHistoryResponse response = new ActivityHistoryResponse();
        response.setActivityHistory(source.getContent());
        response.setTotalPages(source.getTotalPages());
        response.setTotalElements(source.getNumberOfElements());
        return response;
    }

    public void setActivityHistory(List<SingleHistoryResponse> activityHistory) {
        this.activityHistory = activityHistory;
    }

    public List<SingleHistoryResponse> getActivityHistory() {
        return activityHistory;
    }
    
    public int getTotalElements() {
        return totalElements;
    }
     
    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
     
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
