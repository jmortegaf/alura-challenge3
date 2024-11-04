package com.alura.alura_challenge3.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;
    @ManyToMany
    @JoinTable(
            name="book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;
    @ElementCollection(targetClass = Language.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Language> languages;
    private Integer downloads;


    public Book(){}

    public Book(BookData book_data){
        System.out.println(book_data.book_id());
        this.title=book_data.title();
        this.authors=new ArrayList<>();
        for(AuthorData autor_data: book_data.authors()){
            this.authors.add(new Author(autor_data));
        }
        for(Author author:this.authors){
            author.set_books(this);
        }
        this.languages=new ArrayList<>();
        for(String language:book_data.languages()){
            System.out.println("language: "+language);
            this.languages.add(Language.fromString(language));
        }
        this.downloads=book_data.downloads();
    }

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", authors=" + authors +
                ", languages=" + languages +
                ", downloads=" + downloads;
    }

    public Long get_id() {
        return id;
    }

    public void set_id(Long id) {
        this.id = id;
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

    public void upadte_authors(List<Author> authors){
        List<Author> final_authors = new ArrayList<>(authors);

        Map<String,Boolean> present_authors=final_authors.stream()
                .collect(Collectors.toMap(Author::get_author_name,obj->true));

        for(Author author:this.authors){
            if(!present_authors.get(author.get_author_name()))
                final_authors.add(author);
        }

        this.authors=final_authors;
    }

    public List<Language> get_languages() {
        return languages;
    }

    public void set_languages(List<Language> languages) {
        this.languages = languages;
    }

    public Integer get_downloads() {
        return downloads;
    }

    public void set_downloads(Integer downloads) {
        this.downloads = downloads;
    }

    public void add_authors(List<Author> authors) {
        this.authors.addAll(authors);
    }
}
