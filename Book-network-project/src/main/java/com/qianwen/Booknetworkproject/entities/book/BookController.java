package com.qianwen.Booknetworkproject.entities.book;

import com.qianwen.Booknetworkproject.common.PageResponse;
import com.qianwen.Booknetworkproject.entities.book.bookDTO.BookRequest;
import com.qianwen.Booknetworkproject.entities.book.bookDTO.BookResponse;
import com.qianwen.Booknetworkproject.entities.BorrowReturnHistory.BorrowedBookResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@Tag(name = "Book")
public class BookController {
    @Autowired
    private BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(@Valid @RequestBody BookRequest request, Authentication authenticationToken) {
        return ResponseEntity.ok(service.save(request, authenticationToken));
    }

    //find book by bookId
    @GetMapping("/{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId) {
        return ResponseEntity.ok(service.findById(bookId));
    }

    // find all displayable books except books from logged-in user
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllShareableBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authenticationToken
    ) {
        return ResponseEntity.ok(service.findAllBooks(page, size, authenticationToken));
    }

    //find logged-in user's books
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authenticationToken) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, authenticationToken));
    }

    //query all borrowed books for logged-in user
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findUserAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authenticationToken) {
        return ResponseEntity.ok(service.findUserBorrowedBooks(page, size, authenticationToken));
    }

    //find login-in user's returned books
    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findUserReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication authenticationToken
    ) {
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, authenticationToken));
    }
    //create a book transaction history
    @PostMapping("borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(@PathVariable("book-id") Integer bookId, Authentication authenticationToken) {
        return ResponseEntity.ok(service.borrowBook(bookId, authenticationToken));
    }

    @PatchMapping("borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowedBook(@PathVariable("book-id") Integer bookId, Authentication authenticationToken) {
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, authenticationToken));
    }
    //book owner can approve returned book
    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(@PathVariable("book-id") Integer bookId, Authentication authenticationToken) {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, authenticationToken));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(@PathVariable("book-id") Integer bookId, Authentication authenticationToken) {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, authenticationToken));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(@PathVariable("book-id") Integer bookId, Authentication authenticationToken) {
        return ResponseEntity.ok(service.updateShareableStatus(bookId, authenticationToken));
    }
    //consumes -> request param type
    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            //@Parameter()
            //@RequestPart("file") MultipartFile file,
            @RequestParam("file") MultipartFile file,
            Authentication authenticationToken
    ) {
        service.uploadBookCoverPicture(file, authenticationToken, bookId);
        return ResponseEntity.accepted().build();
    }

}
