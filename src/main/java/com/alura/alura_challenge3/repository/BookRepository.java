package com.alura.alura_challenge3.repository;

import com.alura.alura_challenge3.models.Author;
import com.alura.alura_challenge3.models.Book;
import com.alura.alura_challenge3.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {
    @Query("select distinct l from Book b join b.languages l")
    List<Language> find_languages();
    @Query("select count(l) from Book b join b.languages l where l=:language")
    Object find_count_by_language(Language language);
    @Query("select b from Book b order by b.downloads desc limit 10")
    List<Book> find_top10();
    @Query("select count(b) > 0 from Book b where b.title=:title")
    Boolean is_book_present(String title);
}
