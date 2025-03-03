package com.qianwen.Booknetworkproject.entities.feedback.feedbackDto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

    private Double rate;
    private String comment;
    private boolean ownFeedback;

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isOwnFeedback() {
        return ownFeedback;
    }

    public void setOwnFeedback(boolean ownFeedback) {
        this.ownFeedback = ownFeedback;
    }
}