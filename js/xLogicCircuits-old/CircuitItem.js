class CircuitItem {
    constructor() {
        this.on = false;
        this.selected = false;
        this.boundingBox = new FloatRect();
    }
    hit(x, y) {
       return this.boundingBox.inside(x,y);
    }
    reshape(x, y, width, height) {
        this.boundingBox.reshape(x,y,width,height);
    }
    move(x, y) {
         this.reshape(x,y,this.boundingBox.width,this.boundingBox.height);
    }
    drawWithLines(g) {
        this.draw(g);
    }
    selectConnectedLines(select) {
    }
    powerOff() {
        this.on = false;
    }
    getLineSource(x, y) {
        return null;
    }
    getLineDestination(x, y) {
        return null;
    }
    compute() {
        return false;
    }  // returns true if anything visible element changed
    dragTo(x, y, circuitBounds) { // called ONLY when dragging item that is on circuit (not in scroller)
      if (x + this.boundingBox.width > circuitBounds.x + circuitBounds.width - 15)
         x = circuitBounds.x + circuitBounds.width - 15 - this.boundingBox.width;
      if (y + this.boundingBox.height > circuitBounds.y + circuitBounds.height - 15)
         y = circuitBounds.y + circuitBounds.height - 15 - this.boundingBox.height;
      x = Math.max(x, circuitBounds.x + 15);
      y = Math.max(y, circuitBounds.y + 15);
      this.reshape(x,y,this.boundingBox.width,this.boundingBox.height);
   }
   getCopyOfBoundingBox(addInLines) {
   }
   draw(g) {
   }
   copy() {
   }
   delete(owner) {
   }
   unDelete(owner) {
   }
}

class FloatRect {
   constructor( x = 0, y = 0, width = 0, height = 0) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }
   FloatRect(x, y, width, height) {
      this.reshape(x,y,width,height);
   }
   reshape(x,y,width,height) {
      this.x = x;
      this.y = y;
      this.width = Math.max(0,width);
      this.height = Math.max(0,height);
   }
   getIntRect() {
      return new FloatRect(Math.round(this.x),Math.round(this.y),
                           Math.round(this.width),Math.round(this.height));
   }
   inside(a, b) {
      return (a >= this.x && b >= this.y && a < this.x+this.width && b < this.y+this.height);
   }
   add(a, b) {
      if (a instanceof FloatRect) {
         this.add(r.x,r.y);
         this.add(r.x+r.width, r.y+r.height);
      }
      else {
         if (a < this.x) {
            this.width += this.x - a;
            this.x = a;
         }
         else if (a > this.x+this.width)
            this.width = a - this.x;
         if (b < this.y) {
            this.height += this.y - b;
            this.y = b;
         }
         else if (b > this.y+this.height)
            this.height = b - this.y;
      }
   }
   grow(dx, dy) {
      this.x -= dx;
      this.y -= dy;
      this.width += 2*dx;
      this.height += 2*dy;
   }
}

class Vector {
    constructor() {
        this.elements = [];
    }
    elementAt(i) {
        if (i < 0 || i >= this.elements.length)
            throw new Error("Index out of range");
        return this.elements[i];
    }
    addElement(x) {
        this.elements.push(x);
    }
    setElementAt(x,i) {
        this.elements[i] = x;
    }
    removeElement(x) {
        let i = this.elements.indexOf(x);
        if (i >= 0)
            this.elements.splice(i,1);
    }
    indexOf(x) {
        return this.elements.indexOf(x);
    }
    size() {
        return this.elements.length;
    }
}

class Line extends CircuitItem {
   constructor (source, destination) {
      super();
      this.source = source;
      this.destination = destination;
      this.changed = false; // for use in deciding whether to draw this
      this.pos = -1;  // position in vector; just for use in Circuit.copy() and saving files
      if (source != null) {
         source.destination.addElement(this);
         this.on = source.on;
      }
      if (destination != null) {
         destination.source = this;
         destination.on = this.on;
      }
      this.setBoundingBox();
   }
   setBoundingBox() {
      if (this.source != null && this.destination != null) {
         this.boundingBox.reshape(this.source.connect_x,this.source.connect_y,1,1);
         this.boundingBox.add(this.destination.connect_x,this.destination.connect_y);
      }
   }
   draw(g) {
      g.beginPath();
      g.moveTo(this.source.connect_x,this.source.connect_y);
      g.lineTo(this.destination.connect_x,this.destination.connect_y);
      if (this.selected) {
          g.stroke();
          g.strokeStyle = "white";
          g.lineWidth = 6;
          g.stroke();
          g.lineWidth = 2;
      }
      if (this.on)
         if (this.selected)
            g.strokeStyle = "magenta";
         else
             g.strokeStyle = "red";
      else
         if (this.selected)
            g.strokeStyle = "blue";
         else
            g.strokeStyle = "black";
      g.stroke();
   }
   compute() {  // called only by source IONub; passes on value to destination
      this.changed = (this.on != this.source.on);
      this.on = this.source.on;
      return this.changed;
   }
   copy() {  // copied without source and destination
      let it = new Line(null,null);
      it.selected = this.selected;
      it.on = this.on;
      it.reshape(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      return it;
   }
   getCopyOfBoundingBox(addInLines) { 
       let r = new FloatRect( Math.round(this.boundingBox.x),Math.round(this.boundingBox.y),
                            Math.round(this.boundingBox.width),Math.round(thils.boundingBox.height) ); 
       r.grow(2,2);
       return r;
   }
   hit(x, y) { 
      this.boundingBox.grow(3,3);
      let firstPass = this.boundingBox.inside(x,y);
      this.boundingBox.grow(-3,-3);
      if (!firstPass)
         return false;
      let ny = this.source.connect_x - this.destination.connect_x;
      let nx = -(this.source.connect_y - this.destination.connect_y);
      if (nx == 0 || ny == 0)
         return true;
      let len = nx*(x-this.source.connect_x) + ny*(y-this.source.connect_y);
      let dist = Math.abs(len / Math.sqrt(nx*nx + ny*ny));
      return (dist <= 3);
   }
   delete(owner) {
      this.on = false;
      owner.lines.removeElement(this);
      if (this.source != null)
         this.source.destination.removeElement(this);
      if (this.destination != null) {
         this.destination.source = null;
         this.destination.on = false;
      }
   }
   unDelete(owner) {
      owner.lines.addElement(this);
      if (this.source != null)
         this.source.destination.addElement(this);
      if (this.destination != null) {
         this.destination.source = this;
         this.destination.on = this.on;
      }
   }
}

class IONub extends CircuitItem {
   constructor() {
       super();
       this.kind = 0;
       this.connect_x = 0;
       this.connect_y = 0;
       this.source = null;
       this.changed = false;
       this.destination = new Vector();
   }
   drawWithLines(g) { 
      this.draw(g);
      if (this.source != null && this.kind != IONub.INPUT)  // don't draw source line for inputs, because the line is in the containing circuit
         source.draw(g);
      if (this.kind != IONub.OUTPUT)  // for outputs, destination lines are in containing circuit
         for (let i = 0; i < this.destination.size(); i++)
            this.destination.elementAt(i).draw(g);
   }
   selectConnectedLines(select) { 
      if (this.source != null)
         this.source.selected = select;
      for (let i = 0; i < this.destination.size(); i++)
         this.destination.elementAt(i).selected = select;
   }
   getCopyOfBoundingBox(addInLines) { 
      let r = new FloatRect(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      if (addInLines) {
         if (this.source != null)
            r.add(this.source.boundingBox);
         for (let i = 0; i < this.destination.size(); i++)
            r.add(this.destination.elementAt(i).boundingBox);
      }
      r.grow(3,3);
      return r.getIntRect();
   }
   getLineSource(x, y) {
      if (this.kind === IONub.OUTPUT)
         return null;
      if (x < this.boundingBox.x - 2 || x > this.boundingBox.x + this.boundingBox.width + 2 ||
             y < this.boundingBox.y - 2|| y > this.boundingBox.y + this.boundingBox.height + 2)
         return null;
      return this;
   }
   getLineDestination(x, y) {
      if (this.kind === IONub.INPUT || this.source != null)
         return null;
      if (x < this.boundingBox.x - 2 || x > this.boundingBox.x + this.boundingBox.width + 2 ||
             y < this.boundingBox.y - 2|| y > this.boundingBox.y + this.boundingBox.height + 2)
         return null;
      return this;
   }
}
IONub.INPUT = 0;
IONub.OUTPUT = 1;
IONub.TACK = 2;
IONub.lineDestinationColor = "#008800";
IONub.lineSourceColor = "#880088";


class Tack extends IONub {
   constructor() {
      super();
      this.kind = IONub.TACK;
   }
   draw(g) {
      if (this.selected) {
         g.fillStyle = "blue";
         g.beginPath();
         g.arc(Math.round(this.boundingBox.x+this.boundingBox.width/2), 
                Math.round(this.boundingBox.y+this.boundingBox.height/2), 
                Math.round(this.boundingBox.width/2+2), 0, 2 * Math.PI);
         g.fill(); 
      }
      if (this.source === null)
         g.fillStyle = IONub.lineDestinationColor;
      else
         g.fillStyle = IONub.lineSourceColor;
      g.beginPath();
      g.arc(Math.round(this.boundingBox.x+this.boundingBox.width/2), 
             Math.round(this.boundingBox.y+this.boundingBox.height/2), 
             Math.round(this.boundingBox.width/2), 0, 2 * Math.PI);
      g.fill(); 
   }
   copy() {  // copied without source and destination
      let it = new Tack();
      it.selected = this.selected;
      it.on = this.on;
      it.reshape(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      it.kind = this.kind;
      it.connect_x = this.connect_x;
      it.connect_y = this.connect_y;
      return it;
   }
   reshape(x, y, width, height) { 
      this.boundingBox.reshape(x,y,width,height); 
      this.connect_x = Math.round(x)+3.5;
      this.connect_y = Math.round(y)+3.5;
      if (this.source != null)
         this.source.setBoundingBox();
      for (let i = 0; i < this.destination.size(); i++)
         this.destination.elementAt(i).setBoundingBox();
   }
   delete(owner) {
     owner.items.removeElement(this);
     for (let i = 0; i < this.destination.size(); i++) {
        let line = this.destination.elementAt(i);
        owner.lines.removeElement(line);
        line.destination.source = null;           
     }
     if (this.source !== null) {
        owner.lines.removeElement(this.source);
        this.source.source.destination.removeElement(this.source);
     }
   }
   unDelete(owner) {
     owner.items.addElement(this);
     for (let i = 0; i < this.destination.size(); i++) {
        let line = this.destination.elementAt(i);
        owner.lines.addElement(line);
        line.destination.source = line;           
     }
     if (this.source !== null) {
        owner.lines.addElement(this.source);
        this.source.source.destination.addElement(this.source);
     }
   }
}


class CircuitIONub extends IONub {

   constructor(side, position, isInput) {
      super();
      this.side = side;
      this.absolutePosition = position;
      this.kind = (isInput? IONub.INPUT : IONub.OUTPUT);
   }
   
   drawLine(g,x1,y1,x2,y2) {
       g.beginPath();
       g.moveTo(x1,y1);
       g.lineTo(x2,y2);
       g.stroke();
   }
   
   fillOval(g,x,y,w,h) {
       g.beginPath();
       g.arc(x+w/2,y+h/2,w/2,0,2*Math.PI);
       g.fill();
   }
   
   putNub(g, pos, source) {
      switch (pos) {
         case CircuitIONub.ONTOP: this.drawLine(g,this.connect_x,this.connect_y+1,this.connect_x,this.connect_y+4); break;
         case CircuitIONub.ONLEFT: this.drawLine(g,this.connect_x+1,this.connect_y,this.connect_x+4,this.connect_y); break;
         case CircuitIONub.ONRIGHT: this.drawLine(g,this.connect_x-4,this.connect_y,this.connect_x-1,this.connect_y); break;
         case CircuitIONub.ONBOTTOM: this.drawLine(g,this.connect_x,this.connect_y-4,this.connect_x,this.connect_y-1); break;
      }
      if (source) {
         switch (pos) {
            case CircuitIONub.ONTOP:this.drawLine(g,this.connect_x-1,this.connect_y+1,this.connect_x,this.connect_y); 
                         this.drawLine(g,this.connect_x,this.connect_y,this.connect_x+1,this.connect_y+1); break;
            case CircuitIONub.ONLEFT:this.drawLine(g,this.connect_x+1,this.connect_y-1,this.connect_x,this.connect_y); 
                         this.drawLine(g,this.connect_x,this.connect_y,this.connect_x+1,this.connect_y+1); break;
            case CircuitIONub.ONRIGHT: this.drawLine(g,this.connect_x-1,this.connect_y-1,this.connect_x,this.connect_y); 
                         this.drawLine(g,this.connect_x,this.connect_y,this.connect_x-1,this.connect_y+1); break;
            case CircuitIONub.ONBOTTOM: this.drawLine(g,this.connect_x-1,this.connect_y-1,this.connect_x,this.connect_y); 
                        this.drawLine(g,this.connect_x,this.connect_y,this.connect_x+1,this.connect_y-1); break;
         }
      }
      else {
         switch (pos) {
            case CircuitIONub.ONTOP:this.drawLine(g,this.connect_x-1,this.connect_y,this.connect_x,this.connect_y+1); 
                         this.drawLine(g,this.connect_x,this.connect_y+1,this.connect_x+1,this.connect_y); break;
            case CircuitIONub.ONLEFT:this.drawLine(g,this.connect_x,this.connect_y-1,this.connect_x+1,this.connect_y); 
                         this.drawLine(g,this.connect_x+1,this.connect_y,this.connect_x,this.connect_y+1); break;
            case CircuitIONub.ONRIGHT: this.drawLine(g,this.connect_x-1,this.connect_y,this.connect_x,this.connect_y-1); 
                         this.drawLine(g,this.connect_x-1,this.connect_y,this.connect_x,this.connect_y+1); break;
            case CircuitIONub.ONBOTTOM: this.drawLine(g,this.connect_x-1,this.connect_y,this.connect_x,this.connect_y-1); 
                        this.drawLine(g,this.connect_x,this.connect_y-1,this.connect_x+1,this.connect_y); break;
         }
      }
   }
   
   drawIconified(g) {
      if (this.kind === IONub.INPUT)
         g.strokeStyle = IONub.lineDestinationColor;
      else
         g.strokeStyle = IONub.lineSourceColor;
      this.putNub(g,this.side,this.kind === IONub.OUTPUT);
   }
   
   drawCenter(g) {
      if (this.on)
         g.fillStyle = "red";
      else
         g.fillStyle = "black";
      switch (this.side) {
         case CircuitIONub.ONTOP: this.fillOval(g,Math.round(this.boundingBox.x)+2,Math.round(this.boundingBox.y)+2,Math.round(this.boundingBox.width)-4,Math.round(this.boundingBox.height)-7); break;
         case CircuitIONub.ONLEFT: this.fillOval(g,Math.round(this.boundingBox.x)+2,Math.round(this.boundingBox.y)+2,Math.round(this.boundingBox.width)-7,Math.round(this.boundingBox.height)-4); break;
         case CircuitIONub.ONRIGHT: this.fillOval(g,Math.round(this.boundingBox.x)+5,Math.round(this.boundingBox.y)+2,Math.round(this.boundingBox.width)-7,Math.round(this.boundingBox.height)-4); break;
         case CircuitIONub.ONBOTTOM: this.fillOval(g,Math.round(this.boundingBox.x)+2,Math.round(this.boundingBox.y)+5,Math.round(this.boundingBox.width)-4,Math.round(this.boundingBox.height)-7); break;
      }
   }
   
   draw(g) {
      if (this.kind === IONub.OUTPUT) {
         g.fillStyle = IONub.lineDestinationColor;
         g.strokeStyle = IONub.lineDestinationColor;
      }
      else {
         g.fillStyle = IONub.lineSourceColor;
         g.strokeStyle = IONub.lineSourceColor;
      }
      switch (this.side) {
         case CircuitIONub.ONTOP: this.fillOval(g,Math.round(this.boundingBox.x),Math.round(this.boundingBox.y),Math.round(this.boundingBox.width),Math.round(this.boundingBox.height)-3); break;
         case CircuitIONub.ONLEFT: this.fillOval(g,Math.round(this.boundingBox.x),Math.round(this.boundingBox.y),Math.round(this.boundingBox.width)-3,Math.round(this.boundingBox.height)); break;
         case CircuitIONub.ONRIGHT: this.fillOval(g,Math.round(this.boundingBox.x)+3,Math.round(this.boundingBox.y),Math.round(this.boundingBox.width)-3,Math.round(this.boundingBox.height)); break;
         case CircuitIONub.ONBOTTOM: this.fillOval(g,Math.round(this.boundingBox.x),Math.round(this.boundingBox.y)+3,Math.round(this.boundingBox.width),Math.round(this.boundingBox.height)-3); break;
      }
      this.putNub(g,(this.side+2)%4,this.kind === IONub.INPUT);
      this.drawCenter(g);
      if (this.selected) {
         g.strokeStyle = "blue";
         g.strokeRect(Math.round(this.boundingBox.x),Math.round(this.boundingBox.y),Math.round(this.boundingBox.width),Math.round(this.boundingBox.height));
      }
   }

   getCoordsIconified(x, y, width, height) {
      switch (this.side) {
         case CircuitIONub.ONTOP:
            this.connect_x = Math.round(x + 5 + (this.absolutePosition*(width-10)));
            this.connect_y = Math.round(y);
            break;
         case CircuitIONub.ONLEFT:
            this.connect_x = Math.round(x);
            this.connect_y = Math.round(y + 5 + (this.absolutePosition*(height-10)));
            break;
         case CircuitIONub.ONRIGHT:
            this.connect_x = Math.round(x + width);
            this.connect_y = Math.round(y + 5 + (this.absolutePosition*(height-10)));
            break;
         case CircuitIONub.ONBOTTOM:
            this.connect_x = Math.round(x + 5 + (this.absolutePosition*(width-10)));
            this.connect_y = Math.round(y + height);
            break;
      }
      if (this.kind === IONub.INPUT) {
        if (this.source != null)
           this.source.setBoundingBox();
      }
      else {
         for (let i = 0; i < this.destination.size(); i++)
            this.destination.elementAt(i).setBoundingBox();
      }
   }
   
   getCoords(x, y, width, height) {
      switch (this.side) {
         case CircuitIONub.ONTOP:
            this.connect_x = Math.round(x + 7.5 + (this.absolutePosition*(width-10)));
            this.connect_y = Math.round(y + 18);
            this.boundingBox.reshape(this.connect_x-7.5,this.connect_y-18,15,18);
            break;
         case CircuitIONub.ONLEFT:
            this.connect_x = Math.round(x + 18);
            this.connect_y = Math.round(y + 7.5 + (this.absolutePosition*(height-10)));
            this.boundingBox.reshape(this.connect_x-18,this.connect_y-7.5,18,15);
            break;
         case CircuitIONub.ONRIGHT:
            this.connect_x = Math.round(x + width - 18);
            this.connect_y = Math.round(y + 7.5 + (this.absolutePosition*(height-10)));
            this.boundingBox.reshape(this.connect_x,this.connect_y-7.5,18,15);
            break;
         case CircuitIONub.ONBOTTOM:
            this.connect_x = Math.round(x + 7.5 + (this.absolutePosition*(width-10)));
            this.connect_y = Math.round(y + height - 18);
            this.boundingBox.reshape(this.connect_x-7.5,this.connect_y,15,18);
            break;
      }
      if (this.kind === IONub.OUTPUT) {
         if (this.source != null) 
            this.source.setBoundingBox();
      }
      else {
         for (let i = 0; i < this.destination.size(); i++)
            this.destination.elementAt(i).setBoundingBox();
      }
   }
   
   reshape(x, y, width, height) {  // called only during creation/dragging in scroller (so always ONLEFT)
      this.boundingBox.reshape(x,y,width,height);
      switch (this.side) {
         case CircuitIONub.ONTOP:  this.connect_x = Math.round(x+7.5); this.connect_y = Math.round(y+18); break;
         case CircuitIONub.ONLEFT:  this.connect_x = Math.round(x+18); this.connect_y = Math.round(y+7.5); break;
         case CircuitIONub.ONRIGHT:  this.connect_x = Math.round(x); this.connect_y = Math.round(y+7.5); break;
         case CircuitIONub.ONBOTTOM:  this.connect_x = Math.round(x+7.5); this.connect_y = Math.round(y); break;
      }
   }

   dragTo(x, y, circuitBounds) {
      let dist = y + 5 - circuitBounds.y;
      this.side = CircuitIONub.ONTOP;
      if (x + 5 - circuitBounds.x < dist) {
         dist = x + 5 - circuitBounds.x;
         this.side = CircuitIONub.ONLEFT;
      }
      if (circuitBounds.x + circuitBounds.width - (x+10) < dist) {
         dist = circuitBounds.x + circuitBounds.width - (x+10);
         this.side = CircuitIONub.ONRIGHT;
      }
      if (circuitBounds.y + circuitBounds.height - (y+10) < dist) {
         dist = circuitBounds.y + circuitBounds.height - (y+10);
         this.side = CircuitIONub.ONBOTTOM;
      }
      if (this.side == CircuitIONub.ONTOP || this.side == CircuitIONub.ONBOTTOM) {
         x = x - circuitBounds.x;
         x = Math.min(x,Math.round(circuitBounds.width-15));
         x = Math.max(x,10);
         this.absolutePosition = x / circuitBounds.width;
      }
      else {
         y = y - circuitBounds.y;
         y = Math.min(y,Math.round(circuitBounds.height-15));
         y = Math.max(y,10);
         this.absolutePosition = y / circuitBounds.height;
      }
      this.getCoords(Math.round(circuitBounds.x), Math.round(circuitBounds.y), 
                       Math.round(circuitBounds.width), Math.round(circuitBounds.height));
   }
      
   copyDataInto(it) {
      it.selected = this.selected;
      it.on = this.on;
      it.reshape(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      it.connect_x = this.connect_x;
      it.connect_y = this.connect_y;
   }

   copy() {  // copied without source and destination
      let it = new CircuitIONub(this.side,this.absolutePosition,this.kind===IONub.INPUT);
      this.copyDataInto(it);
      return it;
   }
   
   delete(owner) {
      this.on = false;
      if (this.kind === IONub.INPUT) {
         owner.inputs.removeElement(this);
         for (let i = 0; i < this.destination.size(); i++) {
            let line = this.destination.elementAt(i);
            owner.lines.removeElement(line);
            line.destination.source = null;           
         }
         if (owner.saveContainerWhileEnlarged !== null && this.source !== null) {  // source line, if any, is in the containing circuit!
            owner.saveContainerWhileEnlarged.lines.removeElement(this.source);
            this.source.source.destination.removeElement(this.source);
         }
      }
      else {
         owner.outputs.removeElement(this);
         if (this.source != null) {
            owner.lines.removeElement(this.source);
            this.source.source.destination.removeElement(this.source);
         }
         if (owner.saveContainerWhileEnlarged !== null) {  // source line, if any, is in the containing circuit!
            for (let i = 0; i < this.destination.size(); i++) {
               let line = this.destination.elementAt(i);
               owner.saveContainerWhileEnlarged.lines.removeElement(line);
               line.destination.source = null;
            }
         }
      }
   }
   
   unDelete(owner) {
      if (this.kind === IONub.INPUT) {
         owner.inputs.addElement(this);
         for (let i = 0; i < this.destination.size(); i++) {
            let line = this.destination.elementAt(i);
            owner.lines.addElement(line);
            line.destination.source = line;           
         }
         if (owner.saveContainerWhileEnlarged !== null && this.source !== null) {  // source line, if any, is in the containing circuit!
            owner.saveContainerWhileEnlarged.lines.addElement(this.source);
            this.source.source.destination.addElement(this.source);
         }
      }
      else {
         owner.outputs.addElement(this);
         if (this.source !== null) {
            owner.lines.addElement(this.source);
            this.source.source.destination.addElement(this.source);
         }
         if (owner.saveContainerWhileEnlarged !== null) {  // source line, if any, is in the containing circuit!
            for (let i = 0; i < this.destination.size(); i++) {
               let line = this.destination.elementAt(i);
               owner.saveContainerWhileEnlarged.lines.addElement(line);
               line.destination.source = line;
            }
         }
      }
   }
   
} // end class CircuitIONub

CircuitIONub.ONRIGHT = 0;
CircuitIONub.ONTOP = 1;
CircuitIONub.ONLEFT = 2;
CircuitIONub.ONBOTTOM = 3;  // order is coordinated with order of constants in class Gate


class Gate extends CircuitItem {
   
   constructor(typeCode,  faceCode) {
      super();
      this.kind = typeCode;
      this.facing = faceCode;
      this.out = new CircuitIONub(faceCode,0.5,false);
      if (typeCode === Gate.NOTGATE) {
         this.in1 = new CircuitIONub((faceCode+2)%4,0.5,true);
         this.in2 = null;
      }
      else {
         this.in1 = new CircuitIONub((faceCode+2)%4,0.25,true);
         this.in2 = new CircuitIONub((faceCode+2)%4,0.75,true);
      }
   }
   
   draw(g) {
      if (this.selected)
         g.strokeStyle = "blue";
      else
         g.strokeStyle = "black";
      let absoluteVertices = Gate.vertexData[this.facing][this.kind];
      let x0, y0, x1, y1;
      x0 = Math.round(5 + this.boundingBox.x + absoluteVertices[0][0]*(this.boundingBox.width-10));
      y0 = Math.round(5 + this.boundingBox.y + absoluteVertices[0][1]*(this.boundingBox.height-10));
      g.beginPath();
      g.moveTo(x0,y0);
      for (let i = 1; i < absoluteVertices.length; i++) {
         x1 = Math.round(5 + this.boundingBox.x + absoluteVertices[i][0]*(this.boundingBox.width-10));
         y1 = Math.round(5 + this.boundingBox.y + absoluteVertices[i][1]*(this.boundingBox.height-10));     
         g.lineTo(x1,y1);
      }
      g.closePath();
      g.stroke();
      g.beginPath();
      g.moveTo(x0,y0);
      g.lineTo(Math.round(5 + this.boundingBox.x + absoluteVertices[0][0]*(this.boundingBox.width-10)),
                              Math.round(5 + this.boundingBox.y + absoluteVertices[0][1]*(this.boundingBox.height-10)));
      this.in1.drawIconified(g);
      if (this.in2 != null)
         this.in2.drawIconified(g);
      this.out.drawIconified(g);
   }

   reshape(x, y, width, height) {
      this.boundingBox.reshape(x,y,width,height);
      this.in1.getCoordsIconified(x,y,width,height);
      if (this.in2 != null)
         this.in2.getCoordsIconified(x,y,width,height);
      this.out.getCoordsIconified(x,y,width,height);
   }
   
   compute() {
      if (this.kind === Gate.NOTGATE)
         this.out.on = !this.in1.on;
      else {
         if (this.kind === Gate.ANDGATE)
            this.out.on = this.in1.on && this.in2.on;
         else
            this.out.on = this.in1.on || this.in2.on;
      }
      return false;  // gates don't change visible appearance
   }
   
   powerOff() {
      this.on = false;
      this.in1.on = false;
      if (this.in2 != null)
         this.in2.on = false;
      this.out.on = false;
   }

   drawWithLines(g) { 
      this.draw(g);
      if (this.in1.source != null)
         this.in1.source.draw(g);
      if (this.in2 != null && this.in2.source != null)
         this.in2.source.draw(g);
      for (let i = 0; i < this.out.destination.size(); i++)
         this.out.destination.elementAt(i).draw(g);
   }
   
   selectConnectedLines(select) { 
      if (this.in1.source != null)
         this.in1.source.selected = select;
      if (this.in2 != null && this.in2.source != null)
         this.in2.source.selected = select;
      for (let i = 0; i < this.out.destination.size(); i++)
         this.out.destination.elementAt(i).selected = select;
   }
   
   getCopyOfBoundingBox(addInLines = false) { 
      let r = new FloatRect(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      if (addInLines) {
         if (this.in1.source !== null)
            r.add(this.in1.source.boundingBox);
         if (this.in2 != null && this.in2.source != null)
            r.add(this.in2.source.boundingBox);
         for (let i = 0; i < this.out.destination.size(); i++)
            r.add(this.out.destination.elementAt(i).boundingBox);
      }
      r.grow(1,1);
      return r.getIntRect();
   }

   getLineSource(x, y) { 
      if (x < this.boundingBox.x - 2 || x > this.boundingBox.x + this.boundingBox.width + 2 ||
             y < this.boundingBox.y - 2|| y > this.boundingBox.y + this.boundingBox.height + 2)
         return null;
      return this.out;
   }
   
   getLineDestination(x, y) { 
      if (this.in1.source != null && (this.in2 == null || this.in2.source != null))
         return null;
      if (x < this.boundingBox.x - 2 || x > this.boundingBox.x + this.boundingBox.width + 2 ||
             y < this.boundingBox.y - 2|| y > this.boundingBox.y + this.boundingBox.height + 2)
         return null;
      if (this.in2 == null)
         return this.in1;
      else if (this.in1.source != null)
         return this.in2;
      else if (this.in2.source != null)
         return this.in1;
      else {
         let d1 = (x-this.in1.connect_x)*(x-this.in1.connect_x) + (y-this.in1.connect_y)*(y-this.in1.connect_y); 
         let d2 = (x-this.in2.connect_x)*(x-this.in2.connect_x) + (y-this.in2.connect_y)*(y-this.in2.connect_y);
         if (d1 <= d2)
            return this.in1;
         else
            return this.in2;
      }
   }

   copy() {  // copied without source and destination
      let it = new Gate(this.kind,this.facing);
      it.selected = this.selected;
      it.on = this.on;
      it.reshape(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      this.in1.copyDataInto(it.in1);
      if (this.kind !== Gate.NOTGATE)
         this.in2.copyDataInto(it.in2);
      this.out.copyDataInto(it.out);
      return it;
   }
   
   delete(owner) {
      owner.items.removeElement(this);
      if (this.in1.source != null) {
         owner.lines.removeElement(this.in1.source);
         this.in1.source.source.destination.removeElement(this.in1.source);
      }
      if (this.in2 != null && this.in2.source != null) {
         owner.lines.removeElement(this.in2.source);
         this.in2.source.source.destination.removeElement(this.in2.source);
      }      
      for (let i = 0; i < this.out.destination.size(); i++) {
         for (let j = 0; j < this.out.destination.size(); j++) {
            let line = this.out.destination.elementAt(i);
            owner.lines.removeElement(line);
            line.destination.source = null;           
         }
      }
   }

   unDelete(owner) {
      owner.items.addElement(this);
      if (this.in1.source != null) {
         owner.lines.addElement(this.in1.source);
         this.in1.source.source.destination.addElement(this.in1.source);
      }
      if (this.in2 != null && this.in2.source != null) {
         owner.lines.addElement(this.in2.source);
         this.in2.source.source.destination.addElement(this.in2.source);
      }      
      for (let i = 0; i < this.out.destination.size(); i++) {
         for (let j = 0; j < this.out.destination.size(); j++) {
            let line = this.out.destination.elementAt(i);
            owner.lines.addElement(line);
            line.destination.source = line;           
         }
      }
   }

}

Gate.NOTGATE = 0; Gate.ORGATE = 1; Gate.ANDGATE = 2;  // types of gates
Gate.FACERIGHT = 0; Gate.FACEDOWN = 1; Gate.FACELEFT = 2; Gate.FACEUP = 3;  // directions they can face
Gate.standardVertexData = [   // vertices for right-facing gates
       [ [0,0], [0.85,0.5], [0.9,0.37], [0.95,0.37], [1,0.5], [0.95,0.63], [0.9,0.63],  [0.85,0.5], [0,1] ],
       [ [0,0], [0.31,0.035], [0.55,0.12], [0.88,0.34], [1,0.5], 
                [0.88,0.66], [0.55,0.88], [0.31,0.965], [0,1], 
                [0.1,0.75], [0,0.75], [0.1,0.75], [0.12,0.5], [0.1,0.25], [0,0.25], [0.1,0.25] ],
       [ [0,0], [0.5,0], [0.75,0.07], [0.93,0.25], [1,0.45], [1,0.55], [0.93,0.75], [0.75,0.93], [0.5,1], [0,1] ] ];
Gate.vertexData = [];
Gate.vertexData[0] = Gate.standardVertexData;
for (let v = 1; v < 4; v++) {
  Gate.vertexData[v] = new Array(3);
  for (let i = 0; i < 3; i++) {
    Gate.vertexData[v][i] = new Array(Gate.standardVertexData[i].length);
    for (let j = 0; j < Gate.vertexData[v][i].length; j++) {
       Gate.vertexData[v][i][j] = [ Gate.vertexData[v-1][i][j][1], 1 - Gate.vertexData[v-1][i][j][0] ];
    }
  }
}



class Circuit extends CircuitItem {
   
   constructor() {
       super();
       this.inputs = new Vector();
       this.outputs = new Vector();
       this.lines = new Vector();
       this.items = new Vector();
       this.iconified = false;
       this.saveContainerWhileEnlarged = null;  // this is non-null ONLY when this is a contained circuit that has been enlarged
       this.source = null;
       this.boundingBoxInContainer = null;
       this.savedBoundingBox = null;
       this.name = null;
   }
   
   iconify(x, y, width, height) {  // value of boundingBoxInContainer is used it CircuitCanvas
      this.iconified = true;
      this.saveContainerWhileEnlarged = null;
      this.boundingBoxInContainer = null;
      this.reshape(x,y,width,height);
      this.powerOff();
   }
   
   deiconify(x, y, width, height, source = null) {
      this.iconified = false;
      this.saveContainerWhileEnlarged = source;
      if (source !== null)
         this.boundingBoxInContainer = new FloatRect(this.boundingBox.x, this.boundingBox.y, this.boundingBox.width, this.boundingBox.height);
      this.reshape(x,y,width,height);
      this.powerOff();
   }
      
   reshape(x, y, width, height) {
      if (!this.iconified && width < 10 || height < 10)
         return;
      this.boundingBox.reshape(x,y,width,height);
      if (!this.iconified) {  // adjust sizes/positions of contained items
         if (this.savedBoundingBox != null) {
            let xFactor = this.boundingBox.width / this.savedBoundingBox.width;
            let yFactor = this.boundingBox.height / this.savedBoundingBox.height;
            for (let i = 0; i < this.items.size(); i++) {
               let it = this.items.elementAt(i);
               if (it instanceof Tack)
                  it.reshape(5 + ((it.boundingBox.x - 5)*xFactor), 5 + ((it.boundingBox.y - 5)*yFactor), 
                              it.boundingBox.width, it.boundingBox.height);
               else
                 it.reshape( 5 + ((it.boundingBox.x - 5)*xFactor), 5 + ((it.boundingBox.y - 5)*yFactor),
                              Math.max(10,(it.boundingBox.width)*xFactor), Math.max(10,(it.boundingBox.height)*yFactor) );
            }
         }
         this.savedBoundingBox = new FloatRect(this.boundingBox.x, this.boundingBox.y, this.boundingBox.width, this.boundingBox.height);
      }
      for (let i = 0; i < this.inputs.size(); i++) {
         if (this.iconified)
            this.inputs.elementAt(i).getCoordsIconified(x,y,width,height);
         else 
            this.inputs.elementAt(i).getCoords(x,y,width,height);
      }
      for (let i = 0; i < this.outputs.size(); i++) {
         if (this.iconified)
            this.outputs.elementAt(i).getCoordsIconified(x,y,width,height);
         else
            this.outputs.elementAt(i).getCoords(x,y,width,height);
      }
   }
   
   addItem(item) {
      if (item instanceof CircuitIONub) {
         if (item.kind === IONub.INPUT)
            this.inputs.addElement(item);
         else
            this.outputs.addElement(item);
      }
      else if (item instanceof Line)
         this.lines.addElement(item);
      else
         this.items.addElement(item);
   }
   
   computeTopLevel() { // returns true if some visible element it the circuit changes
      let changed = false;
      for (let i = 0; i < this.items.size(); i++) {
          let item = this.items.elementAt(i);
          if (item.compute())
              changed = true;
      }
      for (let i = 0; i < this.lines.size(); i++) {
          if (this.lines.elementAt(i).compute())
             changed = true;
      }
      for (let i = 0; i < this.lines.size(); i++) {
          let line = this.lines.elementAt(i);
          line.destination.on = line.on;
      }
      return changed;
   }
   
   compute() {  // called for nested circuits, which don't change visible appearance
      for (let i = 0; i < this.items.size(); i++) {
          let item = this.items.elementAt(i);
          item.compute();
      }
      for (let i = 0; i < this.lines.size(); i++) {
          this.lines.elementAt(i).compute();
      }
      for (let i = 0; i < this.lines.size(); i++) {
          let line = this.lines.elementAt(i);
          line.destination.on = line.on;
      }
      return false;
   }
   
   powerOff() {
      this.on = false;
      for (let i = 0; i < this.lines.size(); i++) {
          this.lines.elementAt(i).on = false;
      }
      for (let i = 0; i < this.items.size(); i++) {
          this.items.elementAt(i).powerOff();
      }
      for (let i = 0; i < this.inputs.size(); i++) {
          this.inputs.elementAt(i).on = false;
      }
      for (let i = 0; i < this.outputs.size(); i++) {
          this.outputs.elementAt(i).on = false;
      }
   }

   draw(g) {
       let saveFont = g.font;
       if ( ! Circuit.nameFont) {
           Circuit.nameFontBig = "13px monospace";
           g.font = Circuit.nameFontBig;
           Circuit.nameCharWidthBig = g.measureText('W').width;
           Circuit.nameFont = "11px monospace";
           g.font = Circuit.nameFont;
           Circuit.nameCharWidth = g.measureText('W').width;
           Circuit.containerNameFont = "15px bold serif";
       }
       if (this.iconified) {
          if (this.selected) {
             g.fillStyle = "blue";
             g.strokeStyle = "blue";
          }
          else {
             g.fillStyle = "black";
             g.strokeStyle = "black";
          }
          let x1 = Math.round(this.boundingBox.x+5);
          let y1 = Math.round(this.boundingBox.y+5);
          let x2 = Math.round(this.boundingBox.x+this.boundingBox.width-5);
          let y2 = Math.round(this.boundingBox.y+this.boundingBox.height-5);
          g.beginPath();
          g.moveTo(x1,y1);
          g.lineTo(x1,y2);
          g.lineTo(x2,y2);
          g.lineTo(x2,y1);
          g.lineTo(x1,y1);
          g.stroke();
          for (let i = 0; i < this.inputs.size(); i++)
             this.inputs.elementAt(i).drawIconified(g);
          for (let i = 0; i < this.outputs.size(); i++)
             this.outputs.elementAt(i).drawIconified(g);
           this.putName(g);
           g.font = saveFont;
         }
       else {
          for (let i = 0; i < this.lines.size(); i++) {
              this.lines.elementAt(i).draw(g);
          }
          for (let i = 0; i < this.items.size(); i++) {
              this.items.elementAt(i).draw(g);
          }
          for (let i = 0; i < this.inputs.size(); i++) {
              this.inputs.elementAt(i).draw(g);
          }
          for (let i = 0; i < this.outputs.size(); i++) {
              this.outputs.elementAt(i).draw(g);
          }
          if (this.saveContainerWhileEnlarged !== null) {
             g.font = Circuit.containerNameFont;
             g.fillStyle = "red";
             g.fillText("Enlarged from \"" + this.saveContainerWhileEnlarged.name + '\"',
                                    this.boundingBox.x+10, (this.boundingBox.y+this.boundingBox.height)-12);
             g.font = saveFont;
          }
       }
   }
   
   putName(g) {
   
      let charWidth,charHeight,charLeading;
      let maxChars = (this.boundingBox.width - 16) / Circuit.nameCharWidthBig;
      if (maxChars < 6) {
         g.font = Circuit.nameFont;
         charWidth = Circuit.nameCharWidth;
         charHeight = 10;
         charLeading = 2;
         maxChars = (this.boundingBox.width - 16) / Circuit.nameCharWidth;
      }
      else {
         g.font = Circuit.nameFontBig;
         charWidth = Circuit.nameCharWidthBig;
         charHeight = 12;
         charLeading = 3;
      }   
      if (this.selected)
         g.fillStyle = "blue";
      else
         g.fillStyle = "black";
      if (maxChars == 0)
         maxChars = 1;
      let maxLines = Math.round((this.boundingBox.height - 14 + charLeading) / charHeight);
      if (maxLines <= 0)
         maxLines = 1;
      this.name = this.name.trim();
      let lineBreak = new Array(maxLines+1);
      lineBreak[0] = -1;
      let line = 0;
      let pos = 0;
      while (true) {
         if (pos >= this.name.length) {
            lineBreak[line+1] = pos;
            line++;
            break;
         }
         let charCt = 0;
         let lastSpace = -1;
         while (pos < this.name.length && charCt <= maxChars) {
            if (this.name.charAt(pos) === ' ')
               lastSpace = pos;
            pos++;
            charCt++;
         }
         if (charCt > maxChars) {
            if (lastSpace >= 0) {
               lineBreak[line+1] = lastSpace;
               pos = lastSpace + 1;
            }
            else {
               while (pos < this.name.length && this.name.charAt(pos) !== ' ')
                  pos++;
               lineBreak[line+1] = pos;
               pos++;
            }
            line++;
            if (line >= maxLines || pos >= this.name.length)
               break;
         }
      }
      let center_x = (this.boundingBox.x + (this.boundingBox.width+1)/2);
      let top_y =(this.boundingBox.y + this.boundingBox.height/2) - (line*charHeight - charLeading)/2 + charHeight - charLeading;
      for (let i = 0; i < line; i++) {
         let ct = lineBreak[i+1] - lineBreak[i];
         if (ct <= maxChars) {
            let w = (lineBreak[i+1] - lineBreak[i] - 1)*charWidth;
            g.fillText(this.name.substring(lineBreak[i]+1,lineBreak[i+1]),
                            center_x - w/2, top_y + i*charHeight);
         }
         else {
            let w = maxChars * charWidth;
            g.fillText(this.name.substring(lineBreak[i]+1,lineBreak[i]+maxChars+1),
                            center_x - w/2, top_y + i*charHeight);
         }
      }
   }
   
   drawWithLines(g) { 
      this.draw(g);
      if (this.iconified) {
         for (let i = 0; i < this.inputs.size(); i++) {
             let it = this.inputs.elementAt(i);
             if (it.source != null)
                it.source.draw(g);
         }
         for (let i = 0; i < this.outputs.size(); i++) {
             let out = this.outputs.elementAt(i);
             for (let j = 0; j < out.destination.size(); j++)
                out.destination.elementAt(j).draw(g);
         }
      }
   }

   getCopyOfBoundingBox(addInLines) { 
      let r = new FloatRect(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      if (addInLines) {
         for (let i = 0; i < this.inputs.size(); i++) {
            let it = this.inputs.elementAt(i);
            if (it.source != null)
               r.add(it.source.boundingBox);
         }
         for (let i = 0; i < tis.outputs.size(); i++) {
            let out = this.outputs.elementAt(i);
            for (let j = 0; j < out.destination.size(); j++)
               r.add(out.destination.elementAt(j).boundingBox);
         }
      }
      r.grow(1,1);
      return r.getIntRect();
   }
   
   selectConnectedLines(select) { 
      if (this.iconified) {
         for (let i = 0; i < this.inputs.size(); i++) {
             let it = this.inputs.elementAt(i);
             if (it.source != null)
                it.source.selected = select;
         }
         for (let i = 0; i < this.outputs.size(); i++) {
             let out = this.outputs.elementAt(i);
             for (let j = 0; j < out.destination.size(); j++)
                out.destination.elementAt(j).selected = select;
         }
      }
   }
   
   itemHitForLineSource(x, y) {
      for (let i = this.inputs.size()-1; i >= 0; i--) {
          let it = this.inputs.elementAt(i);  
          if (it.getLineSource(x,y) != null)
             return it;
      }
      for (let i = this.items.size()-1; i >= 0; i--) {
          let it = this.items.elementAt(i);  
          if (it.getLineSource(x,y) != null)
             return it;
      }
      return null;
   }

   itemHitForLineDestination(x, y) {
      for (let i = this.outputs.size()-1; i >= 0; i--) {
          let it = this.outputs.elementAt(i);  
          if (it.getLineDestination(x,y) != null)
             return it;
      }
      for (let i = this.items.size()-1; i >= 0; i--) {
          let it = this.items.elementAt(i);  
          if (it.getLineDestination(x,y) != null)
             return it;
      }
      return null;
   }

   getLineDestination(x, y) { 
      if (x < this.boundingBox.x - 2 || x > this.boundingBox.x + this.boundingBox.width + 2 ||
             y < this.boundingBox.y - 2|| y > this.boundingBox.y + this.boundingBox.height + 2)
         return null;
      let i = 0;
      while (i < this.inputs.size() && this.inputs.elementAt(i).source != null)
         i++;
      if (i >= this.inputs.size())
         return null;
      let it_best = this.inputs.elementAt(i);
      let d_min = (x-it_best.connect_x)*(x-it_best.connect_x) + (y-it_best.connect_y)*(y-it_best.connect_y);
      for (let j = i+1; j < this.inputs.size(); j++) {
         let it = this.inputs.elementAt(j);
         if (it.source === null) {
            let d = (x-it.connect_x)*(x-it.connect_x) + (y-it.connect_y)*(y-it.connect_y);
            if (d < d_min) {
               d_min = d;
               it_best = it;
            }
         }
      }
      return it_best;
   }

   getLineSource(x, y) { 
      if (x < this.boundingBox.x - 2 || x > this.boundingBox.x + this.boundingBox.width + 2 ||
             y < this.boundingBox.y - 2|| y > this.boundingBox.y + this.boundingBox.height + 2)
         return null;
      if (this.outputs.size() == 0)
         return null;
      let it_best = this.outputs.elementAt(0);
      let d_min = (x-it_best.connect_x)*(x-it_best.connect_x) + (y-it_best.connect_y)*(y-it_best.connect_y);
      for (let j = 1; j < this.outputs.size(); j++) {
         let it = this.outputs.elementAt(j);
         let d = (x-it.connect_x)*(x-it.connect_x) + (y-it.connect_y)*(y-it.connect_y);
         if (d < d_min) {
            d_min = d;
            it_best = it;
         }
      }
      return it_best;
   }

   copy() {  // copies lines on inside only
      let it = new Circuit();
      it.selected = this.selected;
      it.on = this.on;
      it.iconified = this.iconified;
      it.name = this.name;
      if (this.savedBoundingBox === null)
         it.savedBoundingBos = null;
      else
         it.savedBoundingBox = new FloatRect(this.savedBoundingBox.x,this.savedBoundingBox.y,
                                             this.savedBoundingBox.width,this.savedBoundingBox.height);
 
      for (let i = 0; i < this.inputs.size(); i++) {
         let x = this.inputs.elementAt(i);
         it.inputs.setElementAt(x.copy(),i);
      }
      for (let i = 0; i < this.outputs.size(); i++) {
         let x = this.outputs.elementAt(i);
         it.outputs.setElementAt(x.copy(),i);
      }
      for (let i = 0; i < this.items.size(); i++) {
         let x = this.items.elementAt(i);
         it.items.setElementAt(x.copy(),i);
      }
      for (let i = 0; i < this.lines.size(); i++) {
         let x = this.lines.elementAt(i);
         x.pos = i;
         it.lines.setElementAt(x.copy(),i);
      }

      for (let i = 0; i < this.inputs.size(); i++) {
         let x = this.inputs.elementAt(i);
         let newx = it.inputs.elementAt(i);
         for (let j = 0; j < x.destination.size(); j++) {
            let lin = it.lines.elementAt(x.destination.elementAt(j).pos);
            newx.destination.setElementAt(lin,j);
            lin.source = newx;
         }
      }

      for (let i = 0; i < this.outputs.size(); i++) {
         let x = this.outputs.elementAt(i);
         let newx = it.outputs.elementAt(i);
         if (x.source != null) {
            let lin = it.lines.elementAt(x.source.pos);
            newx.source = lin;
            lin.destination = newx;
         }
      }

      for (let i = 0; i < this.items.size(); i++) {
         let x = this.items.elementAt(i);
         if (x instanceof Tack) {
            let newx = it.items.elementAt(i);
            if (x.source != null) {
               let lin = it.lines.elementAt(x.source.pos);
               newx.source = lin;
               lin.destination = newx;
            }
            for (let j = 0; j < x.destination.size(); j++) {
               let lin = it.lines.elementAt(x.destination.elementAt(j).pos);
               newx.destination.setElementAt(lin,j);
               lin.source = newx;
            }
         }
         else if (x instanceof Gate) {
            let newx = it.items.elementAt(i);
            if (x.in1.source != null) {
               let lin = it.lines.elementAt(x.in1.source.pos);
               newx.in1.source = lin;
               lin.destination = newx.in1;
            }
            if (x.in2 != null && x.in2.source != null) {
               let lin = it.lines.elementAt(x.in2.source.pos);
               newx.in2.source = lin;
               lin.destination = newx.in2;
            }
            for (let j = 0; j < x.out.destination.size(); j++) {
               let lin = it.lines.elementAt(x.out.destination.elementAt(j).pos);
               newx.out.destination.setElementAt(lin,j);
               lin.source = newx.out;
            }
         }
         else if (x instanceof Circuit) {
            let newx = it.items.elementAt(i);
            for (let k = 0; k < x.inputs.size(); k++) {
               let io = x.inputs.elementAt(k);
               let newio = newx.inputs.elementAt(k);
               if (io.source != null) {
                  let lin = it.lines.elementAt(io.source.pos);
                  newio.source = lin;
                  lin.destination = newio;
               }
            }
            for (let k = 0; k < x.outputs.size(); k++) {
               let io = x.outputs.elementAt(k);
               let newio = newx.outputs.elementAt(k);
               for (let j = 0; j < io.destination.size(); j++) {
                  let lin = it.lines.elementAt(io.destination.elementAt(j).pos);
                  newio.destination.setElementAt(lin,j);
                  lin.source = newio;
               }
            }
         }
      }
      
      it.reshape(this.boundingBox.x,this.boundingBox.y,this.boundingBox.width,this.boundingBox.height);
      return it;
   }
   
   delete(owner) {
      this.powerOff();
      owner.items.removeElement(this);
      for (let i = 0; i < this.inputs.size(); i++) {
         let it = this.inputs.elementAt(i);
         if (it.source != null) {
            owner.lines.removeElement(it.source);
            it.source.source.destination.removeElement(it.source);
         }
      }
      for (let i = 0; i < this.outputs.size(); i++) {
         let out = this.outputs.elementAt(i);
         for (let j = 0; j < out.destination.size(); j++) {
            let line = out.destination.elementAt(j);
            owner.lines.removeElement(line);
            line.destination.source = null;           
         }
      }
   }
   
   unDelete(owner) {
      owner.items.addElement(this);
      for (let i = 0; i < this.inputs.size(); i++) {
         let it = this.inputs.elementAt(i);
         if (it.source != null) {
            owner.lines.addElement(it.source);
            it.source.source.destination.addElement(it.source);
         }
      }
      for (let i = 0; i < this.outputs.size(); i++) {
         let out = this.outputs.elementAt(i);
         for (let j = 0; j < out.destination.size(); j++) {
            let line = out.destination.elementAt(j);
            owner.lines.addElement(line);
            line.destination.source = line;           
         }
      }
   }
   
}
