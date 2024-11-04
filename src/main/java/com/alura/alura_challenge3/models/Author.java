package com.alura.alura_challenge3.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String author_name;
    private Integer birth_year;
    private Integer death_year;
    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

    public Author(){}

    public Author(AuthorData authors) {
        this.author_name= authors.author_name();
        this.birth_year=authors.birth_year();
        this.death_year=authors.death_year();
        books=new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder output_string= new StringBuilder("=".repeat(120)+"\n"+
                "name      : "+this.get_formated_author_name()+"\n"+
                "birth year: "+birth_year+"\n"+
                "death year: "+death_year);
        return output_string.toString();
    }

    public String get_formated_author_name(){
        var tmp=author_name.split(", ");
        if(tmp.length>1)return tmp[1]+" "+tmp[0];
        else return tmp[0];
    }

    public Long get_id() {
        return id;
    }

    public void set_id(Long id) {
        this.id = id;
    }

    public String get_author_name() {
        return author_name;
    }

    public void set_author_name(String author_name) {
        this.author_name = author_name;
    }

    public Integer get_birth_year() {
        return birth_year;
    }

    public void set_birth_year(Integer birth_year) {
        this.birth_year = birth_year;
    }

    public Integer get_death_year() {
        return death_year;
    }

    public void set_death_year(Integer death_year) {
        this.death_year = death_year;
    }

    public List<Book> get_books() {
        return books;
    }

    public void set_books(Book book) {
        this.books.add(book);
    }
}
