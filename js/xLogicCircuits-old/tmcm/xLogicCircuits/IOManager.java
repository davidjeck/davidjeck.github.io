package tmcm.xLogicCircuits;

import java.io.*;
import java.util.Vector;


class CircuitIOException extends IOException {
   CircuitIOException(String message) {
      super(message);
   }
}


class IOManager implements Runnable {

   CircuitCanvas owner;

   Thread runner;
   
   final static int IDLE = 0, LOADING = 1;
   int state = IDLE;
   
   InputStream in;
   
   Vector circuitStack;     // data read from InputStream; circuitStack includes "currentCircuit" as last element
   Vector scrollItems;

   String errorMessage;

   IOManager(CircuitCanvas owner) {
      this.owner = owner;
   }
   
   synchronized void writeCircuitData(PrintStream out, Vector circuitStack, Circuit currentCircuit, Vector scrollItems) throws CircuitIOException {
      putProlog(out);
      if (out.checkError())
         throw new CircuitIOException("An error occurred while writing data to output.");
      putCircuitData(out,circuitStack,currentCircuit,scrollItems);
      if (out.checkError())
         throw new CircuitIOException("An error occurred while writing data to output.");
   }

   synchronized boolean startReading(InputStream in){  // returns: circuitStack, items for scroller, currentCircuit
      if (state != IDLE || in == null)  // caller must insure this doesn't happen
         return false;
      state = LOADING;
      this.in = in;
      if (runner == null || !runner.isAlive()) {
         runner = new Thread(this, "Circuit Loader");
         runner.start();
      }
      else
         notify();
      return true;
   }
   
   synchronized void cancelLoad() {
      state = IDLE;
      if (runner != null && runner.isAlive()) {
         runner.stop();
         try { runner.join(1000); }
         catch (InterruptedException e) { }
      }
      runner = null;
      circuitStack = null;
      scrollItems = null;
      in = null;
   }
   
   //----------------------------------------------------------------------------
   
   private synchronized void doneLoading(boolean dataOK) {
      if (state == IDLE)
         return;
      if (dataOK) {
         Circuit currentCircuit = (Circuit)circuitStack.lastElement();
         circuitStack.setSize(circuitStack.size() - 1);
         owner.doneLoading(circuitStack, currentCircuit, scrollItems);
      }
      else
         owner.doneLoadingWithError(errorMessage);
      state = IDLE;
      circuitStack = null;
      scrollItems = null;
      in = null;
   }
   
   private synchronized int getState() {
      return state;
   }
   
   public void run() {
     while (true) {
        synchronized(this) {
          while (getState() == IDLE) {
             try { wait(); }
             catch (InterruptedException e) { }
          }
        }
        InputStream in = this.in;
        try { Thread.sleep(500); }
        catch (InterruptedException e) { }
        try {
           getProlog(in);
           circuitStack = getCircuitStack(in);
           scrollItems = getScrollItems(in);
           doneLoading(true);
        }
        catch (CircuitIOException e) {
           errorMessage = e.getMessage();
           doneLoading(false);
        }
        catch (OutOfMemoryError e) {
           errorMessage = "Ran out of memory while loading data.";
           doneLoading(false);
        }
        catch (Exception e) {
           errorMessage = "Unexpected error while loading data (" + e + ").";
           doneLoading(false);
        }
      }
   }
   
   private void inputError(String message) throws CircuitIOException {
      throw new CircuitIOException(message);
   }
   
   private void inputError() throws CircuitIOException {
      throw new CircuitIOException("Illegal data found in file.");
   }
   
   private void getProlog(InputStream in) throws CircuitIOException {
      if (!getLine(in).equals("This is an xLogicCircuits data file.") ||
                    !getLine(in).equals("DO NOT EDIT THIS FILE!"))
          inputError("File is not a legal xLogicCircuits file.+Illegal header found in first two lines of file.");
      if (!getLine(in).equals("File format version 1.0"))
          inputError("File seems to require a newer version+of xLogicCircuits.  This version cannot+read it.");
   }
   
   private void putProlog(PrintStream out) {
      out.println("This is an xLogicCircuits data file.");
      out.println("DO NOT EDIT THIS FILE!");
      out.println("File format version 1.0");
   }
   
   
   private Vector getCircuitStack(InputStream in) throws CircuitIOException {
      Vector circuits = new Vector();
      int ct = getInt(in);
      if (ct < 0)
         inputError();
      for (int i = 0; i < ct; i++) {
         char ch = getNonBlank(in);
         if (ch == '@') {
            if (i == 0)
               inputError();
            int pos = getInt(in);
            Circuit parent = (Circuit)circuits.lastElement();
            if (pos < 0 || pos >= parent.items.size())
               inputError();
            CircuitItem cirItem = (CircuitItem)parent.items.elementAt(pos);
            if (! (cirItem instanceof Circuit) )
               inputError();
            Circuit cir = (Circuit)cirItem;
            cir.iconified = false;
            cir.boundingBoxInContainer = getFloatRect(in);
            cir.saveContainerWhileEnlarged = parent;
            circuits.addElement(cir);
         }
         else if (ch == '=') {
            if (i != 0)          // added later to prevent having more than one main circuit on the circuit stack
               inputError();
            Circuit cir = getCircuit(in,null);
            cir.iconified = false;
            circuits.addElement(cir);
         }
         else
            inputError();
      }
      return circuits;
   }

   private Vector getScrollItems(InputStream in) throws CircuitIOException {
      Vector circuits = new Vector();
      int ct = getInt(in);
      if (ct < 0)
         inputError();
      for (int i = 0; i < ct; i++)
         circuits.addElement(getCircuit(in,null));
      return circuits;
   }

   private void putCircuitData(PrintStream out, Vector circuitStack, Circuit currentCircuit, Vector scrollItems) {
      putInt(out,circuitStack.size()+1);
      out.println();
      for (int i = 0; i <= circuitStack.size(); i++) {
         Circuit cir = (i < circuitStack.size())? (Circuit)circuitStack.elementAt(i) : currentCircuit;
         if (cir.saveContainerWhileEnlarged != null) {
            out.print('@');
            putInt(out,cir.saveContainerWhileEnlarged.items.indexOf(cir));
            putFloatRect(out,cir.boundingBoxInContainer);
            out.println();
         }
         else {
            out.print('=');
            putCircuit(out,cir);
         }
      }
      putInt(out,scrollItems.size());
      out.println();
      for (int i = 0; i < scrollItems.size(); i++)
         putCircuit(out, (Circuit)scrollItems.elementAt(i));
   }
   
   

   private Circuit getCircuit(InputStream in, Line[] parentLines) throws CircuitIOException {
      Circuit circuit = new Circuit();
      circuit.iconified = true;  // can be changed in getCircuitStack()
      circuit.name = getLine(in);
      circuit.boundingBox = getFloatRect(in);
      circuit.savedBoundingBox = getFloatRect(in);
      int itemCt = getInt(in);
      int lineCt = getInt(in);
      int inputCt = getInt(in);
      int outputCt = getInt(in);
      if (itemCt < 0 || lineCt < 0 || inputCt < 0 || outputCt < 0)
         inputError();
      Line[] lines = new Line[lineCt];
      for (int i = 0; i < lineCt; i++)
         lines[i] = new Line(null,null);
      circuit.inputs = new Vector(inputCt);
      for (int i = 0; i < inputCt; i++)
         circuit.inputs.addElement(getCircuitIONub(in,true,lines,parentLines));
      circuit.outputs = new Vector(outputCt);
      for (int i = 0; i < outputCt; i++)
         circuit.outputs.addElement(getCircuitIONub(in,false,lines,parentLines));
      circuit.items = new Vector(itemCt);
      for (int i = 0; i < itemCt; i++) {
         char code = getNonBlank(in);
         CircuitItem item;
         if (code == 'T')
            item = getTack(in,lines);
         else if (code == '*')
            item = getCircuit(in,lines);
         else
            item = getGate(in,code,lines);
         circuit.items.addElement(item);
      }
      for (int i = 0; i < lineCt; i++)
         if (lines[i].source == null || lines[i].destination == null)
            inputError();
      circuit.lines = new Vector(lineCt);
      for (int i = 0; i < lineCt; i++)
         circuit.lines.addElement(lines[i]);
      return circuit;
   }

   private void putCircuit(PrintStream out, Circuit circuit) {
      out.println(circuit.name);
      int itemCt = circuit.items.size();
      int lineCt = circuit.lines.size();
      int inputCt = circuit.inputs.size();
      int outputCt = circuit.outputs.size();
      putFloatRect(out,circuit.boundingBox);
      out.println();
      putFloatRect(out,circuit.savedBoundingBox);
      out.println();
      out.println(itemCt + " " + lineCt + " " + inputCt + " " + outputCt);
      for (int i = 0; i < lineCt; i++)
         ((Line)circuit.lines.elementAt(i)).pos = i;
      for (int i = 0; i < inputCt; i++)
         putCircuitIONub(out,(CircuitIONub)circuit.inputs.elementAt(i));
      for (int i = 0; i < outputCt; i++)
         putCircuitIONub(out,(CircuitIONub)circuit.outputs.elementAt(i));
      for (int i = 0; i < itemCt; i++) {
         CircuitItem item = (CircuitItem)circuit.items.elementAt(i);
         if (item instanceof Gate)
            putGate(out,(Gate)item);
         else if (item instanceof Circuit) {
            out.print('*');
            putCircuit(out,(Circuit)item);
         }
         else
            putTack(out,(Tack)item);
      }
   }
   

   private void putTack(PrintStream out, Tack tack) {
      out.print('T');
      putInt(out,tack.connect_x);
      putInt(out,tack.connect_y);
      if (tack.source == null)
         putInt(out,-1);
      else
         putInt(out,tack.source.pos);
      putDestination(out,tack.destination);
      out.println();
   }
   
   private Tack getTack(InputStream in, Line[] lines)  throws CircuitIOException {
      Tack tack = new Tack();
      tack.connect_x = getInt(in);
      tack.connect_y = getInt(in);
      tack.boundingBox = new FloatRect(tack.connect_x - 2, tack.connect_y - 2, 5, 5);
      tack.source = getSource(in,tack,lines);
      tack.destination = getDestination(in,tack,lines);
      return tack;
   }
   

   private void putCircuitIONub(PrintStream out, CircuitIONub nub) {
      if (nub.kind == IONub.OUTPUT)
         out.print('>');
      else
         out.print('<');
      putInt(out,nub.side);
      putFloat(out,nub.absolutePosition);
      if (nub.source == null)
         putInt(out,-1);
      else
         putInt(out,nub.source.pos);
      putDestination(out,nub.destination);
      out.println();
   }
   
   private CircuitIONub getCircuitIONub(InputStream in, boolean input, Line[] lines, Line[] parentLines)  throws CircuitIOException {
      char ch = getNonBlank(in);
      if (input && ch != '<')
         inputError();
      else if (!input && ch != '>')
         inputError();
      int side = getInt(in);
      if (side < 0 || side > 3)
         inputError();
      float position = getFloat(in);
      if (position < 0 || position > 1)
         inputError();
      CircuitIONub nub = new CircuitIONub(side,position,input);
      if (input) {
         nub.source = getSource(in,nub,parentLines);
         nub.destination = getDestination(in,nub,lines);
      }
      else {
         nub.source = getSource(in,nub,lines);
         nub.destination = getDestination(in,nub,parentLines);
      }
      return nub;
   }
   

   private void putGate(PrintStream out, Gate gate) {
      if (gate.kind == Gate.NOTGATE)
         out.print('N');
      else if (gate.kind == Gate.ORGATE)
         out.print('R');
      else
         out.print('A');
      putInt(out,gate.facing);
      putFloatRect(out,gate.boundingBox);
      if (gate.in1.source == null)
         putInt(out,-1);
      else
         putInt(out,gate.in1.source.pos);
      if (gate.kind != Gate.NOTGATE) {
         if (gate.in1.source == null)
           putInt(out,-1);
         else
           putInt(out,gate.in2.source.pos);
      }
      putDestination(out,gate.out.destination);
      out.println();
   }

   private Gate getGate(InputStream in, char typeCode, Line[] lines)  throws CircuitIOException {
      Gate gate = null;
      int facing = getInt(in);
      if (facing < 0 || facing > 3)
         inputError();
      if (typeCode == 'N')
         gate = new Gate(Gate.NOTGATE,facing);
      else if (typeCode == 'A')
         gate = new Gate(Gate.ANDGATE,facing);
      else if (typeCode == 'R')
         gate = new Gate(Gate.ORGATE,facing);
      else
         inputError();
      gate.boundingBox = getFloatRect(in);
      gate.in1.source = getSource(in,gate.in1,lines);
      if (typeCode != 'N')
         gate.in2.source = getSource(in,gate.in2,lines);
      gate.out.destination = getDestination(in,gate.out,lines);
      return gate;
   }

   
   private void putDestination(PrintStream out, Vector destination) {
      putInt(out,destination.size());
      for (int i = 0; i < destination.size(); i++) {
         Line line = (Line)destination.elementAt(i);
         putInt(out,line.pos);
      }         
   }
   
   private Vector getDestination(InputStream in, IONub source, Line[] lines) throws CircuitIOException {
      int size = getInt(in);
      if (size < 0)
         inputError();
      if (size > 0 && lines == null)
         inputError();
      Vector des = new Vector(size);
      for (int i = 0; i < size; i++) {
         int linePos = getInt(in);
         if (linePos < 0 || linePos >= lines.length)
            inputError();
         des.addElement(lines[linePos]);
         lines[linePos].source = source;
      }
      return des;
   }
   
   private Line getSource(InputStream in, IONub destination, Line[] lines)  throws CircuitIOException {
      int linePos = getInt(in);
      if (linePos < 0)
         return null;
      if (lines == null || linePos >= lines.length)
         inputError();
      lines[linePos].destination = destination;
      return lines[linePos];
   }


   private FloatRect getFloatRect(InputStream in) throws CircuitIOException {
      return new FloatRect(getFloat(in),getFloat(in),getFloat(in),getFloat(in));
   }
   
   private void putFloatRect(PrintStream out, FloatRect r) {
      putFloat(out,r.x);
      putFloat(out,r.y);
      putFloat(out,r.width);
      putFloat(out,r.height);
   }
   
   private float getFloat(InputStream in) throws CircuitIOException {
      int n = getInt(in);
      return (float)n/8192.0F;
   }
   
   private void putFloat(PrintStream out, float x) {
      out.print(Math.round(x*8192.0F));
      out.print(' ');
   }
   
   private int getInt(InputStream in) throws CircuitIOException {
      int n = 0;
      boolean neg = false;
      char ch = getNonBlank(in);
      if (ch == '-') {
         neg = true;
         ch = getChar(in);
      }
      if (ch < '0' || ch > '9')
         inputError();
      do {
         n = 10*n + ch - '0';
         ch = getChar(in);
      } while (ch >= '0' && ch <= '9');
      return (neg? -n : n);
   }
   
   private void putInt(PrintStream out, int x) {
      out.print(x);
      out.print(' ');
   }
   
   private char getChar(InputStream in) throws CircuitIOException {
      int ch = 0;
      try {
         ch = in.read();
      }
      catch (IOException e) {
         inputError();
      }
      if (ch == -1)
         inputError("Illegal data in file;+unexpected end of file encountered.");
      return (char)ch;
   }
   
   private char getNonBlank(InputStream in) throws CircuitIOException {
      char ch;
      do {
         ch = getChar(in);
      } while (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t');
      return ch;
   }

   private String getLine(InputStream in) throws CircuitIOException {
      StringBuffer buf = new StringBuffer();
      char ch;
      do {
         ch = getChar(in);
      } while (ch == '\n' || ch == '\r');
      do {
         buf.append(ch);
         ch = getChar(in);
      } while (ch != '\n' && ch != '\r');
      return buf.toString().trim();
   }
   
   
}