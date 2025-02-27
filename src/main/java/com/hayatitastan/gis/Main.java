package com.hayatitastan.gis;

/**
 * The Main class serves as the entry point for the GIS application.
 * It initializes and runs the MapViewer to display the map.
 *
 * @author Dr. Hayati TAŞTAN
 * @version 1.0
 */
public class Main {
    /**
     * The main method which is the entry point of the application.
     * It creates an instance of MapViewer and displays the map.
     *
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        MapViewer mapViewer = new MapViewer();
        mapViewer.displayMap();
    }
}
