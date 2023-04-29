package com.example.application.views.teacherdashboard;

import com.example.application.views.MainLayout;
import com.example.application.views.teacherdashboard.ServiceHealth.Status;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.stefan.fullcalendar.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.*;

@PageTitle("لوحة تحكم الأستاذ")
@Route(value = "TeacherDashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class teacherDashboardView extends Main {


    public teacherDashboardView() {
        //RTL Support
        final UI ui = UI.getCurrent();
        ui.setDirection(Direction.RIGHT_TO_LEFT);
        Header header = new Header();
        addClassName("teacher-dashboard-view");
        Board board = new Board();
        // adding items to the board
     // board.addRow(weather());
        board.addRow(calendar());
        board.addRow(createServiceHealth(), createResponseTimes());
        // adding the board to the view
        add(header,board);
    }

// For weather , icons in icons/weathericons
    private Map<String, Object> weather(){
        Map<String, Object> getWeatherStatus; {
            // Initialize a map to store the weather status
            Map<String, Object> weatherStatus = new HashMap<>();

            try {
                VaadinRequest request = VaadinRequest.getCurrent();

                // Get the user location from the request
                String userLocation = request.getParameter("location");
                H1 whwh = new H1(userLocation);

                // If the user location is null or empty, return an empty map
                if (userLocation == null || userLocation.isEmpty()) {
                    return (Map<String, Object>) weatherStatus;
                }

                // Build the Open Weather API URL with the user location and an API key
                // You need to get your own API key from https://openweathermap.org/api_keys
                String apiKey = "5ec0b81ebb2fb1d9486b936acdfaee60";
                // 35.4269° N, 7.1460° E

                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=35.42&lon=7.14&appid=5ec0b81ebb2fb1d9486b936acdfaee60&lang=AR&units=metric&";

                // Create a URL object and open a connection
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to GET and connect
                connection.setRequestMethod("GET");
                connection.connect();

                // Get the response code and check if it is 200 (OK)
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    // Create a scanner to read the response
                    Scanner scanner = new Scanner(url.openStream());

                    // Append the response to a string
                    String response = "";
                    while (scanner.hasNext()) {
                        response += scanner.nextLine();
                    }

                    // Close the scanner
                    scanner.close();

                    // Parse the response as a JSON object
                    JsonObject responseJson = Json.parse(response);

                    // Get the weather array from the JSON object
                    JsonArray weatherArray = responseJson.getArray("weather");

                    // If the weather array is not empty, get the first element
                    if (weatherArray.length() > 0) {
                        JsonObject weatherObject = weatherArray.getObject(0);

                        // Get the weather id, main, description, and icon from the weather object
                        int weatherId = (int) weatherObject.getNumber("id");
                        String weatherMain = weatherObject.getString("main");
                        String weatherDescription = weatherObject.getString("description");
                        String weatherIcon = weatherObject.getString("icon");

                        // Put the weather information into the map
                        weatherStatus.put("id", weatherId);
                        weatherStatus.put("main", weatherMain);
                        weatherStatus.put("description", weatherDescription);
                        weatherStatus.put("icon", weatherIcon);
                    }
                }
            } catch (Exception e) {
                // Handle any exceptions
                e.printStackTrace();
            }

            // Return the map with the weather status
            return  weatherStatus;
        }

    };



    private Component calendar(){
        FullCalendar calendar = FullCalendarBuilder.create().build();
        calendar.getBrowserTimezone();
        calendar.setHeight(500);
        calendar.changeView(CalendarViewImpl.DAY_GRID_MONTH );
        calendar.setLocale(Locale.forLanguageTag("ar-DZ"));
        //Calendar entries
        Entry entry = new Entry();
        entry.setTitle("تنبيهات متنوعة");
        entry.setAllDay(true);
        entry.setColor("#ff3333");
        entry.setStart(LocalDate.now().withDayOfMonth(3).atTime(10, 0));
        entry.setEnd(entry.getStart().plusHours(2));
        calendar.addEntry(entry);
        return calendar;
    }


    private Component createServiceHealth() {
        // Header
        HorizontalLayout header = createHeader("Service health", "Input / output");

        // Grid
        Grid<ServiceHealth> grid = new Grid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        grid.addColumn(new ComponentRenderer<>(serviceHealth -> {
            Span status = new Span();
            String statusText = getStatusDisplayName(serviceHealth);
            status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().setAttribute("title", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(serviceHealth));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(ServiceHealth::getCity).setHeader("City").setFlexGrow(1);
        grid.addColumn(ServiceHealth::getInput).setHeader("Input").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(ServiceHealth::getOutput).setHeader("Output").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.setItems(new ServiceHealth(Status.EXCELLENT, "Münster", 324, 1540),
                new ServiceHealth(Status.OK, "Cluj-Napoca", 311, 1320),
                new ServiceHealth(Status.FAILING, "Ciudad Victoria", 300, 1219));

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    private Component createResponseTimes() {
        HorizontalLayout header = createHeader("Response times", "Average across all systems");

        // Chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        chart.setThemeName("gradient");

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("System 1", 12.5));
        series.add(new DataSeriesItem("System 2", 12.5));
        series.add(new DataSeriesItem("System 3", 12.5));
        series.add(new DataSeriesItem("System 4", 12.5));
        series.add(new DataSeriesItem("System 5", 12.5));
        series.add(new DataSeriesItem("System 6", 12.5));
        conf.addSeries(series);

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, chart);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private String getStatusDisplayName(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        if (status == Status.OK) {
            return "Ok";
        } else if (status == Status.FAILING) {
            return "Failing";
        } else if (status == Status.EXCELLENT) {
            return "Excellent";
        } else {
            return status.toString();
        }
    }

    private String getStatusTheme(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        String theme = "badge primary small";
        if (status == Status.EXCELLENT) {
            theme += " success";
        } else if (status == Status.FAILING) {
            theme += " error";
        }
        return theme;
    }
    public static String executePost(String targetURL) {
        try {
            URL yahoo = new URL(targetURL);
            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
}

