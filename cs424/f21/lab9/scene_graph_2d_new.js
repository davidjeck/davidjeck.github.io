/**
 * The base class for all nodes in the scene graph data structure.
 */
class SceneGraphNode {
    constructor() { 
        this.fillColor = null;   // If non-null, the default fillStyle for this node.
        this.strokeColor = null; // If non-null, the default strokeStyle for this node.
           // a null value means that color is inherited from a node's parent
        this.parent = null;
    }
    copy() {
           // Returns a deep copy of this object, except with parent pointer set to null.
        let clone = Object.create(Object.getPrototypeOf(this)); 
             // creates an empty object belonging to the same class as "this".
        clone.fillColor = this.fillColor;
        clone.strokeColor = this.strokeColor;
        clone.parent = null;  // don't copy parent!
        return clone;
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
        g.save();
        if (this.fillColor) {
            g.fillStyle = this.fillColor;
        }
        if (this.strokeColor) {
            g.strokeStyle = this.strokeColor;
        }
        this.doDraw(g);
        g.restore();
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
    copy() {
            // Make a copy of this node, recursively copying its child nodes.
        let clone = super.copy();
        clone.subobjects = [];
        for (let i = 0; i < this.subobjects.length; i++) {
            clone.add(this.subobjects[i].copy());
        }
        return clone;
    }
    add(node) { 
            // Add node a subobject of this object.  Note that the
            // return value is a reference to this node, to allow chaining
            // of method calls.  This method and remove() manage the parent pointers.
            // If the added node is currently a child of some other node, an
            // error is thrown.
        if (node.parent) {
            throw new Error("Can't add node to CompoundObject since it already has a parent node.");
        }
        node.parent = this;
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
           // Create a TransformedObject with object as its only
           // child.  object cannot be null.  If object already
           // has a parent, an error is thrown.
        if (object.parent) {
           throw new Error("Can't create TransformedObject since object already has a parent.");
        }
        super();
        this.object = object;
        this.rotationInDegrees = 0;
        this.scaleX = 1;
        this.scaleY = 1;
        this.translateX = 0;
        this.translateY = 0;
    }
    copy() {
            // Create a copy of this node, including a recursive copy of its child.
        let clone = super.copy();
        clone.object = this.object.copy();
        clone.object.parent = this;
        clone.rotationInDegrees = this.rotationInDegrees;
        clone.scaleX = this.scaleX;
        clone.scaleY = this.scaleY;
        clone.translateX = this.translateX;
        clone.translateY = this.translateY;
        return clone;
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
            // Draws the object, if object is not null, with its modeling transformation.
        if (!this.object)
            return;
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
    copy() {
       let clone = super.copy();
       clone.x1 = this.x1;
       clone.y1 = this.y1;
       clone.x2 = this.x2;
       clone.y2 = this.y2;
       clone.x3 = this.x3;
       clone.y3 = this.y3;
       return clone;
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
 *  This class was introduced to be the class of the basic objects
 *  defined below.  Previously, those objects were of type
 *  SceneGraphNode.  This solved the problem that the copy() method
 *  in SceneGraphNode produced a node with the doDraw() method from
 *  SceneGraphNode, not the doDraw() method that is added to the 
 *  basic object, since that doDraw() is added as a property of the
 *  object, not defined as a method in a class.
 */
class BasicObject extends SceneGraphNode {
    copy() {
       let clone = super.copy();
       clone.doDraw = this.doDraw;
       return clone;
    }
}


/**
 * Directly create a line object as a SceneGraphNode with a
 * custom doDraw() method.  line is of length 1 and
 * extends along the x-axis from (0,0) to (1,0).
 */
const line = new BasicObject();
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
const filledRect = new BasicObject();
filledRect.doDraw = function(g) {
    g.fillRect(-0.5,-0.5,1,1);
};

/**
 * Directly create a rectangle object as a SceneGraphNode with a
 * custom doDraw() method.  rect is a square with side 1, centered
 * at (0,0), with corners at (-0.5,-0.5) and (0.5,0.5).  Only the
 * outline of the square is drawn.
 */
const rect = new BasicObject();
rect.doDraw = function(g) {
    g.strokeRect(-0.5,-0.5,1,1);
};

/**
 * Directly create a filled circle object as a SceneGraphNode with a
 * custom doDraw() method.  filledCircle is a circle with radius 0.5
 * (diameter 1), centered at (0,0).
 */
const filledCircle = new BasicObject();
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
const circle = new BasicObject();
circle.doDraw = function(g) {
    g.beginPath();
    g.arc(0,0,0.5,0,2*Math.PI);
    g.stroke();
};

