package com.qianwen.Booknetworkproject.entities.book;

import com.qianwen.Booknetworkproject.common.PageResponse;
import com.qianwen.Booknetworkproject.entities.book.bookDTO.BookMapper;
import com.qianwen.Booknetworkproject.entities.book.bookDTO.BookRequest;
import com.qianwen.Booknetworkproject.entities.book.bookDTO.BookResponse;
import com.qianwen.Booknetworkproject.entities.BorrowReturnHistory.BookTransactionHistory;
import com.qianwen.Booknetworkproject.entities.BorrowReturnHistory.BookTransactionHistoryRepository;
import com.qianwen.Booknetworkproject.entities.BorrowReturnHistory.BorrowedBookResponse;
import com.qianwen.Booknetworkproject.entities.book.file.FileStorageService;
import com.qianwen.Booknetworkproject.entities.user.User;
import com.qianwen.Booknetworkproject.exceptions.OperationNotPermittedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.qianwen.Booknetworkproject.entities.book.BookSpecification.withOwnerId;

@Service
@Slf4j
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookTransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication authenticationToken) {
        User user = ((User) authenticationToken.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(book -> bookMapper.toBookResponse(book))
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authenticationToken) {
        User user = ((User) authenticationToken.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllShareableBooks(pageable, user.getId());
        List<BookResponse> booksResponse = books.stream()
                .map(book -> bookMapper.toBookResponse(book))
                .toList();
        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication authenticationToken) {
        User user = ((User) authenticationToken.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getEmail()), pageable);
        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findUserBorrowedBooks(int page, int size, Authentication authenticationToken) {
        User user = ((User) authenticationToken.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());

        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication authenticationToken) {
        User user = ((User) authenticationToken.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer borrowBook(Integer bookId, Authentication authenticationToken) {
        //validation1: if book exists in repo
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        //validation2: if book is archived or not shareable
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }

        User user = ((User) authenticationToken.getPrincipal());
        //validation3: user cannot borrow its own books
        if (Objects.equals(book.getCreatedBy(), user.getEmail())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        //validation4: user cannot borrow same book twice
        BookTransactionHistory borrowedBookByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (borrowedBookByUser != null) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }
        //validation4: user cannot borrow book that is borrowed by others
        BookTransactionHistory isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowedByOtherUser != null) {
            throw new OperationNotPermittedException("The requested book is already borrowed by others!");
        }

        BookTransactionHistory bookTransactionHistory = new BookTransactionHistory();
        bookTransactionHistory.setUser(user);
        bookTransactionHistory.setBook(book);
        bookTransactionHistory.setReturned(false);
        bookTransactionHistory.setReturnApproved(false);

        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }

    public Integer returnBorrowedBook(Integer bookId, Authentication authenticationToken) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

        User user = ((User) authenticationToken.getPrincipal());

        if (Objects.equals(book.getCreatedBy(), user.getName())) {
            throw new OperationNotPermittedException("You cannot return your own book!");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication authenticationToken) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

        User user = ((User) authenticationToken.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(), authenticationToken.getName())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book that you own");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You cannot approve its return"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }


    public Integer updateArchivedStatus(Integer bookId, Authentication authenticationToken) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) authenticationToken.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(), user.getName())) {
            throw new OperationNotPermittedException("You cannot archive or non-archive others' books");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateShareableStatus(Integer bookId, Authentication authenticationToken) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) authenticationToken.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(), user.getName())) {
            throw new OperationNotPermittedException("You cannot update others' books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }


    public void uploadBookCoverPicture(MultipartFile file, Authentication authentication, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) authentication.getPrincipal());
        String profilePicture = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(profilePicture);
        bookRepository.save(book);
    }


}