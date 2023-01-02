package com.example.application.services;


import com.example.application.WeatherApi;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
@Service
public class weatherService implements Serializable {

    public List<WeatherApi> getWeatherService() {

        System.out.println("Fetching all Post objects through REST..");

        // Fetch from 3rd party API; configure fetch
        final WebClient.RequestHeadersSpec<?> spec = WebClient.create().get().uri("https://api.openweathermap.org/data/2.5/weather?lat=35.42&lon=7.14&appid=5ec0b81ebb2fb1d9486b936acdfaee60&lang=AR&units=metric");

        // do fetch and map result
        final List<WeatherApi> posts = spec.retrieve().toEntityList(WeatherApi.class).block().getBody();


        return posts;

    }

}