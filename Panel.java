/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.util.ArrayDeque;
import java.util.ArrayList;


public class Panel extends JPanel implements KeyListener, ComponentListener {

    private ArrayList<Phone> phones;
    private ArrayList<Thread> threads;
    private int width = 1000;
    private int height = 600;
    private int maxLifespan = 500;
    private Image healthyImage;
    private Image infectedImage;
    private Image repairShopImage;
    private RepairShop repairShop;
    private Thread rThread;

    public Panel() {
        this.addKeyListener(this);
        this.addComponentListener(this);
        this.setFocusable(true);

        phones = new ArrayList<>();
        threads = new ArrayList<>();
        repairShop = new RepairShop(460, 250, this);

        /**
         * Run the repair shop thread to start monitoring infected phones
         */
        rThread = new Thread(repairShop);
        rThread.start();

        healthyImage = new ImageIcon("Cell_Phone_Healthy.png").getImage();
        infectedImage = new ImageIcon("Cell_Phone_Infected.png").getImage();
        repairShopImage = new ImageIcon("Repair_Shop.png").getImage();
    }

    public ArrayList<Phone> getPhones() {
        return phones;
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        g.drawImage(repairShopImage, repairShop.getX(), repairShop.getY(), this);
        handleCollisions();
        if (!phones.isEmpty()) {
            for (Phone phone : phones) {
                Image image = phone.isInfected() ? infectedImage : healthyImage;
                g.drawImage(image, phone.getX(), phone.getY(), this);
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_UP) {
            Phone phone = new Phone(width, height, this, repairShop, healthyImage, phones.size());
            Thread thread = new Thread(phone);
            thread.start();
            phones.add(phone);
            threads.add(thread);
        }

        /** Infect the phone manually by pressing V key */
        if (ke.getKeyCode() == KeyEvent.VK_V) {
            infectPhone();
        }
    }

    public void infectPhone() {
        int index = (int) (Math.random() * phones.size());
        Phone phone = phones.get(index);

        /**
         * To avoid race conditions on infection, we need to check first if
         * the phone in the generated random index is infected already.
         * 
         * If it is not infected, infect the phone. Otherwise, execute
         * the same function to generate another random index to infect.
         */
        if (!phone.isInfected()) {
            phone.setInfected(true);

            /** Once the phone has been infected, add the phone's index to the repair shop's queue */
            repairShop.addInfectedPhone(index);
        } else {
            infectPhone();
        }
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        width = this.getWidth();
        height = this.getHeight();
    }

    @Override
    public void componentShown(ComponentEvent es) {
        
    }

    @Override
    public void componentHidden(ComponentEvent es) {

    }

    @Override
    public void componentMoved(ComponentEvent es) {

    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    public void handleCollisions() {
        for (int i = 0; i < phones.size(); i++) {
            Phone phone1 = phones.get(i);
            for (int j = i + 1; j < phones.size(); j++) {
                Phone phone2 = phones.get(j);
                if (phone1.collidesWith(phone2)) {
                    if (phone1.isInfected() && !(phone1.getX() == repairShop.getX() && phone1.getY() == repairShop.getY())) {
                        if (!phone2.isInfected()) {
                            phone2.setInfected(true);
                            repairShop.addInfectedPhone(j);
                        }
                    } else if (phone2.isInfected() && !(phone2.getX() == repairShop.getX() && phone2.getY() == repairShop.getY())) {
                        if (!phone1.isInfected()) {
                            phone1.setInfected(true);
                            repairShop.addInfectedPhone(i);
                        }
                    }
//                    for collision:
//                    phone1.setDX(phone1.getDX() * -1);
//                    phone1.setDY(phone1.getDY() * -1);
//                    phone2.setDX(phone2.getDX() * -1);
//                    phone2.setDY(phone2.getDY() * -1);
                }
            }
        }
    }
}
