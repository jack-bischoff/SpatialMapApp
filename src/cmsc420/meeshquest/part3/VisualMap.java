package cmsc420.meeshquest.part2;

import cmsc420.drawing.CanvasPlus;

import java.awt.*;
import java.io.IOException;

public class VisualMap {
    private static VisualMap instance;
    private CanvasPlus canvas;
    private VisualMap() { canvas = new CanvasPlus("MeeshQuest");}

    public static VisualMap VisualMap() {
        if (instance == null) instance = new VisualMap();
        return instance;
    }

    public static boolean isInitalized() {
        return !(instance == null);
    }
    public void setFrame(int width, int height) {
        canvas.setFrameSize(width, height);
        addRectangle(0, 0, width, height, Color.WHITE, true);
        addRectangle(0, 0, width, height, Color.BLACK, false);
    }

    public void addPoint(String name, double x, double y) {
        canvas.addPoint(name, x, y, Color.BLACK);
    }

    public void addPoint(String name, int x, int y) {
        addPoint(name, (double)x, (double)y);
    }

    public void removePoint(String name, int x, int y) {
        removePoint(name, (double)x, (double)y);
    }

    public void removePoint(String name, double x, double y) {
        canvas.removePoint(name, x, y, Color.BLACK);
    }

    public void addRectangle(int originX, int originY, int endX, int endY, Color color, boolean filled ) {
        canvas.addRectangle(originX, originY, endX, endY, color, filled);
    }

    public void addCross(int originX, int originY, int width, int height) {
        int horizontal = originX - (width / 2);
        int vertical = originY - (height / 2);
        canvas.addLine(horizontal, originY, width, (float)height / 2, Color.GRAY);
        canvas.addLine(originX, vertical, (float)width / 2, height, Color.GRAY);
    }

    public void removeCross(int originX, int originY, int width, int height) {
        int horizontal = originX - (width / 2);
        int vertical = originY - (height / 2);
        canvas.removeLine(horizontal, originY, width, (float)height / 2, Color.GRAY);
        canvas.removeLine(originX, vertical, (float)width / 2, height, Color.GRAY);
    }

    public void addCircle(int x, int y, int radius, Color color, boolean filled) {
        canvas.addCircle(x, y, radius, color, filled);
    }

    public void save(String filename) throws IOException {
        canvas.save(filename);
    }
    public void draw() {
        canvas.drawBlocking();
    }
    public void dispose() {
        canvas.dispose();
    }

}
