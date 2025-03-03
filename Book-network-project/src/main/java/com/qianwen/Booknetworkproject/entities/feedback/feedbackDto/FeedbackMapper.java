package com.qianwen.Booknetworkproject.entities.feedback.feedbackDto;

import com.qianwen.Booknetworkproject.entities.book.Book;
import com.qianwen.Booknetworkproject.entities.feedback.Feedback;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        Feedback feedback =new Feedback();
        feedback.setRate(request.getRate());
        feedback.setComment(request.getComment());
        Book book = new Book();
        book.setId(request.getBookId());
        feedback.setBook(book);
        return feedback;
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setRate(feedback.getRate());
        feedbackResponse.setComment(feedback.getComment());
        feedbackResponse.setOwnFeedback(Objects.equals(feedback.getCreatedBy(), id));
        return feedbackResponse;
    }
}