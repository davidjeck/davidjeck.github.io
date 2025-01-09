/**
 * The base class for all nodes in the scene graph data structure.
 */
class SceneGraphNode {
    constuctor() {
        this.fillColor = null;   // If non-null, the default fillStyle for this node.
        this.strokeColor = null; // If non-null, the default strokeStyle for this node.
           // a null value means that color is inherited from a node's parent
    }
    doDraw(g) {
            // This method is meant to be abstract and must be
            // OVERRIDDEN in any actual object in the scene graph.
            // It is not meant to be called; it is called by draw().
        throw "doDraw not implemented in SceneGraphNode";
    }
    draw(g) {
           // This method should be CALLED to draw the object
           // represented by this SceneGraphNode.  It should NOT
           // ordinarily be overridden in subclasses.
        graphics.save();
        if (this.fillColor) {
            g.fillStyle = this.fillColor;
        }
        if (this.strokeColor) {
            g.strokeStyle = this.strokeColor;
        }
        this.doDraw(g);
        graphics.restore();
    }
    setFillColor(color) {
            // Sets fillColor for this node to color.
            // Color should be a legal CSS color string, or null.
        this.fillColor = color;
        return this;
    }
    setStrokeColor(color) {
            // Sets fillColor for this node to color.
            // Color should be a legal CSS color string, or null.
        this.strokeColor = color;
        return this;
    }
    setColor(color) {
            // Sets both the fillColor and strokeColor to color.
            // Color should be a legal CSS color string, or null.
        this.fillColor = color;
        this.strokeColor = color;
        return this;
    }
} // end clsss SceneGraphNode


/**
 *  Defines a subclass, CompoundObject, of SceneGraphNode to represent
 *  an object that is made up of sub-objects.  Initially, there are no
 *  sub-objects.
 */
class CompoundObject extends SceneGraphNode {
    constructor(...objects) {
        super();
        this.subobjects = [];
        for (let obj of objects)
            this.add(obj);
    }
    add(node) {
            // Add node a subobject of this object.  Note that the
            // return value is a reference to this node, to allow chaining
            // of method calls.
        this.subobjects.push(node);
        return this;
    }
    doDraw(g) {
            // Just call the sub-objects' draw() methods.
        for (let i = 0; i < this.subobjects.length; i++)
            this.subobjects[i].draw(g);
    }
} // end class CompoungObject

   
/**
 *  Define a subclass, TransformedObject, of SceneGraphNode that
 *  represents an object along with a modeling transformation to
 *  be applied to that object.  The object must be specified in
 *  the constructor.  The transformation is specified by calling
 *  the setScale(), setRotate() and setTranslate() methods. Note that
 *  each of these methods returns a reference to the TransformedObject
 *  as its return value, to allow for chaining of method calls.
 *  The modeling transformations are always applied to the object
 *  in the order scale, then rotate, then translate.
 */
class TransformedObject extends SceneGraphNode {
    constructor(object) {
        super();
        this.object = object;
        this.rotationInDegrees = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        this.translateX = 0;
        this.translateY = 0;
    }
    setRotation(angle) {
           // Set the angle of rotation, measured in DEGREES.  The rotation
           // is always about the origin.
        this.rotationInDegrees = angle;
        return this;
    }
    setScale(sx, sy = sx) {
           // Sets scaling factors.
        this.scaleX = sx;
        this.scaleY = sy;
        return this;
    }
    setTranslation(dx,dy) {
           // Set translation amounts.
        this.translateX = dx;
        this.translateY = dy;
        return this;
    }
    doDraw(g) {
            // Draws the object, with its modeling transformation.
        g.save();
        if (this.translateX != 0 || this.translateY != 0) {
            g.translate(this.translateX, this.translateY);
        }
        if (this.rotationInDegrees != 0) {
            g.rotate(this.rotationInDegrees/180*Math.PI);
        }
        if (this.scaleX != 1 || this.scaleY != 1) {
            g.scale(this.scaleX, this.scaleY);
        }
        this.object.draw(g);
        g.restore();
    }
} // end class TransformedObject
    
/**
 *  A subclass of SceneGraphNode representing filled triangles.
 *  The constructor specifies the vertices of the triangle:
 *  (x1,y1), (x2,y2), and (x3,y3).
 */
class Triangle extends SceneGraphNode {
    constructor(x1,y1,x2,y2,x3,y3) {
        super();
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
    }
    doDraw(g) {
        g.beginPath();
        g.moveTo(this.x1,this.y1);
        g.lineTo(this.x2,this.y2);
        g.lineTo(this.x3,this.y3);
        g.closePath();
        g.fill();
    }
}


/**
 * Directly create a line object as a SceneGraphNode with a
 * custom doDraw() method.  line is of length 1 and
 * extends along the x-axis from (0,0) to (1,0).
 */
const line = new SceneGraphNode();
line.doDraw = function(g) {
    g.beginPath();
    g.moveTo(0,0);
    g.lineTo(1,0);
    g.stroke();
};

/**
 * Directly create a filled rectangle object as a SceneGraphNode with a
 * custom doDraw() method.  filledRect is a square with side 1, centered
 * at (0,0), with corners at (-0.5,-0.5) and (0.5,0.5).
 */
const filledRect = new SceneGraphNode();
filledRect.doDraw = function(g) {
    g.fillRect(-0.5,-0.5,1,1);
};

/**
 * Directly create a rectangle object as a SceneGraphNode with a
 * custom doDraw() method.  rect is a square with side 1, centered
 * at (0,0), with corners at (-0.5,-0.5) and (0.5,0.5).  Only the
 * outline of the square is drawn.
 */
const rect = new SceneGraphNode();
rect.doDraw = function(g) {
    g.strokeRect(-0.5,-0.5,1,1);
};

/**
 * Directly create a filled circle object as a SceneGraphNode with a
 * custom doDraw() method.  filledCircle is a circle with radius 0.5
 * (diameter 1), centered at (0,0).
 */
const filledCircle = new SceneGraphNode();
filledCircle.doDraw = function(g) {
    g.beginPath();
    g.arc(0,0,0.5,0,2*Math.PI);
    g.fill();
};

/**
 * Directly create a circle object as a SceneGraphNode with a
 * custom doDraw() method.  filledCircle is a circle with radius 0.5
 * (diameter 1), centered at (0,0).  Only the outline of the circle
 * is drawn.
 */
const circle = new SceneGraphNode();
circle.doDraw = function(g) {
    g.beginPath();
    g.arc(0,0,0.5,0,2*Math.PI);
    g.stroke();
};
