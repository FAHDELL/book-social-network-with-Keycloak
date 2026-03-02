package net.fahd.book_network.book;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.fahd.book_network.commun.PageResponse;
import net.fahd.book_network.exception.OperationNotPermittedException;
import net.fahd.book_network.file.FileStorageService;
import net.fahd.book_network.history.BookTransactionHistory;
import net.fahd.book_network.history.BookTransactionHistoryRepository;
import net.fahd.book_network.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static net.fahd.book_network.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final BookTransactionHistoryRepository transactionHistoryRepository;

    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectUser) {

        //User user = (User) connectUser.getPrincipal();
        Book book = bookMapper.tobook(request);
        //book.setOwner(user);
            return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectUser) {
        //User user = (User) connectUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, connectUser.getName());
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

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectUser) {
        //User user = (User) connectUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(connectUser.getName()), pageable);
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

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectUser) {
        //User user = ((User) connectUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, connectUser.getName());
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

    public PageResponse<BorrowedBookResponse> findAllReturnBooks(int page, int size, Authentication connectUser) {
        //User user = ((User) connectUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBReturnBooks(pageable, connectUser.getName());
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

    public Integer updateShareableStatus(Integer bookId, Authentication connectUser)  {
         Book book = bookRepository.findById(bookId)
                 .orElseThrow(() -> new EntityNotFoundException("Book with id: " + bookId + " not found."));
         //User user = (User) connectUser.getPrincipal();
         if(!Objects.equals(book.getCreatedBy(), connectUser.getName())){
             try {
                 throw new OperationNotPermittedException("you cannot update books shareable status");
             } catch (OperationNotPermittedException e) {
                 throw new RuntimeException(e);
             }
         }
         book.setShareable(!book.isShareable());
         bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectUser)  {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id: " + bookId + " not found."));
        //User user = (User) connectUser.getPrincipal();
        if(!Objects.equals(book.getCreatedBy(), connectUser.getName())){
            try {
                throw new OperationNotPermittedException("you cannot update books archived status");
            } catch (OperationNotPermittedException e) {
                throw new RuntimeException(e);
            }
        }
        book.setShareable(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectUser)  {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id: " + bookId + " not found."));

        if(book.isArchived() || !book.isShareable()){
            try {
                throw new OperationNotPermittedException("The requested book cannot b borrowed since it is archived or not shareable");
            } catch (OperationNotPermittedException e) {
                throw new RuntimeException(e);
            }
        }
        //User user = (User) connectUser.getPrincipal();
        if(Objects.equals(book.getCreatedBy(), connectUser.getName() )){
            try {
                throw new OperationNotPermittedException("You cannot borrow your own book");
            } catch (OperationNotPermittedException e) {
                throw new RuntimeException(e);
            }
        }

        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, connectUser.getName()) ;

        if(isAlreadyBorrowed){
            try {
                throw new OperationNotPermittedException("The requested book is already borrowed");
            } catch (OperationNotPermittedException e) {
                throw new RuntimeException(e);
            }
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .userId(connectUser.getName())
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

//    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser)  {
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
//        if (book.isArchived() || !book.isShareable()) {
//            try {
//                throw new OperationNotPermittedException("The requested book is archived or not shareable");
//            } catch (OperationNotPermittedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        User user = ((User) connectedUser.getPrincipal());
//        if (Objects.equals(book.getCreatedBy(), user.getName())) {
//            try {
//                throw new OperationNotPermittedException("You cannot borrow or return your own book");
//            } catch (OperationNotPermittedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        BookTransactionHistory bookTransactionHistory = null;
//        try {
//            bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
//                    .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
//        } catch (OperationNotPermittedException e) {
//            throw new RuntimeException(e);
//        }
//
//        bookTransactionHistory.setReturned(true);
//        return transactionHistoryRepository.save(bookTransactionHistory).getId();
//    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser)
            throws OperationNotPermittedException {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new EntityNotFoundException("No book found with ID:: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                    "The requested book is archived or not shareable"
            );
        }

        //User user = (User) connectedUser.getPrincipal();

        if (Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
            throw new OperationNotPermittedException(
                    "You cannot borrow or return your own book"
            );
        }

        BookTransactionHistory bookTransactionHistory =
                transactionHistoryRepository
                        .findByBookIdAndUserId(bookId, connectedUser.getName())
                        .orElseThrow(() ->
                                new OperationNotPermittedException(
                                        "You did not borrow this book"
                                )
                        );

        bookTransactionHistory.setReturned(true);

        return transactionHistoryRepository
                .save(bookTransactionHistory)
                .getId();
    }


    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser)  {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            try {
                throw new OperationNotPermittedException("The requested book is archived or not shareable");
            } catch (OperationNotPermittedException e) {
                throw new RuntimeException(e);
            }
        }
         //User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getCreatedBy(), connectedUser.getName())) {
//            System.out.println("book CreatedBy() " + book.getCreatedBy());
//            System.out.println();
//            System.out.println("connectedUser " + connectedUser.getName());
            try {
                throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
            } catch (OperationNotPermittedException e) {
                throw new RuntimeException(e);
            }
        }

        BookTransactionHistory bookTransactionHistory = null;
        try {
            bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, connectedUser.getName())
                    .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));
        } catch (OperationNotPermittedException e) {
            throw new RuntimeException(e);
        }

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        //User user = ((User) connectedUser.getPrincipal());
        var profilePicture = fileStorageService.saveFile(file, connectedUser.getName());
        book.setBookCover(profilePicture);
        bookRepository.save(book);

    }
}
