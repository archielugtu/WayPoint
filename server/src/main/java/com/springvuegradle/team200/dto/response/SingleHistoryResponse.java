package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserActivityHistory;

import java.time.Instant;

/**
 * SingleHistoryResponse
 */
public class SingleHistoryResponse {

    @JsonProperty("activity_id")
    private Long activityId;

    @JsonProperty("activity_name")
    private String activityName;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("editor")
    private String editor;

    @JsonProperty("editor_id")
    private Long editorId;

    @JsonProperty("action")
    private String action;

    public SingleHistoryResponse() {
        //left empty intentionally
    }

    public static SingleHistoryResponse of(UserActivityHistory activityHistory) {
        Activity activity = activityHistory.getActivity();
        SingleHistoryResponse singleHistoryResponse = new SingleHistoryResponse();
        if (activity != null) {
            singleHistoryResponse.setActivityId(activity.getId());
            singleHistoryResponse.setActivityName(activity.getActivityName());
        }

        User editor = activityHistory.getUser();
        if (editor != null) {
            singleHistoryResponse.setEditor(editor.getFirstName() + " " + editor.getLastName());
            singleHistoryResponse.setEditorId(editor.getId());
        }

        singleHistoryResponse.setTimestamp(activityHistory.getTimestamp());
        singleHistoryResponse.setAction(activityHistory.getAction().value());
        return singleHistoryResponse;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "SingleHistoryResponse{" +
                "activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", timestamp=" + timestamp +
                ", editor='" + editor + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }
}
