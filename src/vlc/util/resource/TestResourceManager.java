/*****************************************************************************
 *                The Virtual Light Company Copyright (c) 1999
 *                               Java Source
 *
 * This code is licensed under the GNU Library GPL. Please read license.txt
 * for the full details. A copy of the LGPL may be found at
 *
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * Project:    URI Class libs
 *
 * Version History
 * Date        TR/IWOR  Version  Programmer
 * ----------  -------  -------  ------------------------------------------
 *
 ****************************************************************************/

package vlc.util.resource;

// Standard imports
// none

// Application specific imports
// none

/**
 * Simple test case to test the resource manager.  This is not guaranteed to
 * be an exhaustive test, but should hopefully be enough.
 * <p>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 *  @author  Justin Couch
 *  @version 1.0 (5 June 2000)
 */
class TestResourceManager
{
  public static void main(String[] a)
  {
    ResourceManager man = ResourceManager.getResourceManager();

    // setup the object hierarchy
    java.awt.Frame f = new java.awt.Frame();
    java.awt.Panel p = new java.awt.Panel();
    java.awt.TextField t = new java.awt.TextField();


    System.out.println("Beginning test.  Results follow:\n");
    System.out.println("----------------------------------------------------");
    System.out.println("Testing priority conformance\n");

    man.clearResources();
    man.setResource("*frame.a", "failed");
    man.setResource("frame.a", "passed");
    man.registerObject(f, "frame");

    System.out.println("testing if tight binding has preference over loose "+
                       "binding:         "+man.getResource("frame.a"));

    man.clearResources();
    man.setResource("java/awt/Frame.a", "failed");
    man.setResource("frame.a", "passed");
    man.registerObject(f, "frame");

    System.out.println("testing if name has preference over class:  "+
                       "                        "+ man.getResource("frame.a"));

    man.clearResources();
    man.setResource("?.a", "failed");
    man.setResource("frame.a", "passed");
    man.registerObject(f, "frame");

    System.out.println("testing if name has preference over single match:"+
                       "                   "+ man.getResource("frame.a"));


    man.clearResources();
    man.setResource("java/awt/Frame.a", "passed");
    man.setResource("?.a", "failed");
    man.registerObject(f, "frame");

    System.out.println("testing if class has preference over single wildcard:"+
                       "               "+ man.getResource("frame.a"));

    man.clearResources();
    man.setResource("*java/awt/Frame.a", "passed");
    man.setResource("*a", "failed");
    man.registerObject(f, "frame");

    System.out.println("testing if tight binding has preference over loose "+
                       "binding :        "+ man.getResource("frame.a"));

    man.clearResources();
    man.setResource("*java/awt/Frame.a", "passed");
    man.setResource("*java/awt/Component.a", "failed");
    man.registerObject(f, "frame");

    System.out.println("testing if class hierarchy progresses from bottom up "+
                       ":              "+ man.getResource("frame.a"));

    // now perform 2nd set of tests

    // register the object hierachy
    man.clearResources();
    man.registerObject(f, "a.frame");
    man.registerObject("panel", p, f);
    man.registerObject("textfield", t, p);

    // set the resources
    man.setResource("*a", "A");
    man.setResource("*panel.textfield.b", "B");
    man.setResource("*java/awt/Frame.?.java/awt/TextField.c", "C");
    man.setResource("*panel.java/awt/TextField.d", "D");
    man.setResource("*java/awt/Component.e", "E");
    man.setResource("a.frame.?.java/awt/TextField.f", "F");
    man.setResource("*java/awt/Component.java/awt/Component.g", "G");
    man.setResource("a.b.c.d.e.f.g.h", "H");
    man.setResource("abc.*", "I");
    
    System.out.println("----------------------------------------------------");
    System.out.println("Performing complex integrated test:\n");

    // now test them
    System.out.println("a is: "+man.getResource("a", t));
    System.out.println("b is: "+man.getResource("b", t));
    System.out.println("c is: "+man.getResource("c", t));
    System.out.println("d is: "+man.getResource("d", t));
    System.out.println("e is: "+man.getResource("e", p));
    System.out.println("f is: "+man.getResource("a.frame.panel.textfield.f"));
    System.out.println("g is: "+man.getResource("textfield.g", p));
    System.out.println("h is: "+man.getResource("a.b.c.d.e.f.g.h"));
    System.out.println("i is: "+man.getResource("abc.d"));

    System.out.println("----------------------------------------------------");

    // dump the entire resource tree

    System.out.println("Resource tree is:\n");
    man.dump();

    System.exit(0);
  }
}

