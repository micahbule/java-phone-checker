/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.Image;
import static java.lang.Thread.sleep;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author snipi
 */
/*  “Which object(s) have you chosen for the synchronize? Why?”
    
    I synchronized my 'moveToRepairShop' method as only 1 'Thread' of 'Phone' should
    be allowed to called that method to go to the repair shop. All other infected 
    'Phone' must call the normal 'move()' method and move as normal.
 */
public class Phone implements Runnable {

    private int width;
    private int height;
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int size;
    public boolean infected;
    public int lifespan;
    public boolean isMoving;
    public Image image;
    Random random = new Random();
    public boolean isR;
    public Panel panel;
    public RepairShop rs;
    private Thread internalThread;
    private boolean queued;
    private Integer index = 0;

    public Phone(int width, int height, Panel panel, RepairShop rs, Image image, Integer index) {
        this.width = width;
        this.height = height;
        this.x = (int) (Math.random() * (width - 30));
        this.y = (int) (Math.random() * (height - 50));
        this.dx = random.nextInt(4);
        this.dy = random.nextInt(4);
        this.size = 50;
        this.infected = false;
        this.lifespan = 0;
        this.isMoving = false;
        this.panel = panel;
        this.rs = rs;
        this.queued = false;
        this.index = index;
    }

    public boolean collidesWith(Phone otherPhone) {
        int x1 = x + size / 2;
        int y1 = y + size / 2;
        int x2 = otherPhone.getX() + size / 2;
        int y2 = otherPhone.getY() + size / 2;
        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return distance < this.size;
    }

    public void move() {
        while (dx == 0) {
            dx = random.nextInt(4);
        }
        while (dy == 0) {
            dy = random.nextInt(4);
        }

        if (dx > 2) {
            dx = dx * -1;
            dx = dx + 2;
        }

        if (dy > 2) {
            dy = dy * -1;
            dy = dy + 2;
        }
        if (x > width || x < 0) {
            dx = dx * -1;
        }
        if (y > height || y < 0) {
            dy = dy * -1;
        }
        this.setX(x + dx);
        this.setY(y + dy);
    }
    
    public void setTimeout(Runnable runnable, int delay){
        internalThread = new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        });
        internalThread.start();
    }

    @Override
    public void run() {
        /**
         * To avoid race conditions, always use the getters for the attributes
         * 
         * Check if the phone is infected AND queued for processing. If it is,
         * then make it go to the repair shop. Otherwise, just keep moving.
         * 
         * This makes all other infected phones to just move until it is queued for processing.
         */
        if (isInfected() && isQueued()) {
            moveToRepairShop();
        } else {
            move();
        }
        setTimeout(this, 5);
    }

    public boolean isQueued() {
        return queued;
    }

    public void setQueued(boolean queued) {
        this.queued = queued;
    }

    public Integer getIndex() {
        return this.index;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDX() {
        return dx;
    }

    public int getDY() {
        return dy;
    }

    public void setDX(int dx) {
        this.dx = dx;
        if (dx != 0) {
            isMoving = false;
        }
    }

    public void setDY(int dy) {
        this.dy = dy;
        if (dy != 0) {
            isMoving = false;
        }
    }

    public int getSize() {
        return this.size;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setIsMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public void moveToRepairShop() {
        if (getX() < rs.getX()) {
            setDX(1);
        } else if (getX() > rs.getX()) {
            setDX(-1);
        } else {
            setDX(0);
        }

        if (getY() < rs.getY()) {
            setDY(1);
        } else if (getY() > rs.getY()) {
            setDY(-1);
        } else {
            setDY(0);
        }
        setX(getX() + getDX());
        setY(getY() + getDY());
        atRS();
    }

    public void atRS() {
        if (getX() == rs.getX() && getY() == rs.getY()) {
            rs.repair(this);
        }
    }
}
