package com.alura.alura_challenge3.repository;

import com.alura.alura_challenge3.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    @Query("select a from Author a where a.author_name=:author_name")
    Author find_by_name(String author_name);
    @Query("select a from Author a where  a.birth_year<=:year and a.death_year>=:year")
    List<Author> find_by_date(Integer year);
    @Query("select a from Author a where a.author_name ilike %:author_name%")
    List<Author> find_authors_by_name(String author_name);
    @Query("select a from Author a where a.birth_year=:year")
    List<Author> find_authors_by_birth_year(Integer year);
    @Query("select a from Author a where a.death_year=:year")
    List<Author> find_authors_by_death_year(Integer year);
}
