package com.Tank;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
/**
 * 这个类是主界面类，相当于大管家管理着所有的类
 * @author yandan
 *
 */
public class TankClient extends Frame {
    
    //常量定义public static final 类型
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
     * 需要添加物件画出来的必须弄在这个方法中用线程不断执行。
     * 动态的画面或者是判断方法都要在这里调用。
     * 坦克，子弹，墙，碰撞处理都要在这儿调用。
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
     * update方法主要是处理屏幕的闪烁问题，但是注意在这里必须调用paint方法。
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
     * launchFame()只是在主函数调用一遍，完成了窗口的设置（Frame框的设置），为坦克和子弹提供了
     * 活动场地的样子。另外有线程的启动和键盘监听器的添加
     */
    public void launchFrame() {
        for(int i=0; i<ENEMYCOUNT; i++) {
            tanks.add(new Tank(350*i , 50 ,false ,Tank.Direction.D, this));
        }
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setLocation(400, 300);
        this.setVisible(true);
        this.setTitle("TankWar");
        // 窗口监听加入一个类，现在加入一个匿名类，关闭窗口。
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
        this.setResizable(false);
        //this.setBackground(Color.darkGray);
        new Thread(new PaintThread()).start();
        //添加键盘监听器
        this.addKeyListener(new KeyMonitor());
    }

    /**
     * launchFame()只是在主函数调用一遍，完成了窗口的设置（Frame框的设置），为坦克和子弹提供了
     * 活动场地的样子。另外有线程的启动和键盘监听器的添加
     */
    
    public static void main(String[] args) {
        TankClient tc = new TankClient();
        tc.launchFrame();

    }
    /**
     * 线程类，这个内部类用于启动一个线程对画面进行不断地更新重画，这样坦克才能活动起来
     repaint()方法会调用update方法，update方法要调用paint方法。
     所以这个线程就会不断地重画界面，paint方法会不断地被调用
     * @author yandan
     *
     */
    private class PaintThread implements Runnable {
        // 线程启动后执行的就是run()方法内的操作
        public void run() {
            // 新建一个线程用于不断地重画刷新图形，让坦克动起来
            // 这个线程是和主线程同时进行的
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
     *键盘监听类Adapter，用于控制坦克的移动。监听类一般是用继承adapter类，这样你只要重写你所需要的方法即可 
     * @author yandan
     * 按键后执行这个方法，按键产生的信息封装在KeyEvent中，
     * 这个方法调用对象的keyPressed方法，在按键信息类作为参数传递到对象类中去让对象（坦克）自己去处理
     */
    private class KeyMonitor extends KeyAdapter {
        
        //键盘抬起
        public void keyReleased(KeyEvent e) {
           myTank.keyReleased(e);
        }
        public void keyPressed(KeyEvent e) {
            //坦克移动
                myTank.keyPressed(e);
            }
            
        }
    }
    
