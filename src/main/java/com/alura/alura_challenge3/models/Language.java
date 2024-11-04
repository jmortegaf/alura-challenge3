package com.alura.alura_challenge3.models;

public enum Language {
    ENGLISH("EN","ENGLISH"),
    SPANISH("ES","ESPAÑOL"),
    FRENCH("FR","FRENCH");

    private String abbr;
    private String language;

    Language(String abbr,String language){
        this.abbr=abbr;
        this.language=language;
    }
    public static Language fromString(String text){
        for(Language l:Language.values()){
            if(l.abbr.equalsIgnoreCase(text) || l.language.equalsIgnoreCase(text))
                return l;
        }
        throw new IllegalArgumentException("Language not found: "+text);
    }
}
