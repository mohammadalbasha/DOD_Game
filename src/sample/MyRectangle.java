package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class MyRectangle extends Rectangle {
    int x, y, width, height;

    MyRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    int getx() {
        return x;
    }

    int gety() {
        return y;
    }

    int getwidth() {
        return width;
    }

    int getheight() {
        return height;
    }
}
