package mazeoblig;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;

public class NoMoreFlickering extends Applet
{
    Image offScreenBuffer;
    public void update(Graphics g)
    {
    Graphics gr; 
    if (offScreenBuffer==null ||
          (! (offScreenBuffer.getWidth(this) == this.size().width
          && offScreenBuffer.getHeight(this) == this.size().height)))
    {
        offScreenBuffer = this.createImage(size().width, size().height);
    }
    gr = offScreenBuffer.getGraphics();
    paint(gr); 
    g.drawImage(offScreenBuffer, 0, 0, this);     
    }
}