package com.alura.alura_challenge3.main;

import com.alura.alura_challenge3.models.*;
import com.alura.alura_challenge3.repository.AuthorRepository;
import com.alura.alura_challenge3.repository.BookRepository;
import com.alura.alura_challenge3.services.APIRequest;
import com.alura.alura_challenge3.services.DataConverter;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Main {

    private Scanner scanner=new Scanner(System.in);
    private APIRequest api_request=new APIRequest();
    private DataConverter data_converter=new DataConverter();
    private List<Book> search_history=new ArrayList<>();

    @Autowired
    private BookRepository repository;
    @Autowired
    private AuthorRepository author_repository;

    public void show_menu()throws Exception {
        while (true){
            System.out.println("Wellcome to LiterAlura:");
            System.out.println("1. Find Book By Title. [API]");
            System.out.println("2. Search History. [API]");
            System.out.println("3. Author's List. [API]");
            System.out.println("4. Authors Alive At. [API]");
            System.out.println("5. Books By Language. [DB]");
            System.out.println("[Q]uit");
            var user_input=scanner.nextLine();
            if(user_input.equalsIgnoreCase("q"))break;
            else if(user_input.equalsIgnoreCase("1"))
                find_book_by_title_web();
            else if(user_input.equalsIgnoreCase("2"))
                show_search_history();
            else if(user_input.equalsIgnoreCase("3"))
                show_authors_list_from_api();
            else if(user_input.equalsIgnoreCase("4"))
                show_authors_by_data_from_api();
            else if(user_input.equalsIgnoreCase("5"))
                show_books_by_languague();
        }
    }
    private void find_book_by_title_web() throws Exception{

        System.out.println("Title");
        var search_filter=scanner.nextLine();
        var json=api_request.request(
                "http://gutendex.com/books/?search="+search_filter.replace(" ","%20"));
        var response_data=data_converter.get_data(json, QueryResponseData.class);

        if(response_data.count()!=0){
            BookData data =response_data.results().stream()
                    .filter(b->b.title().toLowerCase().contains(search_filter.toLowerCase()))
                    .toList().get(0);
            Book book=new Book(data);

            System.out.println(book);
            search_history.add(book);
            List<Author> stored_authors=new ArrayList<>();

            for(Author author:book.get_authors()){
                var _author=author_repository.find_by_name(author.get_author_name());
                if(_author!=null)stored_authors.add(_author);
            }
            if(!stored_authors.isEmpty()){
                Map<String,Boolean> stored_map=stored_authors.stream()
                        .collect(Collectors.toMap(Author::get_author_name,obj->true));
                List<Author> refactor_authors = new ArrayList<>();
                for(Author _author:book.get_authors()){
                    if(!stored_map.get(_author.get_author_name())){
                        refactor_authors.add(_author);
                    }
                }
                book.set_authors(refactor_authors);
                book.get_authors().forEach(a->author_repository.save(a));
                book.add_authors(stored_authors);
            }
            else{
                book.get_authors().forEach(a->author_repository.save(a));
            }
            repository.save(book);
        }
        else System.out.println("Book not found.");
    }

    private void show_search_history() {
        System.out.println("Search History.");
        search_history.forEach(System.out::println);
    }

    private void show_authors_list_from_api(){
        System.out.println("Author's List");
        Map<String,Author> authors_map=new HashMap<>();
        //        search_history.forEach(b-> authors.addAll(b.get_authors()));
        search_history.forEach(b->b.get_authors().forEach(a->authors_map.put(a.get_author_name(),a)));

        if(!authors_map.isEmpty()){
            authors_map.forEach((key,author)-> System.out.println(author));
        }
        else System.out.println("Search history is empty");

    }

    private void show_authors_by_data_from_api(){
        System.out.println("Year");
        var search_filter=scanner.nextLine();
        try {
            var filter_year=Integer.valueOf(search_filter);
            Map<String,Author> authors_map=new HashMap<>();
            search_history.forEach(a->a.get_authors().stream()
                    .filter(a2-> a2.get_birth_year()<=filter_year && a2.get_death_year()>=filter_year)
                    .forEach(a3->authors_map.put(a3.get_author_name(),a3)));
            if(!authors_map.isEmpty()){
                authors_map.forEach((key,author)-> System.out.println(author));
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid year");
        }
    }

    private void show_books_by_languague(){
        System.out.println("Books By Language");
        List<Language> languages=repository.find_languages();
        for(int i=0;i<languages.size();i++){
            System.out.println((i+1)+". "+languages.get(i));
        }
        System.out.println("Select a language");
        var user_input=scanner.nextLine();
        try {
            var book_count=repository.find_count_by_language(languages.get(Integer.valueOf(user_input)-1));
            System.out.println(book_count);
        }catch (NumberFormatException e){
            System.out.println("Invalid selection.");
        }
    }

    private void show_authors_list(){
        System.out.println("Author's List");
        List<Author> authors=author_repository.findAll();
        authors.forEach(System.out::println);
    }

    private void show_authors_by_date(){
        System.out.println("Year");
        var search_filter=scanner.nextLine();
        try{
            List<Author> authors=author_repository.find_by_date(Integer.valueOf(search_filter));
            authors.forEach(System.out::println);
        }catch (NumberFormatException e){
            System.out.println("Invalid year");
        }
    }

}
