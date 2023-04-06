/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.Image;
import javax.swing.ImageIcon;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 *
 * @author snipi
 */
public class RepairShop implements Runnable {
    private int x;
    private int y;
    private boolean occupied;
    public Panel panel;
    private Image repair = new ImageIcon("Repair.png").getImage();
    private Image healthy = new ImageIcon("Cell_Phone_Healthy.png").getImage();
    private Deque<Integer> infectedPhones;
    
    public RepairShop(int x, int y, Panel panel) {
        this.x = x;
        this.y = y;
        this.occupied = false;
        this.panel = panel;
        this.infectedPhones = new ArrayDeque<>();
    }

    public void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

    @Override
    public void run() {
        /**
         * If the infected phones' queue is not empty and the repair shop is not
         * occupied, run the following process:
         */
        if (!infectedPhones.isEmpty() && !isOccupied()) {
            /** Get the head value of the infected phones' queue */
            Integer infectedPhoneIndex = infectedPhones.peek();

            /** Use the head value as the phone index to get the phone from the array list */
            Phone infectedPhone = this.panel.getPhones().get(infectedPhoneIndex);

            /**
             * To avoid race conditions, don't assume that the phone is still infected.
             * Always check if the phone is infected before running any processes.
             * 
             * If phone is infected, but have not been queued for processing, do the following:
             */
            if (infectedPhone.isInfected() && !infectedPhone.isQueued()) {
                /** Set the repair shop to occupied */
                setOccupied(true);

                /** Set the phone to be queued for processing */
                infectedPhone.setQueued(true);

                /** Remove the phone's index from the queue (this is always the head value) */
                infectedPhones.remove();
            }
        }

        setTimeout(this, 5);
    }
    
    public void repair(Phone phone) {
        /** Once the phone is repaired, set infected to false */
        phone.setInfected(false);
        phone.setLifespan(0);
        /** Set the phone's queue for processing flag to false */
        phone.setQueued(false);
        /** Free up the repair shop so it can take a new infected phone */
        setOccupied(false);
    }

    public void addInfectedPhone(Integer phoneIndex) {
        infectedPhones.add(phoneIndex);
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
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
}
