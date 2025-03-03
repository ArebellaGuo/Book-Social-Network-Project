package com.qianwen.Booknetworkproject.entities.book.bookDTO;

import com.qianwen.Booknetworkproject.entities.book.Book;
import com.qianwen.Booknetworkproject.entities.BorrowReturnHistory.BorrowedBookResponse;
import com.qianwen.Booknetworkproject.entities.BorrowReturnHistory.BookTransactionHistory;
import com.qianwen.Booknetworkproject.entities.book.file.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    @Autowired
    private FileStorageService fileStorageService;

    public Book toBook(BookRequest request) {
        Book book = new Book();
        book.setId(request.getId());
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setAuthorName(request.getAuthorName());
        book.setSynopsis(request.getSynopsis());
        book.setArchived(false);
        book.setShareable(request.isShareable());
        return book;
    }

    public BookResponse toBookResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setIsbn(book.getIsbn());
        bookResponse.setAuthorName(book.getAuthorName());
        bookResponse.setSynopsis(book.getSynopsis());
        bookResponse.setArchived(book.isArchived());
        bookResponse.setShareable(book.isShareable());
        bookResponse.setOwner(book.getOwner().fullName());
        bookResponse.setRate(book.getRate());
        bookResponse.setCover(FileStorageService.readFileFromLocation(book.getBookCover()));
        return bookResponse;
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        BorrowedBookResponse borrowedBookResponse = new BorrowedBookResponse();
        borrowedBookResponse.setId(history.getBook().getId());
        borrowedBookResponse.setTitle(history.getBook().getTitle());
        borrowedBookResponse.setAuthorName(history.getBook().getAuthorName());
        borrowedBookResponse.setIsbn(history.getBook().getIsbn());
        borrowedBookResponse.setRate(history.getBook().getRate());
        borrowedBookResponse.setReturned(history.isReturned());
        borrowedBookResponse.setReturnApproved(history.isReturnApproved());
        return borrowedBookResponse;
    }
}