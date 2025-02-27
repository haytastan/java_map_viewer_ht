package com.hayatitastan.gis;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class MapViewer {
    private Point2D startPoint;
    private Cursor openHandCursor;
    private Cursor grabbedHandCursor;
    private JXMapViewer mapViewer;
    private Map<String, TileFactoryInfo> baseMapOptions;

    public void displayMap() {
        JFrame frame = new JFrame("Free & Open Source Map Viewer (v1.0)");
        mapViewer = new JXMapViewer();

        // Load base map options
        baseMapOptions = new HashMap<>();
        baseMapOptions.put("OpenStreetMap", new TileFactoryInfo(1, 15, 17, 256, true, true,
                "https://tile.openstreetmap.org", "x", "y", "z") {
            @Override
            public String getTileUrl(int x, int y, int zoom) {
                int z = 17 - zoom;
                return this.baseURL + "/" + z + "/" + x + "/" + y + ".png";
            }
        });

        baseMapOptions.put("OpenTopoMap", new TileFactoryInfo(1, 15, 17, 256, true, true,
                "https://tile.opentopomap.org", "x", "y", "z") {
            @Override
            public String getTileUrl(int x, int y, int zoom) {
                int z = 17 - zoom;
                return this.baseURL + "/" + z + "/" + x + "/" + y + ".png";
            }
        });

        // Set initial base map
        setBaseMap("OpenStreetMap");

        GeoPosition frankfurt = new GeoPosition(50.11, 8.68);
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(frankfurt);

        // Load cursor images from resources
        openHandCursor = loadCustomCursor("/open_hand_cursor.png", "Open Hand");
        grabbedHandCursor = loadCustomCursor("/grabbed_hand_cursor.png", "Grabbed Hand");

        mapViewer.setCursor(openHandCursor);

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    startPoint = e.getPoint();
                    mapViewer.setCursor(grabbedHandCursor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mapViewer.setCursor(openHandCursor);
                }
            }
        });

        mapViewer.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && startPoint != null) {
                    Point2D endPoint = e.getPoint();
                    double deltaX = startPoint.getX() - endPoint.getX();
                    double deltaY = startPoint.getY() - endPoint.getY();
                    Point2D center = mapViewer.getCenter();
                    mapViewer.setCenter(new Point2D.Double(center.getX() + deltaX, center.getY() + deltaY));
                    startPoint = endPoint;
                }
            }
        });

        mapViewer.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() > 0) {
                    mapViewer.setZoom(mapViewer.getZoom() - 1);
                } else {
                    mapViewer.setZoom(mapViewer.getZoom() + 1);
                }
            }
        });

        // Add combo box for base map selection
        JComboBox<String> comboBox = new JComboBox<>(baseMapOptions.keySet().toArray(new String[0]));
        comboBox.addActionListener(e -> setBaseMap((String) comboBox.getSelectedItem()));

        JLabel selectBaseMapLabel = new JLabel("Select a base map:");

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(selectBaseMapLabel);
        controlPanel.add(comboBox);

        JLabel footerLabel = new JLabel("Developed by Dr. Hayati TAŞTAN & No rights reserved :)");
        footerLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(mapViewer, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(footerLabel, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setBaseMap(String baseMapName) {
        TileFactoryInfo info = baseMapOptions.get(baseMapName);
        if (info != null) {
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapViewer.setTileFactory(tileFactory);
        }
    }

    private Cursor loadCustomCursor(String resourcePath, String cursorName) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(getClass().getResource(resourcePath));
        return toolkit.createCustomCursor(image, new Point(0, 0), cursorName);
    }
}