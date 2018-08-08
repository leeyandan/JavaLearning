package com.Tank;
import java.awt.*;
import java.util.List;

/**
 * 一个类的注释
 * @author yandan
 *
 */
public class Missile {

        private static final int XSPEED = 20;
        private static final int YSPEED = 20;
        private static final int MISSILEWIDTH = 10 ;
        private static final int MISSILEHEIGHT = 10;

        int x , y;
        private boolean live = true;
        private boolean good;
        
        Tank.Direction dir;
        private TankClient tc;
          
          public Missile(int x, int y, boolean good,Tank.Direction dir) {
                this.x = x;
                this.y = y;
                this.dir = dir;
                this.good=good;
          }
          
          public Missile(int x,int y,boolean good,Tank.Direction dir,TankClient tc) {
              this(x,y,good,dir);
              this.tc = tc;
          }
          
          public void draw(Graphics g) {
              if(!live) {
                  tc.missiles.remove(this);
                  return;
              
              }
              
              Color c = g.getColor();
              if(good)
                  g.setColor(Color.RED);
              else
                   g.setColor(Color.BLACK);
              g.fillOval(x-MISSILEWIDTH/2, y-MISSILEHEIGHT/2, MISSILEWIDTH,MISSILEHEIGHT);
              g.setColor(c);
              
              move();
          
          }
          

        private void move() {
            switch (dir) {
            case L:
                x-=XSPEED;
                break;
            case LU:
                x-=XSPEED;
                y-=YSPEED;
                break;
            case U:
                y-=YSPEED;
                break;
            case RU:
                x+=XSPEED;
                y-=YSPEED;
                break;
            case R:
                x+=XSPEED;
                break;
            case RD:
                x+=XSPEED;
                y+=YSPEED;
                break;
            case D:
                y+=YSPEED;
                break;
            case LD:
                x-=XSPEED;
                y+=YSPEED;
                break;
            }
            if(x<0 || y<0 || x> TankClient.GAME_WIDTH || y>TankClient.GAME_HEIGHT) {
                live = false;
            }
        }
          
        public Rectangle getRect() {
            return new Rectangle(x,y,MISSILEWIDTH,MISSILEHEIGHT);
        }
        
        //碰撞有待完善，这个只是两个矩形的碰撞
        public boolean hitTank(Tank t) {
            if(this.getRect().intersects(t.getRect()) && t.isLive() && this.good!=t.isGood() && this.live) {
                if(t.isGood()) {
                    t.setLife(t.getLife() - 20);
                    if(t.getLife()<=0)
                        t.setLive(false);     
                }
                else {
                    t.setLive(false);
                }
                this.live=false;
                Explode e = new Explode(x,y,tc);
                tc.explodes.add(e);
                return true;
            }
            return false;
        }
        public boolean hitTanks(List<Tank> tanks) {
            for(int i=0; i<tanks.size(); i++) {
                if(hitTank(tanks.get(i)))
                    //tanks.remove(i);
                    return true;
            }
            return false;
        }
        
        public boolean isLive() {
          return live;
      }
        
        public boolean hitWall(Wall w) {
            if(this.live && this.getRect().intersects(w.getRect())) {
                this.live = false;
                return true;
            }
            return false;
        }

}
