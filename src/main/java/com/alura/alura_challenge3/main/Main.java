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
            System.out.println("=".repeat(120));
            System.out.println("Wellcome to LiterAlura:");
            System.out.println("1. Find book by title.");
            System.out.println("2. Search history.");
            System.out.println("3. Author's list.");
            System.out.println("4. Authors alive at.");
            System.out.println("5. Books by language.");
            System.out.println("6. Top 10 most downloaded books.");
            System.out.println("7. Find author by name.");
            System.out.println("8. Find author by birth year.");
            System.out.println("9. Find author by death year.");
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
                show_authors_by_date();
            else if(user_input.equalsIgnoreCase("5"))
                show_books_by_languague();
            else if(user_input.equalsIgnoreCase("6"))
                show_top10_books();
            else if(user_input.equalsIgnoreCase("7"))
                find_author_by_name();
            else if(user_input.equalsIgnoreCase("8"))
                find_author_by_birth_year();
            else if(user_input.equalsIgnoreCase("9"))
                find_author_by_death_year();
        }
    }
    private void find_book_by_title_web() throws Exception{

        System.out.print("Title: ");
        var search_filter=scanner.nextLine();
        var json=api_request.request(
                "http://gutendex.com/books/?search="+search_filter.replace(" ","%20"));
        var response_data=data_converter.get_data(json, QueryResponseData.class);

        try{
            if(response_data.count()!=0){
                BookData data =response_data.results().stream()
                        .filter(b->b.title().toLowerCase().contains(search_filter.toLowerCase()))
                        .toList().get(0);
                Book book=new Book(data);

                System.out.println(book);
                System.out.println("=".repeat(120));
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
                var is_book_present=repository.is_book_present(book.get_title());
                if(!is_book_present)
                    repository.save(book);
                else System.out.println("Book already on the Database.");
                continue_msg();
            }
            else {
                System.out.println("=".repeat(120));
                System.out.println("Book not found.");
                continue_msg();
            }
        }catch (Exception e){
            System.out.println("Book already on the Database.");
            continue_msg();
        }
    }

    private void show_search_history() {
        System.out.println("=".repeat(120));
        System.out.println("Search History.");
        if(!search_history.isEmpty())
            search_history.forEach(System.out::println);
        else{
            System.out.println("Empty.");
        }
        continue_msg();
    }

    private void show_authors_list_from_api(){
        System.out.println("=".repeat(120));
        System.out.println("Author's List");
        Map<String,Author> authors_map=new HashMap<>();
        //        search_history.forEach(b-> authors.addAll(b.get_authors()));
        search_history.forEach(b->b.get_authors().forEach(a->authors_map.put(a.get_author_name(),a)));

        if(!authors_map.isEmpty()){
            authors_map.forEach((key,author)-> System.out.println(author));
            System.out.println("=".repeat(120));
        }
        else{
            System.out.println("=".repeat(120));
            System.out.println("Search history is empty");
        }
        continue_msg();

    }

    private void show_authors_by_date_from_api(){
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
        System.out.println("=".repeat(120));
        System.out.println("Books By Language");
        List<Language> languages=repository.find_languages();
        for(int i=0;i<languages.size();i++){
            System.out.println((i+1)+". "+languages.get(i));
        }
        if(languages.isEmpty()) System.out.println("No languages in the daatabase.");
        System.out.println("=".repeat(120));
        System.out.print("Select a language: ");
        var user_input=scanner.nextLine();
        try {
            var book_count=repository.find_count_by_language(languages.get(Integer.valueOf(user_input)-1));
            System.out.println("There are "+book_count+" books in "+
                    languages.get(Integer.valueOf(user_input)-1)+" stored.");
        }catch (NumberFormatException | IndexOutOfBoundsException e){
            System.out.println("Invalid selection.");
        }
        continue_msg();
    }

    private void show_authors_list(){
        System.out.println("Author's List");
        List<Author> authors=author_repository.findAll();
        authors.forEach(System.out::println);
    }

    private void show_authors_by_date(){
        System.out.print("Year: ");
        var search_filter=scanner.nextLine();
        try{
            List<Author> authors=author_repository.find_by_date(Integer.valueOf(search_filter));
            if(!authors.isEmpty())
                authors.forEach(System.out::println);
            else{
                System.out.println("=".repeat(120));
                System.out.println("No authors alive at "+search_filter);
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid year");
        }
        continue_msg();
    }

    private void show_top10_books(){
        System.out.println("=".repeat(120));
        System.out.println("Top 5 most downloaded books.");
        var books=repository.find_top10();
        books.forEach(System.out::println);
        continue_msg();
    }

    private void find_author_by_name(){
        System.out.println("=".repeat(120));
        System.out.println("Find author by name.");
        System.out.print("Name: ");
        var search_filter=scanner.nextLine();
        var authors=author_repository.find_authors_by_name(search_filter);
        if(!authors.isEmpty()){
            authors.forEach(System.out::println);
        }
        else{
            System.out.println("=".repeat(120));
            System.out.println("No authors matching: "+search_filter);
        }
        continue_msg();
    }

    private void find_author_by_birth_year(){
        System.out.println("=".repeat(120));
        System.out.println("Find author by birth year");
        System.out.print("Birth year: ");
        var search_filter=scanner.nextLine();
        try{
            var authors=author_repository.find_authors_by_birth_year(Integer.valueOf(search_filter));
            if(!authors.isEmpty())
                authors.forEach(System.out::println);
            else{
                System.out.println("=".repeat(120));
                System.out.println("No author was born on "+search_filter);
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid input.");
        }
        continue_msg();
    }

    private void find_author_by_death_year(){
        System.out.println("=".repeat(120));
        System.out.println("Find author by death year");
        System.out.print("Death year: ");
        var search_filter=scanner.nextLine();
        try{
            var authors=author_repository.find_authors_by_death_year(Integer.valueOf(search_filter));
            if(!authors.isEmpty())
                authors.forEach(System.out::println);
            else {
                System.out.println("=".repeat(120));
                System.out.println("No author died on "+search_filter);
            }
        }catch (NumberFormatException e){
            System.out.println("Invalid input.");
        }
        continue_msg();
    }

    private void continue_msg(){
        System.out.print("Press enter to continue.");
        scanner.nextLine();
    }

}
