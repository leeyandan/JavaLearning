package com.Tank;

import java.awt.*;
public class Blood {
      
        int x , y , w , h;
        TankClient tc;
        
        private int[][] pos = { {100,120} ,{270,220} ,{480,320} ,{330,500} ,{640,120} ,{240,400} ,{750,320}
                                            };
        private int step = 0;
        private boolean live = true;
        
        public Blood() {
            x = pos[0][0];
            y = pos[0][1];
            w=h=15;
        }
        
        public boolean isLive() {
            return live;
        }
        public void setLive(boolean live) {
            this.live = live;
        }
        public void draw(Graphics g) {
            if(!live)
                return;
            Color c = g.getColor();
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, w, h);
            g.setColor(c);
            
        }
       
        public void move() {
            step++;
            if(step==pos.length) {
                step=0;
            }
            x = pos[step][0];
            y = pos[step][1];
        }
        
        public Rectangle getRect() {
            return new Rectangle(x , y ,w,h);
        }
}
