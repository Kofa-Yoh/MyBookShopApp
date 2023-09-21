package com.example.MyBookShopApp.books.assessments;

import com.example.MyBookShopApp.books.books.Book;
import com.example.MyBookShopApp.security.BookStoreUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookAssessmentService {

    private final BookAssessmentRepository bookAssessmentRepository;

    public BookAssessmentService(BookAssessmentRepository bookAssessmentRepository) {
        this.bookAssessmentRepository = bookAssessmentRepository;
    }

    public void changeBookUserAssessment(BookStoreUser user, Book book, Byte newRate) {
        BookAssessment bookAssessment = bookAssessmentRepository.findBookAssessmentByUserAndBook(user, book);
        if (bookAssessment == null) {
            BookAssessment newBookAssessment = new BookAssessment();
            newBookAssessment.setBook(book);
            newBookAssessment.setUser(user);
            newBookAssessment.setRate(newRate);
            newBookAssessment.setCreateTime(LocalDateTime.now());
            bookAssessmentRepository.save(newBookAssessment);
        } else {
            bookAssessment.setRate(newRate);
            bookAssessment.setCreateTime(LocalDateTime.now());
            bookAssessmentRepository.save(bookAssessment);
        }
    }

    public Integer getBookRate(Book book) {
        List<BookAssessment> assessments = bookAssessmentRepository.findBookAssessmentsByBook(book);
        if (assessments == null || assessments.size() == 0) {
            return 0;
        } else {
            return (int) Math.round(assessments.stream()
                    .mapToInt(BookAssessment::getRateInt)
                    .average()
                    .getAsDouble());
        }
    }

    public Integer getBookUserRate(BookStoreUser user, Book book) {
        BookAssessment assessment = bookAssessmentRepository.findBookAssessmentByUserAndBook(user, book);
        if (assessment == null) {
            return 0;
        } else {
            return assessment.getRateInt();
        }
    }

    public Map<Integer, Long> getBookUsersRates(Book book) {
        List<BookAssessment> assessments = bookAssessmentRepository.findBookAssessmentsByBook(book);
        if (assessments == null || assessments.size() == 0) {
            Map<Integer, Long> rate = new HashMap<>();
            rate.put(5, 0L);
            rate.put(4, 0L);
            rate.put(3, 0L);
            rate.put(2, 0L);
            rate.put(1, 0L);
            return rate;
        } else {
            Map<Integer, Long> rate = assessments.stream()
                    .collect(Collectors.groupingBy(BookAssessment::getRateInt, Collectors.counting()));
            rate.putIfAbsent(5, 0L);
            rate.putIfAbsent(4, 0L);
            rate.putIfAbsent(3, 0L);
            rate.putIfAbsent(2, 0L);
            rate.putIfAbsent(1, 0L);
            return rate;
        }
    }

    public Integer getBookUsersRatesCount(Book book) {
        List<BookAssessment> assessments = bookAssessmentRepository.findBookAssessmentsByBook(book);
        if (assessments == null || assessments.size() == 0) {
            return 0;
        } else {
            return assessments.size();
        }
    }
}
