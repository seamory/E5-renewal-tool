package com.contoso;

import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.User;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("Java Graph Tutorial");
        System.out.println();
        System.out.println("    NOTE: Please make sure your application program is available. "
                + "the detail you can realize on https://docs.microsoft.com/zh-CN/graph/tutorials/java?tutorial-step=2.");
        System.out.println();
        System.out.println("    NOTE: Your should to create a 'oAuth.properties' file first and keep the same path as the program.");
        System.out.println("        the 'oAuth.properties' file content:");
        System.out.println("            app.id=Client_ID (You can get it in your application on https://apps.dev.microsoft.com/)\r\n" +
                "            app.scopes=User.Read,Calendars.Read");
        System.out.println();

        // Load OAuth settings
        final Properties oAuthProperties = new Properties();
        try {
            // File directory = new File("..");
            // final String filePath = directory.getAbsolutePath();
            final String filePath = System.getProperty("user.dir");
            System.out.println(filePath);
            // oAuthProperties.load(App.class.getResourceAsStream(filePath+ "\\oAuth.properties"));
            oAuthProperties.load(new InputStreamReader(new BufferedInputStream(new FileInputStream(filePath+ "\\oAuth.properties"))));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            return;
        }

        final String appId = oAuthProperties.getProperty("app.id");
        final String[] appScopes = oAuthProperties.getProperty("app.scopes").split(",");

        // Get an access token
        Authentication.initialize(appId);
        final String accessToken = Authentication.getUserAccessToken(appScopes);

        // Greet the user
        User user = Graph.getUser(accessToken);
        System.out.println("Welcome " + user.displayName);
        System.out.println();

        Scanner input = new Scanner(System.in);

        int choice = -1;

        while (choice != 0) {
            System.out.println("Please choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Display access token");
            System.out.println("2. List calendar events");

            try {
                choice = input.nextInt();
            } catch (InputMismatchException ex) {
                // Skip over non-integer input
                input.nextLine();
            }

            // Process user choice
            switch(choice) {
                case 0:
                    // Exit the program
                    System.out.println("Goodbye...");
                    break;
                case 1:
                    // Display access token
                    System.out.println("Access token: " + accessToken);
                    break;
                case 2:
                    // List the calendar
                    listCalendarEvents(accessToken);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }

        input.close();
    }

    private static String formatDateTimeTimeZone(DateTimeTimeZone date) {
        LocalDateTime dateTime = LocalDateTime.parse(date.dateTime);

        return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) + " (" + date.timeZone + ")";
    }

    private static void listCalendarEvents(String accessToken) {
        // Get the user's events
        List<Event> events = Graph.getEvents(accessToken);

        System.out.println("Events:");

        for (Event event : events) {
            System.out.println("Subject: " + event.subject);
            System.out.println("  Organizer: " + event.organizer.emailAddress.name);
            System.out.println("  Start: " + formatDateTimeTimeZone(event.start));
            System.out.println("  End: " + formatDateTimeTimeZone(event.end));
        }

        System.out.println();
    }

}
