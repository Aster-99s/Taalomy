package com.example.application.services;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

import java.util.Map;
@Tag("weather-widget")

// Widget class
public class WeatherWidget extends Component implements HasComponents {


    // Constructor
    public WeatherWidget(Map<String, Object> weatherStatus) {
        // Create a div element to wrap the widget content
        Div wrapper = new Div();
        wrapper.addClassName("wrapper");

        // Check if the weather status map is not empty
        if (!weatherStatus.isEmpty()) {
            // Get the weather information from the map
            int weatherId = (int) weatherStatus.get("id");
            String weatherMain = (String) weatherStatus.get("main");
            String weatherDescription = (String) weatherStatus.get("description");
            String weatherIcon = (String) weatherStatus.get("icon");

            // Create an image element to display the weather icon
            Image icon = new Image();
            icon.setSrc("/icons/weatherIcons/" + weatherIcon + ".png");
            icon.setAlt(weatherDescription);
            icon.addClassName("icon");


            // Create a div element to wrap the icon and temp elements
            Div leftSide = new Div();
            leftSide.add(icon);
            leftSide.addClassName("left-side");

            // Create a span element to display the weather id
            Span id = new Span();
            id.setText("ID: " + weatherId);
            id.addClassName("id");

            // Create a span element to display the weather main
            Span main = new Span();
            main.setText("Main: " + weatherMain);
            main.addClassName("main");

            // Create a span element to display the weather description
            Span description = new Span();
            description.setText("Description: " + weatherDescription);
            description.addClassName("description");

            // Create a div element to wrap the id, main, and description elements
            Div rightSide = new Div();
            rightSide.add(id, main, description);
            rightSide.addClassName("right-side");

            // Add the left and right sides to the wrapper element
            wrapper.add(leftSide, rightSide);
        } else {
            // If the weather status map is empty, display a message
            Span message = new Span();
            message.setText("No weather information available.");
            message.addClassName("message");

            // Add the message to the wrapper element
            wrapper.add(message);
        }

        // Add the wrapper element to this component
        add(wrapper);
    }
}