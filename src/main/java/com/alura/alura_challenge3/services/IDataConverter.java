package com.alura.alura_challenge3.services;

public interface IDataConverter {

    <T> T get_data(String json, Class<T> _class);
}
