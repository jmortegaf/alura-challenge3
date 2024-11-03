package com.alura.alura_challenge3.models;

import java.util.List;

public class Author {
    private String author_name;
    private Integer birth_year;
    private Integer death_year;

    public Author(AuthorData authors) {
        this.author_name= authors.author_name();
        this.birth_year=authors.birth_year();
        this.death_year=authors.death_year();
    }

    @Override
    public String toString() {
        return "author_name='" + author_name + '\'' +
                ", birth_year=" + birth_year +
                ", death_year=" + death_year;
    }
}
