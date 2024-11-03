package com.alura.alura_challenge3.models;

import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private String title;
    private List<Author> authors;
    private List<String> languages;
    private Integer downloads;


    public Book(){}

    public Book(BookData book_data){
        this.title=book_data.title();
        this.authors=new ArrayList<>();
        for(AuthorData autor_data: book_data.authors()){
            this.authors.add(new Author(autor_data));
        }
        this.languages=book_data.languages();
        this.downloads=book_data.downloads();
    }

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", authors=" + authors +
                ", languages=" + languages +
                ", downloads=" + downloads;
    }

    public String get_title() {
        return title;
    }

    public void set_title(String title) {
        this.title = title;
    }

    public List<Author> get_authors() {
        return authors;
    }

    public void set_authors(List<Author> authors) {
        this.authors = authors;
    }

    public List<String> get_languages() {
        return languages;
    }

    public void set_languages(List<String> languages) {
        this.languages = languages;
    }

    public Integer get_downloads() {
        return downloads;
    }

    public void set_downloads(Integer downloads) {
        this.downloads = downloads;
    }
}
