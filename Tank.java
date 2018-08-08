package com.Tank;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.DirectoryIteratorException;
import java.util.*;
/**
 * 坦克类，这个包含了主站坦克和机器人坦克
 * 
 * @author yandan
 *
 */
public class Tank {
    
    //这些常量是静态的，这样在类外用类名.常量名来引用
    private static final int XSPEED = 10;
    private static final int YSPEED = 10;
    private static final int TANKWIDTH = 30;
    private static final int TANKHEIGHT = 30;
    private static final int TITLE_WIDTH = 25;
    
    //随机数产生器一个就够了，所有是static
    private static Random r= new Random();
    private int x , y;
    private int oldX , oldY;
    private boolean bL = false , bD =false , bU = false, bR = false;
    TankClient tc;
    private int life = 100;
    private BloodBar bb =new BloodBar();
    
    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }
    private boolean good;
    private boolean live=true;
    //枚举类型其实就是定义了一种类型的数据，比如这个是定义了一种数据类型 Direction，只能有9个取值
    enum Direction { L , LU , U , RU , R , RD , D , LD , STOP     };
    private Direction dir =Direction. STOP ;
    private Direction ptDir = Direction.U;   
    private int step=r.nextInt(13)+3;   
        
        public Tank(int x, int y , boolean good) {
           this.x = x;
           this.y = y;
           oldX = x;
           oldY = y;
           this.good=good;
       } 
       
       public Tank(int x , int y ,boolean good ,Direction dir, TankClient tc) {
           this(x,y , good);
           this.tc = tc;
           this.dir=dir;
       }
       
       public boolean isLive() {
           return live;
       }

       public void setLive(boolean live) {
           this.live = live;
       }

      public void draw(Graphics g) {
         if(!live) {
            if(!good) {
                tc.killNum++;
                tc.tanks.remove(this);
            }
            return;
         }
          Color c = g.getColor();
          if(isGood()) g.setColor(Color.ORANGE);
          else g.setColor(Color.BLUE);
          g.fillOval(x, y,TANKWIDTH,TANKHEIGHT);
          // Fills an oval bounded by the specified rectangle with the current color.
          
          //画一个炮筒
          g.setColor(Color.BLACK);
          switch (ptDir) {
          case L:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x, y+TANKHEIGHT/2);
              break;
          case LU:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x, y);
              break;
          case U:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x+TANKHEIGHT/2, y);
              break;
          case RU:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x+TANKWIDTH, y);
              break;
          case R:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x+TANKWIDTH, y+TANKHEIGHT/2);
              break;
          case RD:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x+TANKWIDTH, y+TANKHEIGHT);
              break;
          case D:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x+TANKWIDTH/2, y+TANKHEIGHT);
              break;
          case LD:
              g.drawLine(x+TANKWIDTH/2, y+TANKHEIGHT/2, x, y+TANKHEIGHT);
              break;
          }
          
          g.setColor(c);
          if(good)
              bb.draw(g);
          move();
      }
      
      private void move() {
          
          this.oldX = x;
          this.oldY = y;
          //移动的话，炮筒改变。不动（STOP）的话炮筒不变。
          if(dir!=Direction.STOP)
              ptDir=dir;
          switch (dir) {
          case L:
              if((x-XSPEED)>=0)
                     x-=XSPEED;
              break;
          case LU:
             if((x-XSPEED)>=0)
                 x-=XSPEED;
             //20是标题栏占的像素
             if((y-YSPEED-TITLE_WIDTH)>=0)
                 y-=YSPEED;
              break;
          case U:
              if((y-YSPEED-TITLE_WIDTH)>=0)
                  y-=YSPEED;
              break;
          case RU:
              if((x+TANKWIDTH+XSPEED)<=tc.GAME_WIDTH)
                  x+=XSPEED;
              if((y-YSPEED-TITLE_WIDTH)>=0)
                  y-=YSPEED;
              break;
          case R:
              if((x+TANKWIDTH+XSPEED)<=tc.GAME_WIDTH)
                  x+=XSPEED;
              break;
          case RD:
              if((x+TANKWIDTH+XSPEED)<=tc.GAME_WIDTH)
                  x+=XSPEED;
              if((y+TANKHEIGHT+YSPEED)<=tc.GAME_HEIGHT)
                  y+=YSPEED;
              break;
          case D:
              if((y+TANKHEIGHT+YSPEED)<=tc.GAME_HEIGHT)
                  y+=YSPEED;
              break;
          case LD:
              if((x-XSPEED)>=0)
                  x-=XSPEED;
              if((y+TANKHEIGHT+YSPEED)<=tc.GAME_HEIGHT)
                  y+=YSPEED;
              break;
          case STOP:
              break;
          }
         if(!isGood()) {
             //将enum的值转换为数组
             Direction[] dirs=Direction.values();
             if(step==0) {
                 step=r.nextInt(13)+3;
                 int randomNum = r.nextInt(dirs.length);
                 dir = dirs[randomNum];  
                 tc.missiles.add(this.fire());
             }
             step--;

         }
          
      }
      private void stay() {
          x = this.oldX;
          y = this.oldY;
      }
      public void keyPressed(KeyEvent e) {
          int key = e.getKeyCode();
          switch(key) {
          case KeyEvent.VK_F2:
              if(!this.live) {
                  this.live = true;
                  this.life = 100;
              }
              break;
          case KeyEvent.VK_RIGHT:
              bR = true;
              break;
          case KeyEvent.VK_DOWN:
              bD = true;
              break;
          case KeyEvent.VK_LEFT:
              bL = true;
              break;
          case KeyEvent.VK_UP:
              bU = true;
              break;
          }
          //System.out.println("x="+x+" y="+y);
          locateDirection();
    
          
          //测试bug还是能输出变量的值来测试就输出变量的值来测试
          //System.out.println("bU= "+bU+"  bD= "+bD+" bL="+bL+" bR="+bR);
          //System.out.println("dir= "+dir);
      }
      private void locateDirection() {
          if(bL && !bU && !bR && !bD)     dir = Direction.L;
          else if(!bL && bU && !bR && !bD)     dir = Direction.U;
          else if(!bL && !bU && bR && !bD)     dir = Direction.R;
          else if(!bL && !bU && !bR && bD)     dir = Direction.D;
          else if(bL && !bU && !bR && bD)     dir = Direction.LD;
          else if(!bL && !bU && bR && bD)     dir = Direction.RD;
          else if(bL && bU && !bR && !bD)     dir = Direction.LU;
          else if(!bL && bU && bR && !bD)     dir = Direction.RU;
          else if(!bL && !bU && !bR && !bD)     dir = Direction.STOP;
      }

      public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key) {
        case KeyEvent.VK_RIGHT:
            bR = false;
            break;
        case KeyEvent.VK_DOWN:
            bD = false;
            break;
        case KeyEvent.VK_LEFT:
            bL = false;
            break;
        case KeyEvent.VK_UP:
            bU = false;
            break;
            }
        if(live && key==KeyEvent.VK_CONTROL)
            tc.missiles.add(fire());
        if(live && key==KeyEvent.VK_A)
            superFire();
            locateDirection();
    }
      
       public Missile fire() {
           if(!live)
               return null;
           Missile m = new Missile(x+TANKWIDTH/2,y+TANKHEIGHT/2, good,ptDir , this.tc);
           return m;
       }
       
       public Missile fire(Direction dir) {
           if(!live)
               return null;
           Missile m = new Missile(x+TANKWIDTH/2,y+TANKHEIGHT/2, good,dir , this.tc);
           return m;
       }
       
       
       public Rectangle getRect() {
           return new Rectangle(x,y,TANKWIDTH,TANKHEIGHT);
       }

    public boolean isGood() {
        return good;
    }
    
    public boolean collidesWithWall (Wall w) {
        if(this.live && this.getRect().intersects(w.getRect())) {
                this.stay();
            return true;
        }
        return false;
    }
    
    public boolean collidesWithTanks(java.util.List<Tank> tanks) {
        for(int i=0;i<tanks.size() ;i++) {
            Tank t=tanks.get(i);
            if(t!=this) {
                if(this.live && t.isLive() && this.getRect().intersects(t.getRect())) {
                    this.stay();
                    t.stay();
                return true;
                }
            }
        }
        return false;
    }
    private void superFire() {

        if(!live)
            return;
        Direction[] dirs = Direction.values();
        for(int i = 0; i<8; i++) {
            tc.missiles.add(fire(dirs[i]));
        }
        
    }
    
    //内部类
    private class BloodBar{
        
        public void draw(Graphics g) {
            Color c=g.getColor();
            g.setColor(Color.RED);
            g.drawRect(x, y-10, TANKWIDTH, 10);
            int w = TANKWIDTH*life/100;
            g.fillRect(x, y-10, w, 10);
            g.setColor(c);
        }
    }
    public boolean eatBlood(Blood b) {
        if(this.getRect().intersects(b.getRect()) && b.isLive()  && this.live &&this.good ) {
            this.life=100;
            b.setLive(false);
            return true;
            }
        else
            return false;
           
    }
}
