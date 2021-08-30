package sample;

import java.util.Scanner;
import java.util.jar.Attributes;

public class Console {
    int zoom = 1;
    int l = 0;
    int r = 10;
    int u = 0;
    int d = 10;

    void start() {
        int choice = -1;
        System.out.println("Enter 1 to display map, 2 to zoom in, 3 to zoom out, 4 to move border");
        choice = (new Scanner(System.in).nextInt());
        switch (choice) {
            case 1:
                System.out.println("L: " + l * zoom + " R: " + r * zoom + " U: " + u * zoom + " D: " + d * zoom);
                for (int j = u; j < d; j++) {
                    for (int i = l; i < r; i++) {
                        String display = "E";
//                        System.out.println(i*zoom+" "+j*zoom);
                        if (Game.getInstance().getUnit(i * zoom, j * zoom) != null) {
                            Type t = Game.getInstance().getUnit(i * zoom, j * zoom).getType();
                            SubType st = Game.getInstance().getUnit(i * zoom, j * zoom).getName();
                            Side s = Game.getInstance().getUnit(i * zoom, j * zoom).getSide();
                            if (t == Type.Environment) {
                                if (st == SubType.valley) {
                                    display = "V";
                                } else if (st == SubType.river) {
                                    display = "R";
                                } else if (st == SubType.bridge) {
                                    display = "B";
                                }
                            } else if (s == Side.Defence) {
                                if (st == SubType.MainBase) {
                                    display = "M";
                                } else
                                    display = "D";
                            } else if (s == Side.Attack) {
                                display = "A";
                            }
                        }
                        System.out.print(display + " ");
                    }
                    System.out.println();
                }
                break;
            case 2:
                if (zoom > 1) {
                    zoom /= 10;
                }
                System.out.println(zoom);
                break;
            case 3:
                if (zoom * 10 < Settings.getGroundSize()) {
                    zoom *= 10;
//                    l = 0;
//                    u = 0;
//                    r = 10;
//                    d = 10;
                }
                System.out.println(zoom);
                break;
            case 4:
                System.out.println("Enter 1 to go up, 2 to go down, 3 to go left, 4 to go right");
                int choice2 = -1;
                choice2 = (new Scanner(System.in).nextInt());
                switch (choice2) {
                    case 1:
                        if (u - zoom >= 0) {
                            u -= zoom;
                            d -= zoom;
                        }
                        break;
                    case 2:
                        if (d + zoom < Settings.getGroundSize()) {
                            u += zoom;
                            d += zoom;
                        }
                        break;
                    case 3:
                        if (l - zoom >= 0) {
                            l -= zoom;
                            r -= zoom;
                        }
                        break;
                    case 4:
                        if (r + zoom < Settings.getGroundSize()) {
                            l += zoom;
                            r += zoom;
                        }
                        break;
                }
                break;
        }
        start();
    }
}