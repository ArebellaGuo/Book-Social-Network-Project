package com.qianwen.Booknetworkproject.entities.feedback;

import com.qianwen.Booknetworkproject.common.PageResponse;
import com.qianwen.Booknetworkproject.entities.book.Book;
import com.qianwen.Booknetworkproject.entities.book.BookRepository;
import com.qianwen.Booknetworkproject.entities.feedback.feedbackDto.FeedbackMapper;
import com.qianwen.Booknetworkproject.entities.feedback.feedbackDto.FeedbackRequest;
import com.qianwen.Booknetworkproject.entities.feedback.feedbackDto.FeedbackResponse;
import com.qianwen.Booknetworkproject.entities.user.User;
import com.qianwen.Booknetworkproject.exceptions.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FeedbackService {
    @Autowired
    private  FeedBackRepository feedBackRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FeedbackMapper feedbackMapper;

    public Integer save(FeedbackRequest request, Authentication authentication) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.getBookId()));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
        }

        User user = ((User) authentication.getPrincipal());
        if (Objects.equals(book.getCreatedBy(), user.getName())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }

        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedBackRepository.save(feedback).getId();
    }

    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }
}
