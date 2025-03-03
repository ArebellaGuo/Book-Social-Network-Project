package com.qianwen.Booknetworkproject.entities.feedback;

import com.qianwen.Booknetworkproject.common.PageResponse;
import com.qianwen.Booknetworkproject.entities.feedback.feedbackDto.FeedbackRequest;
import com.qianwen.Booknetworkproject.entities.feedback.feedbackDto.FeedbackResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@Tag(name = "Feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService service;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(@Valid @RequestBody FeedbackRequest request, Authentication authenticationToken) {
        return ResponseEntity.ok(service.save(request, authenticationToken));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authenticationToken)
    {
        return ResponseEntity.ok(service.findAllFeedbacksByBook(bookId, page, size, authenticationToken));
    }
}