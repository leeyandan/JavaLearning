package com.Tank;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
/**
 * ��������������࣬�൱�ڴ�ܼҹ��������е���
 * @author yandan
 *
 */
public class TankClient extends Frame {
    
    //��������public static final ����
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final int ENEMYCOUNT = 3; 
    
    Tank myTank = new Tank(300,570,true  ,Tank.Direction.STOP,this);
     int killNum = 0;
    List<Explode> explodes = new ArrayList<Explode>();
    List<Missile> missiles = new ArrayList<Missile>();
    List<Tank> tanks = new ArrayList<Tank>();
    Wall w1=new Wall(0,200,200,20,this) , w2 =new Wall(400,200,200,20,this);
    Wall w3=new Wall(200,400,200,20,this) , w4 =new Wall(600,400,200,20,this);
    Missile m = null;
    Blood b = new Blood();
    Image offScreenImage = null;
    /**
     * ��Ҫ�������������ı���Ū��������������̲߳���ִ�С�
     * ��̬�Ļ���������жϷ�����Ҫ��������á�
     * ̹�ˣ��ӵ���ǽ����ײ����Ҫ��������á�
     */
    public void paint(Graphics g) {
        if(myTank.isLive())
            myTank.draw(g);
        myTank.eatBlood(b);
        myTank.collidesWithTanks(tanks);
        w1.draw(g);
        w2.draw(g);
        w3.draw(g);
        w4.draw(g);
       for(int i=0;i<missiles.size();i++) {
           Missile m=missiles.get(i);
           m.hitWall(w1);
           m.hitWall(w2);
           m.hitWall(w3);
           m.hitWall(w4);
           m.hitTanks(tanks);
           m.hitTank(myTank);
           m.draw(g);
       }
       myTank.collidesWithWall(w1);
       myTank.collidesWithWall(w2);
       myTank.collidesWithWall(w3);
       myTank.collidesWithWall(w4);
       for(int i=0 ; i<tanks.size(); i++ ) {
           Tank t = tanks.get(i);
           t.collidesWithWall(w1);
           t.collidesWithWall(w2);
           t.collidesWithWall(w3);
           t.collidesWithWall(w4);
           t.collidesWithTanks(tanks);
           t.draw(g);
       }
       if(tanks.size()<=0) {
           for(int i=0; i<ENEMYCOUNT; i++) {
               tanks.add(new Tank(350*i , 50 ,false ,Tank.Direction.D, this));
           }
       }
       for(int i=0;i<explodes.size(); i++) {
           Explode e = explodes.get(i);
           e.draw(g);
       }
       Color temp =g.getColor();
       g.setColor(Color.cyan);
       //g.drawString("missiles count: "+missiles.size(), 10,50 );
       //g.drawString("explodes count: "+explodes.size(), 10,70 );
       g.drawString("Tank killed: "+killNum, 10, 50);
       g.drawString("Tank life: "+myTank.getLife(), 10, 70);
       g.setColor(temp);
       b.draw(g);
       if(!b.isLive()) {
           b.move();
           b.setLive(true);
       }
    }
    /**
     * update������Ҫ�Ǵ�����Ļ����˸���⣬����ע��������������paint������
     */
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.DARK_GRAY);
        gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);

    }
    /**
     * launchFame()ֻ��������������һ�飬����˴��ڵ����ã�Frame������ã���Ϊ̹�˺��ӵ��ṩ��
     * ����ص����ӡ��������̵߳������ͼ��̼����������
     */
    public void launchFrame() {
        for(int i=0; i<ENEMYCOUNT; i++) {
            tanks.add(new Tank(350*i , 50 ,false ,Tank.Direction.D, this));
        }
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setLocation(400, 300);
        this.setVisible(true);
        this.setTitle("TankWar");
        // ���ڼ�������һ���࣬���ڼ���һ�������࣬�رմ��ڡ�
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
        this.setResizable(false);
        //this.setBackground(Color.darkGray);
        new Thread(new PaintThread()).start();
        //��Ӽ��̼�����
        this.addKeyListener(new KeyMonitor());
    }

    /**
     * launchFame()ֻ��������������һ�飬����˴��ڵ����ã�Frame������ã���Ϊ̹�˺��ӵ��ṩ��
     * ����ص����ӡ��������̵߳������ͼ��̼����������
     */
    
    public static void main(String[] args) {
        TankClient tc = new TankClient();
        tc.launchFrame();

    }
    /**
     * �߳��࣬����ڲ�����������һ���̶߳Ի�����в��ϵظ����ػ�������̹�˲��ܻ����
     repaint()���������update������update����Ҫ����paint������
     ��������߳̾ͻ᲻�ϵ��ػ����棬paint�����᲻�ϵر�����
     * @author yandan
     *
     */
    private class PaintThread implements Runnable {
        // �߳�������ִ�еľ���run()�����ڵĲ���
        public void run() {
            // �½�һ���߳����ڲ��ϵ��ػ�ˢ��ͼ�Σ���̹�˶�����
            // ����߳��Ǻ����߳�ͬʱ���е�
            while (true) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }
    /**
     *���̼�����Adapter�����ڿ���̹�˵��ƶ���������һ�����ü̳�adapter�࣬������ֻҪ��д������Ҫ�ķ������� 
     * @author yandan
     * ������ִ�����������������������Ϣ��װ��KeyEvent�У�
     * ����������ö����keyPressed�������ڰ�����Ϣ����Ϊ�������ݵ���������ȥ�ö���̹�ˣ��Լ�ȥ����
     */
    private class KeyMonitor extends KeyAdapter {
        
        //����̧��
        public void keyReleased(KeyEvent e) {
           myTank.keyReleased(e);
        }
        public void keyPressed(KeyEvent e) {
            //̹���ƶ�
                myTank.keyPressed(e);
            }
            
        }
    }
    
