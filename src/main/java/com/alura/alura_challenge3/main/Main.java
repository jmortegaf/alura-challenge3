package com.alura.alura_challenge3.main;

import com.alura.alura_challenge3.models.Book;
import com.alura.alura_challenge3.models.BookData;
import com.alura.alura_challenge3.models.QueryResponseData;
import com.alura.alura_challenge3.services.APIRequest;
import com.alura.alura_challenge3.services.DataConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Main {

    private Scanner scanner=new Scanner(System.in);
    private APIRequest api_request=new APIRequest();
    private DataConverter data_converter=new DataConverter();
    private List<Book> search_history=new ArrayList<>();


    public void show_menu()throws Exception {
        while (true){
            System.out.println("Wellcome to LiterAlura:");
            System.out.println("1. Find Book By Title.");
            System.out.println("2. Search History.");
            System.out.println("3. Author's List");
            System.out.println("[Q]uit");
            var user_input=scanner.nextLine();
            if(user_input.equalsIgnoreCase("q"))break;
            else if(user_input.equalsIgnoreCase("1"))
                find_book_by_title_web();
            else if(user_input.equalsIgnoreCase("2"))
                show_search_history();
            else if(user_input.equalsIgnoreCase("3"))
                show_authors_list();
        }
    }
    private void find_book_by_title_web() throws Exception{

        System.out.println("Title");
        var search_filter=scanner.nextLine();
        var json=api_request.request(
                "http://gutendex.com/books/?search="+search_filter.replace(" ","%20"));
        var response_data=data_converter.get_data(json, QueryResponseData.class);

        if(response_data.count()!=0){
            Book book=response_data.results().stream()
                    .filter(b->b.title().toLowerCase().contains(search_filter.toLowerCase()))
                    .map(Book::new)
                    .toList().get(0);

            System.out.println(book);
            search_history.add(book);
        }
        else System.out.println("Book not found.");
    }

    private void show_search_history() {
        System.out.println("Search History.");
        search_history.forEach(System.out::println);
    }

    private void show_authors_list(){

    }

}
