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
    @ManyToMany(fetch = FetchType.EAGER)
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
        this.title=book_data.title();
        this.authors=new ArrayList<>();
        for(AuthorData author_data: book_data.authors()){
            this.authors.add(new Author(author_data));
        }
        for(Author author:this.authors){
            author.set_books(this);
        }
        this.languages=new ArrayList<>();
        for(String language:book_data.languages()){
            this.languages.add(Language.fromString(language));
        }
        this.downloads=book_data.downloads();
    }

    @Override
    public String toString() {
        StringBuilder output_string= new StringBuilder("=".repeat(120)+"\n"+
                "title:     " + title + "\n" +
                "authors:   ");
        for(int i=0;i<authors.size();i++){
            output_string.append(authors.get(i).get_formated_author_name()).append("\n");
            if(i!=authors.size()-1)output_string.append("           ");
        }
        output_string.append("languages: ");
        for(int i=0;i<languages.size();i++){
            output_string.append(languages.get(i)).append("\n");
            if(i!=languages.size()-1)output_string.append("           ");
        }
        output_string.append("downloads: ").append(downloads);
        return output_string.toString();
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
