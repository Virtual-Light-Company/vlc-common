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
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.StringTokenizer;

// Application specific imports
// none

/**
 * This class is used for resource management.  A <i>Resource</i> consists of
 * a key-value pair.  These pairs are stored in config files which the
 * application will request a lookup from.  The purpose of the resource
 * management system is to provide facilities for accessing these resources.
 * <p>
 * Below are examples of resource names:
 * <ul>
 *  <li> application.startup.userprefs.file
 *  <li> treeview.node.fgcolour
 *  <li> playback.maindialog.field1.label
 * </ul>
 * The following wildcards are allowed (name refers to the characters between
 * periods ('.'):
 * <ul>
 *  <li> '*' which matches any number of names
 *  <li> '?' which matches exactly one name
 * </ul>
 * <p>
 * Resource names can also be specified using the absolute java class name
 * (though '/' must be used as package delimiter).  Because java classes do
 * not have to appear in a package, it is convention that java classes must
 * begin with an Uppercase character to distinguish class and instance names.
 * Consequently, resource names that do NOT refer to java classes must NOT
 * begin with an uppercase character<br>
 * e.g.
 * <ul>
 *  <li> *java/awt/Button.bgcolour
 * </ul>
 * Refers to the background colour of all buttons in the application.
 * <p>
 * Objects which wish to request resources, may register themselves with
 * the internal registry of this Manager.  e.g. An object which wishes to only
 * request the resources:
 * <ul>
 *  <li> application.startup.home.dir
 *  <li> application.startup.security
 * </ul>
 * Can register itself with the manager as only requiring resources prefixed by
 * 'application.startup', then will be able to query with "home.dir" and
 * "security"
 * <p>
 * Objects used for graphical display should register themselves with the
 * manager by specifying their parent.  This allows for the hierarchical nature
 * of the resource manager to function to its fullest.  This then allows for
 * complex resources such as:
 * <ul>
 *  <li> *panel1*bgColour : red
 *  <li> *panel1*java/awt/TextField.bgColour : blue
 * </ul>
 * Which says that all graphical objects under panel1 are to have a red
 * background, except any textfields which are to have a blue background.
 * <hr>
 * Here is a detailed example of using the resource system when creating a
 * new user interface component.
 * <p>
 * Lets say that we are creating a new toolbar with lots of fancy stuff, like
 * buttons, textfields, checkboxes.  We should set up resources for things like
 * the background and foreground colours, labels to go on the UI components.
 * These should be stored as resources, rather than hardcoding values.
 * <p>
 * For this example let us say that we have this situation:<br>
 * <ul>
 *  <li> Our toolbar class is application.gui.Toolbar.
 *  <li> The toolbar contains two java.awt.Buttons - which we name
 *       <em>button1</em> and <em>button2</em>.  The reason we must name the
 *       buttons, is to uniquely identify them.
 *  <li> The toolbar also contains a java.awt.TextField - which we name
 *       <em>status</em> as it will output the current status of things.
 * </ul>
 * The resources that we need are the background and foreground colours of
 * all components (the toolbar, the buttons and the textfield), and the labels
 * for the buttons.  For brevity in this example we will only show the
 * foreground resource and ignore the background.
 * <p>
 * Now, the buttons and textfield are uniquely named, but what about the
 * toolbar itself?  The toolbar is a reusable component so it is the
 * responsibility of the component <strong>using</strong> the toolbar to give
 * it a name.  The toolbar <strong>cannot</strong> assign its own name as it
 * doesn't have enough information to determine it.
 * <p>
 * Let's now have a look at some sample code to be implemented in the
 * constructor of the toolbar.
 * <p>
 * <code><pre>
 * public class Toolbar
 * {
 *   private TextField statusField;
 *   private Button leftButton;
 *   private Button rightButton;
 *
 *   public Toolbar()
 *   {
 *     super();
 *
 *     // create the subcomponent the toolbar uses
 *     statusField = new TextField();
 *     leftButton = new Button();
 *     rightButton = new Button();
 *
 *     // we cannot get resources yet, as this object hasn't been linked
 *     // to its parent
 *
 *     // add these components to the toolbar
 *     add(statusField);
 *     add(leftButton);
 *     add(rightButton);
 *
 *     // continue initialising the toolbar...
 *   }
 *
 *   public void init()
 *   {
 *
 *     // get the resource manager
 *     ResourceManager manager = ResourceManager.getResourceManager();
 *
 *     //
 *     // register all objects hierarchically with the ResourceManager
 *     //
 *
 *     // this toolbar is the parent of the textfield known as status
 *     manager.registerObject("status", statusField, this);
 *
 *     // this toolbar is the parent of the button known as button1
 *     manager.registerObject("button1", leftButton, this);
 *
 *     // this toolbar is the parent of the button known as button2
 *     manager.registerObject("button2", rightButton, this);
 *
 *     //
 *     // Note that we haven't registered the toolbar with the resource
 *     // manager.  It is the responsibility of the components using
 *     // the toolbar to do that BEFORE calling this init() method.
 *     // If the component does NOT register the toolbar, the resource
 *     // management system will not function.
 *     //
 *
 *     Color color;
 *
 *     // now set the foreground for the toolbar
 *     color = Color.decode(manager.getResource("foreground", this));
 *     this.setForeground(color);
 *
 *     // set labels for buttons
 *     leftButton.setLabel(manager.getResource("label", leftButton));
 *     rightButton.setLabel(manager.getResource("label", rightButton));
 *
 *     // set foreground colour for child buttons and textfield
 *
 *     color = Color.decode(manager.getResource("foreground", leftButton));
 *     leftButton.setForeground(color);
 *
 *     // an alternate way
 *     color = Color.decode(manager.getResource("button2.foreground", this));
 *     rightButton.setForeground(color);
 *
 *     color = Color.decode(manager.getResource("foreground", statusField));
 *     statusField.setForeground(color);
 *    }
 *  }
 * </pre></code>
 * <hr>
 * Now lets have a look at the resource file for these resources
 * <code><pre>
 * # Sample default resource file for the toolbar
 *
 * # Default foreground for our toolbar is red
 * *application/gui/Toolbar.foreground = red
 *
 * # Specify resources for button 1
 * *application/gui/Toolbar.button1.label = This is the left button
 * *application/gui/Toolbar.button1.foreground = green
 *
 * # An alternative way.  This will still work, but the former is preferred
 * *button2.label = This is the right button
 * *button2.foreground = blue
 *
 * # Resources for the status textfield
 * *application/gui/Toolbar.status.foreground = yellow
 * </pre></code>
 * <hr>
 * Now lets have a look at how we use the Toolbar class.
 * <code><pre>
 * public class MySpecialDisplay
 * {
 *   public MySpecialDisplay()
 *   {
 *     ResourceManager manager = ResourceManager.getResourceManager();
 *
 *     // specify the resource prefix for this display
 *     manager.registerObject(this, "application.display.special");
 *
 *     Toolbar tb = new Toolbar();
 *
 *     // now link this object with the child
 *     manager.registerObject("my_toolbar", tb, this);
 *
 *     // now initialise toolbar.
 *     // as the objects are linked, the child can now use the resource
 *     // management system.
 *     tb.init();
 *
 *     // continue with constructor
 *   }
 * }
 * </pre></code>
 * <hr>
 * <strong>Note</strong> that we have to register the toolbar as a child
 * to the display <strong>BEFORE</strong> we can access the resource
 * manager in the toolbar.  This means that we can not access resources
 * in the constructor of the toolbar, but must access them afterwards.
 * <p>
 * Why is this?  Consider a resource set to:
 * <ul>
 *  <li> application.display.special.my_toolbar.button2.foreground = puce
 * </ul>
 * The toolbar will not be able to recognise this resource until AFTER it
 * has been registered as a child of the display.
 * <hr>
 * <p>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 *  @author  Justin Couch
 *  @version 1.0 (5 June 2000)
 */
public class ResourceManager
  implements ResourceConstants
{
  /** Print debug messages to stderr? */
  protected final static boolean DEBUG = false;

  /** The root of the resource tree */
  protected Node root;

  /** ResourceManager is a singleton */
  protected static ResourceManager _instance = null;

  /**
   * Protected Constructor.
   */
  protected ResourceManager()
  {
    createRootNode();
    createNameObjectMaps();
  }

  /**
   * Returns the current resource manager
   * @return the Resource Manager
   */
  public static ResourceManager getResourceManager()
  {
    if (_instance == null)
      _instance = new ResourceManager();

    return _instance;
  }

  /**
   * Removes all resources from the manager.
   */
  public synchronized void clearResources()
  {
    // create a new root, making old tree eligible for GCing
    createRootNode();

    // clear all registered objects
    unRegisterAll();
  }

  /**
   * Reads all resources from the given stream.
   * The resources are stored in the internal registry.  If a particular
   * resource already exists in the registry, then it is overwritten.
   * @param stream stream to read resources from
   */
  public synchronized void readResources(InputStream stream)
  {
    Properties prop = new Properties();
    Enumeration keys;
    String key, value;
    List values;

    try
    {
      // load the properties
      prop.load(stream);
      keys = prop.propertyNames();

      while(keys.hasMoreElements())
      {
        // extract this key and value
        key = (String) keys.nextElement();
        value = prop.getProperty(key).trim();

        // only store valid resources
        if (isValidResource(key))
        {
          // store the key/value pair
          setTheResource(root, key, value);
        }
        else if (DEBUG)
        {
          // inform the user of the ill formed resource
          System.err.println("ResourceManager.readResources(): "+
                             "the key '"+key+"' is illformed - skipping");
        }
      }
    }
    catch(IOException e)
    {
      if (DEBUG)
        System.err.println("ResourceManager.readResources(): "+e.getMessage());
    }
  }

  /**************** Methods to handle registration of objects ****************/

  /**
   * Removes all references to registered objects.
   */
  public synchronized void unRegisterAll()
  {
    clearNameObjectMaps();
  }

  /**
   * Remove all references of the given object from the internal registry.
   * Does nothing if the object does not exist in the registry.
   * @param object the object to remove
   */
  public synchronized void unRegisterObject(Object object)
  {
    removeObjectFromMaps(object);
  }

  /**
   * Registers the given object with the manager.  The object is associated
   * with the given name.  This name is used by the manager to make searching
   * for resources easier.  The name should be a single "word" containing no
   * spaces. e.g.  "button1" or "billboard".  Note that the name is NOT the
   * classname of the object, and must follow the naming convention specified
   * at the top of this file (e.g. must start with a lowercase letter).
   * <P>
   * Note: This method of registering an object should rarely be used, and
   * is only really useful to retrieve resources that are specified by class.
   * @param name the name this object will be known by
   * @param object reference to the object
   * @exception IllegalArgumentException if the name contains invalid chars
   * @exception NullPointerException if the given object is null
   */
  public synchronized void registerObject(String name, Object object)
    throws IllegalArgumentException,
           NullPointerException
  {
    if (containsSubString(name, TIGHT_BINDING))
    {
      // user has used the wrong method to register this object.
      registerObject(object, name);
    }
    else
    {
      // check that name doesn't contain illegal characters
      if (!isValidName(name))
      {
        throw new IllegalArgumentException("ResourceManager.registerObject(): "+
                                          "name '" +name+ "' can not contain "+
                                          "the whitespace, or the characters " +
                                          " '"+LOOSE_BINDING+"', '"+
                                          SINGLE_MATCH+ "'");
      }

      if (object == null)
      {
        throw new NullPointerException("ResourceManager.registerObject(): "+
                                       "object is null");
      }

      // store this mapping
      storeObject(name, object);
    }
  }

  /**
   * Registers the child of an already registered object with the manager.
   * Providing the object's parent allows for a hierarchical order to the
   * resource names.  The child object is associated with the given name.  This
   * name is used by the manager to make searching for resources easier.  The
   * name should be a single "word" containing no spaces. e.g.  "button1" or
   * "billboard".  Note that the name is NOT the classname of the object, and
   * must follow the naming convention specified at the top of this file (e.g.
   * must start with a lowercase letter).
   * @param name the name the child object will be known by
   * @param child reference to the child object
   * @param object reference to the object which is the parent (in the resource
   * hierarchy)
   * @exception IllegalArgumentException if the name contains invalid
   * characters.  Also if the given parent does not exist in the internal
   * registry
   * @exception NullPointerException if the given object is null
   */
  public synchronized void registerObject(String name, Object child,
                                          Object object)
    throws IllegalArgumentException,
           NullPointerException
  {
    if (object == null)
    {
      throw new NullPointerException("ResourceManager.registerObject(): "+
                                     "parent object is null");
    }

    // determine the absolute name of the parent
    String parentName = retrieveAbsoluteName(object);

    if (parentName == null)
    {
      throw new IllegalArgumentException("ResourceManager.registerObject(): "+
                                         "parent not registered with manager");
    }
    else
    {
      // register the object under the fully qualified name
      registerObject(child, parentName+TIGHT_BINDING+name);
    }
  }

  /**
   * Registers the given object with a specified resource prefix.  This prefix
   * is used when searching for resources.  Multiple objects may have the same
   * prefix.  An example of a prefix is <em>application.modules</em>  .  The
   * difference between registering by name, and registering by prefix is that
   * name is a single level of the resource hierarchy, while prefix is the
   * absolute path to a level in the resource hierarchy. e.g. :
   * <ul>
   *  <li> <em>prefix</em> - application.startup.modules
   *  <li> <em>name</em> - either application, or startup or modules
   * </ul>
   * @param name the name this object will be known by
   * @param object reference to the object
   * @param prefix prefix used for this object
   * @exception IllegalArgumentException if the name or prefix contains invalid
   * characters
   * @exception NullPointerException if the given object is null
   */
  public synchronized void registerObject(Object object, String prefix)
    throws IllegalArgumentException,
           NullPointerException
  {
    if (!isValidResource(prefix))
    {
      throw new IllegalArgumentException("ResourceManager.registerObject(): "+
                                         "root '" + prefix +
                                         "' is invalid and can not contain "+
                                         "the characters "+
                                         LOOSE_BINDING+ " "+SINGLE_MATCH);
    }
    if (object == null)
    {
      throw new NullPointerException("ResourceManager.registerObject(): "+
                                     "object is null");
    }

    // store this mapping
    storeObject(prefix, object);
  }

  /***************** Methods to handle resource retrieval ********************/

  /**
   * Retrieves the given resource relative to the specified object.
   * @param resource the resource to retrieve relative to the object
   * @param object the object to resource relates to
   * @return a <strong>String</strong> if there is only one value for this
   * resource, a <strong>List</strong> if there are multiple values, and
   * <strong>null</strong> if there are no values.
   * @exception IllegalArgumentException if resource is illformed
   */
  public synchronized Object getResource(String resource, Object object)
    throws IllegalArgumentException
  {
    Object result = null;

    String prefix = retrieveAbsoluteName(object);
    if (prefix != null)
    {
      result = getResource(prefix+TIGHT_BINDING+resource);
    }
    else
    {
      // the object has not been registered, so we perform a tricky manouver
      // here, by registering the object, performing the search then
      // unregistering the object

      if (object != null) {
        // store this mapping
        storeObject(UNIQUE_NAME, object);
        result = getResource(UNIQUE_NAME+TIGHT_BINDING+resource);
        removeObjectFromMaps(object);
      }
    }

    return result;
  }

  /**
   * Retrieves the given absolute resource.
   * @param resource the resource to retrieve
   * @return a <strong>String</strong> if there is only one value for this
   * resource, a <strong>List</strong> if there are multiple values, and
   * <strong>null</strong> if there are no values.
   * @exception IllegalArgumentException if resource is illformed
   */
  public synchronized Object getResource(String resource)
    throws IllegalArgumentException
  {
    // ensure resource is well formed
    if (!isValidResource(resource))
    {
      throw new IllegalArgumentException("Resource '"+resource+
                                         "' is illformed");
    }

    Object retval = null;

    Node node = findNode(root, ensureWellFormed(resource), "");

    // resource found
    if (node != null)
    {
      String data = node.getData();
      if (containsSubString(data, DELIMITER))
        retval = getValuesAsList(data);
      else
        retval = data;
    }

    // change returned resources of "" to the null reference
    if ((retval != null) && retval.equals(""))
      retval = null;

    if (DEBUG && (retval == null))
    {
      System.err.println("ResourceManager.getResource(\"" + resource +
                         "\") returned null");
    }

    // return either the string, or list stored in the node
    return retval;
  }

  /**************** Methods to handle storage of resources *******************/

  /**
   * Sets the given resource, overriding existing resources.
   * @param key the absolute resource name
   * @param value the single value of this resource, or null to remove this
   * resource
   * @exception IllegalArgumentException if the key contains illegal characters
   */
  public synchronized void setResource(String key, String value)
    throws IllegalArgumentException
  {
    // set the resource under the root node
    setTheResource(root, key, value);

    // inform listeners
    resourceSet(key);
  }

  /**
   * Sets the given resource, overriding existing resources.
   * @param key the absolute resource name
   * @param values a list of values (These must be strings), or null to remove
   * the resource
   * @exception IllegalArgumentException if the key contains illegal characters
   */
  public synchronized void setResource(String key, List values)
    throws IllegalArgumentException
  {
    // set the resource under the root node
    setTheResource(root, key, values);

    // inform listeners
    resourceSet(key);
  }


  /**
   * Sets the given resource, overriding existing resources.  The resource
   * is relative to the given object, where the object has been registered
   * with the internal registry.
   * @param object the object
   * @param key the resource name relative to the object
   * @param value the single value of this resource, or null to remove the
   * resource
   * @exception IllegalArgumentException if the key contains illegal characters
   * @exception NullPointerException if the given object is null
   */
  public synchronized void setResource(Object object, String key, String value)
    throws IllegalArgumentException,
           NullPointerException
  {
    setResourcePrivate(object, key, value);
  }

  /**
   * Sets the given resource, overriding existing resources.  The resource
   * is relative to the given object, where the object has been registered
   * with the internal registry.
   * @param object the object
   * @param key the resource name relative to the object
   * @param values a list of values (These must be strings), or null to remove
   * the resource
   * @exception IllegalArgumentException if the key contains illegal characters
   * @exception NullPointerException if the given object is null
   */
  public synchronized void setResource(Object object, String key, List values)
    throws IllegalArgumentException,
           NullPointerException
  {
    setResourcePrivate(object, key, values);
  }

  /**
   * Sets the given resource, overriding existing resources.  The resource
   * is relative to the given object, where the object has been registered
   * with the internal registry.
   * @param object the object
   * @param key the resource name relative to the object
   * @param value the single value of this resource, or null to remove the
   * resource
   * @exception IllegalArgumentException if the key contains illegal characters
   * @exception NullPointerException if the given object is null
   */
  private void setResourcePrivate(Object object, String key, Object value)
    throws IllegalArgumentException,
           NullPointerException
  {
    if (object == null)
    {
      throw new NullPointerException("ResourceManager.setResource(): "+
                                     "object is null");
    }

    // determine the name of the object
    String name = retrieveAbsoluteName(object);

    if (name == null)
    {
      throw new IllegalArgumentException("ResourceManager.setResource(): "+
                                         "object '"+object+"' not registered"+
                                         "with manager");
    }

    String res = name+TIGHT_BINDING+key;

    // set resource under the node
    Node node = getNode(name);
    if (node == null)
    {
      // the node was not found, so create it as we go
      setTheResource(root, res, value);

      // inform listeners
      resourceSet(res);
    }
    else
    {
      // set resource from the node
      setTheResource(node, key, value);

      // inform listeners
      resourceSet(res);
    }
  }

  /**
   * Notification that a resource was set.
   * @param resource the resource that was set.
   */
  protected void resourceSet(String resource)
  {
    // nothing to do
  }

  /**
   * This method does all the guts of inserting a new resource into the
   * resource tree.  It traverses the tree, creating intermediate nodes
   * as required.
   * @param startNode where in the tree we are currently at
   * @param resource resource string as it stands from under this node
   * @param value the value to insert into this resource
   * @exception IllegalArgumentException if the resource name is illegal
   */
  private void setTheResource(Node startNode, String resource, Object value)
    throws IllegalArgumentException
  {
    if (!isValidResource(resource))
    {
      throw new IllegalArgumentException("ResourceManager.setResource(): " +
                                         "resource '" + resource + 
                                         "' is not well formed");
    }

    // ensure that the resource value is a string
    String stringValue;
    if (value instanceof List)
      stringValue = ManagerUtils.listToDelimitedString((List) value);
    else
      stringValue = (String) value;

    setTheResourceRecursive(startNode,
                            ensureWellFormed(resource),
                            stringValue);
  }

  /**
   * This method does all the guts of inserting a new resource into the
   * resource tree.  It traverses the tree, creating intermediate nodes
   * as required.
   * @param startNode where in the tree we are currently at
   * @param resource resource string as it stands from under this node
   * @param value the value to insert into this resource
   * @exception IllegalArgumentException if the resource name is illegal
   */
  private void setTheResourceRecursive(Node startNode, String resource,
                                       String value)
    throws IllegalArgumentException
  {
    int idx;

    Node newNode;
    String name;
    HashMap hashmap;

    int tightIndex = resource.indexOf(TIGHT_BINDING);

    // 1st character may be '*' so skip over it
    int looseIndex = resource.indexOf(LOOSE_BINDING, LOOSE_BINDING_LEN);

    // if no delimiter was found, set a sentinel value that is tested for
    // later
    if (tightIndex == -1)
      tightIndex = Integer.MAX_VALUE;
    if (looseIndex == -1)
      looseIndex = Integer.MAX_VALUE;


    // find the index of the first delimiter ('.' or '*')
    int pos = (tightIndex < looseIndex)?tightIndex:looseIndex;

    // is this the last name in the resource string?
    boolean atLastName = (pos == Integer.MAX_VALUE);

    // if the first character is '*' then we have a loose binding
    boolean looseBinding = resource.startsWith(LOOSE_BINDING);

    // determine the name of this node, ensure we don't include the delimiter
    // in the name.
    // create the node for this name
    if (looseBinding)
    {
      name = (atLastName) ? resource.substring(LOOSE_BINDING_LEN) :
                            resource.substring(LOOSE_BINDING_LEN, pos);
    }
    else
    {
      name = (atLastName) ? resource : resource.substring(0, pos);
    }

    String binding = looseBinding ? LOOSE_BINDING : TIGHT_BINDING;
    newNode = startNode.createChild(name, binding);

    // if this is the last component of the resource then store the value
    if (atLastName)
    {
      newNode.setData(value);
    }
    else
    {
      // if loosely bound, ensure that '*' is the first character of the tail
      String tail;
      if (resource.regionMatches(pos, LOOSE_BINDING, 0, LOOSE_BINDING_LEN))
        tail = resource.substring(pos);
      else
        tail = resource.substring(pos+LOOSE_BINDING_LEN);

      // recurse to next level
      setTheResourceRecursive(newNode, tail, value);
    }
  }

  /******************** Method for traversal of the tree *********************/

  /**
   * Traverses the tree from the current node finding the given relative
   * resource name.  This is a recursive method.
   * @param currentNode the current node in the tree to search from
   * @param resourceName resource relative from this node to find.  This
   * resource contains only tightly bound names.
   * @param currentResourcePrefix the name of the current node, if this is null
   * then the exact resourceName will be sought.  A non null value will mean
   * that the search will attempt to lookup the type of object to do searches
   * by class.
   * @return the desired node, or null if it is not found
   */
  private Node findNode(Node currentNode, String resourceName,
                        String currentResourcePrefix)
  {
    // if the resourceName is null then assume the given node is
    // the node that we seek
    if (resourceName == null)
      return currentNode;

    String head, tail;
    String[] res = splitResource(resourceName, TIGHT_BINDING);
    head = res[0];
    tail = res[1];

    HashMap hashmap;
    Node childNode;
    Node theNode = null;

    // this is a depth first search with backtracking.
    // again the children are searched in the order specified earlier

    String searchName = head;

    // determine the name of the node we will be searching
    String newName;
    newName = (currentResourcePrefix.equals("")) ? head :
                                currentResourcePrefix+TIGHT_BINDING+head;

    // first search for an exact tight match
    childNode = currentNode.getExactChild(searchName, TIGHT_BINDING);
    if (childNode != null)
    {
      if ( (theNode = findNode(childNode, tail, newName)) != null)
        return theNode;
    }

    // there was no exact match for the name, so next attempt will be
    // the class of the object
    if (ManagerUtils.getNodeType(searchName) == BY_NAME)
    {
      // get a reference to the object registered with this resource name
      // and obtain the absolute java class name to search by
      Object obj = retrieveObject(currentResourcePrefix, searchName);
      if (obj != null)
        searchName = obj.getClass().getName().replace('.', '/');
    }

    // next search for the closest tight match
    childNode = currentNode.getClosestChild(searchName, TIGHT_BINDING);
    if (childNode != null)
    {
      if ( (theNode = findNode(childNode, tail, newName)) != null)
        return theNode;
    }

    // we are now about to search the children that are loosely bound.
    // since loosely bound children can skip bits of a resource name,
    // we generate all possible resouce (sub)names and try them

    String[] heads;
    String[] tails;
    int count;

    if (tail == null)
    {
      // resource name consists of only one component so there is only
      // one possible subname
      count = 0;
      heads = new String[1];
      tails = new String[1];
      heads[0] = head;
      tails[0] = tail;
    }
    else
    {
      // generate all possible head/tails of the resource subnames
      // there is one more possibilities than delimiters

      count = countDelimiters(resourceName, TIGHT_BINDING) + 1;

      heads = new String[count];
      tails = new String[count];

      String resource = resourceName;

      // create an array of heads & tails
      for(int i=0; i<count; i++)
      {
        res = splitResource(resource, TIGHT_BINDING);
        heads[i] = res[0];
        tails[i] = res[1];
        resource = tails[i];
      }
    }

    newName = currentResourcePrefix;
    String prefix = currentResourcePrefix;

    // now search loosely bound children for matches
    for(int i=0; i<count; i++)
    {
      searchName = heads[i];
      newName = (newName.equals("")) ? heads[0] :
                                       newName+TIGHT_BINDING+heads[i];

      // first search for an exact tight match
      childNode = currentNode.getExactChild(searchName, LOOSE_BINDING);
      if (childNode != null)
      {
        if ( (theNode = findNode(childNode, tails[i], newName)) != null)
          return theNode;
      }

      // there was no exact match for the name, so next attempt will be
      // the class of the object
      if (ManagerUtils.getNodeType(searchName) == BY_NAME)
      {
        // get a reference to the object registered with this resource name
        // and obtain the absolute java class name to search by
        Object obj = retrieveObject(prefix, searchName);
        if (obj != null)
          searchName = obj.getClass().getName().replace('.', '/');
      }

      // next search for the closest tight match
      childNode = currentNode.getClosestChild(searchName, LOOSE_BINDING);
      if (childNode != null)
      {
        if ( (theNode = findNode(childNode, tails[i], newName)) != null)
          return theNode;
      }

      prefix = (prefix.equals("")) ? heads[0] :
                                       prefix+TIGHT_BINDING+heads[i];
    }

    // didn't find a match
    return null;
  }

  /*************************** Utility methods *******************************/

  /**
   * Creates a new root node.  If there is an existing tree, invoking this
   * method will remove it.
   */
  private void createRootNode()
  {
    root = new Node("root", null);
    Node.clear();
  }

  /**
   * Returns the node that matches the given resource exactly.  If there is
   * no exact match, then null is returned.
   * @param resource the absolute resource to search
   * @return the Node representing the resource, or null if node
   */
  private Node getNode(String resource)
  {
    return Node.getNode(resource);
  }

  /**
   * Tests the given resource to see if it is well formed or not.  The
   * definition of a well formed resource is at the top of this file.
   * @param resource the resource name to test
   * @return true if well formed, false otherwise
   */
  public boolean isValidResource(String resource)
  {
    // check for illegal combinations of characters
    // this is probably not a complete check, but hopefully it is good enough
    if ( (resource == null) ||
         (resource.equals("")) ||
         (resource.startsWith(TIGHT_BINDING)) ||
         (resource.endsWith(TIGHT_BINDING)) ||
         (resource.endsWith(LOOSE_BINDING)) ||
         containsSubString(resource, TIGHT_BINDING+TIGHT_BINDING) ||
         containsSubString(resource, SINGLE_MATCH+SINGLE_MATCH) ||
         containsSubString(resource, " ") ||
         containsSubString(resource, "\t") ||
         containsSubString(resource, "\n") )
    {
      return false;
    }

    return true;
  }

  /**
   * Tests the given name to see if it is suitable to be used to register
   * an object under.  A suitable name must not contain any whitespace,
   * or the binding characters '*', '.', '?'.
   * @param name the name to test
   * @return true if valid, false otherwise
   */
  public boolean isValidName(String name)
  {
    // check for illegal combinations of characters
    // this is probably not a complete check, but hopefully it is good enough
    if ( (name == null) ||
         (name.equals("")) ||
         Character.isUpperCase(name.charAt(0)) ||
         containsSubString(name, TIGHT_BINDING) ||
         containsSubString(name, LOOSE_BINDING) ||
         containsSubString(name, SINGLE_MATCH) ||
         containsSubString(name, "/") ||
         containsSubString(name, " ") ||
         containsSubString(name, "\t") ||
         containsSubString(name, "\n") ||
         name.equals(UNIQUE_NAME) )
    {
      return false;
    }

    return true;
  }

  /**
   * Returns whether the given string contains the specified substring.
   * @param str the string
   * @param subStr the substring
   * @return true if the substring is contained in the given string, false
   * otherwise
   */
  private boolean containsSubString(String str, String subStr)
  {
    if (str == null)
      return false;

    return (str.indexOf(subStr) != -1);
  }

  /**
   * Ensures that the given string is well formed by replacing all occurances
   * of '.*' and '*.' with '*'.
   * name1*.name2 is illformed, it should be name1*name2, however the former
   * is accepted as valid user input.
   * @param resource the resource
   * @return the resource with given substitutions
   */ 
  private String ensureWellFormed(String res)
  {
    String resource = res;
    int idx;

    // replace all occurances of '.*' with '*'
    while( (idx = resource.indexOf(TIGHT_BINDING+LOOSE_BINDING)) != -1)
    {
      if (idx == 0)
      {
        resource = LOOSE_BINDING +
                   resource.substring(TIGHT_BINDING_LEN+LOOSE_BINDING_LEN);
      }
      else
      {
        resource = resource.substring(0, idx) + LOOSE_BINDING +
                   resource.substring(idx+TIGHT_BINDING_LEN+LOOSE_BINDING_LEN);
      }
    }

    // replace all occurances of '*.' with '*'
    while( (idx = resource.indexOf(LOOSE_BINDING+TIGHT_BINDING)) != -1)
    {
      if (idx == 0)
      {
        resource = LOOSE_BINDING +
                   resource.substring(TIGHT_BINDING_LEN+LOOSE_BINDING_LEN);
      }
      else
      {
        resource = resource.substring(0, idx) + LOOSE_BINDING +
                   resource.substring(idx+LOOSE_BINDING_LEN+TIGHT_BINDING_LEN);
      }
    }

    return resource;
  }

  /**
   * Counts the number of delimiters in a given resource.
   * @param resource the resource
   * @param delimiter the delimiter to count, either TIGHT_BINDING, or
   * LOOSE_BINDING
   * @return the number of occurances of this delimiter in the resource
   */
  private int countDelimiters(String resource, String delimiter)
  {
    int delLen = (delimiter.equals(TIGHT_BINDING)) ? TIGHT_BINDING_LEN :
                                                     LOOSE_BINDING_LEN ;

    // find the first delimiter
    int index = resource.indexOf(delimiter);

    if (index != -1)
      return 1 + countDelimiters(resource.substring(index+delLen), delimiter);
    else
      return 0;
  }

  /**
   * Splits the given resource into a head and tail at the first occurance
   * of the specified delimiter.
   * @param resource the resource
   * @param delimiter the delimiter to count, either TIGHT_BINDING, or
   * LOOSE_BINDING
   * @return String[2], where String[0] is the head, and String[1] is the
   * tail.  If there is no occurance of the delimiter, then String[0] will
   * be the given resource, and String[1] will be null
   */
  private String[] splitResource(String resource, String delimiter)
  {
    String[] retval = new String[2];
    String head, tail;

    int delLen = (delimiter.equals(TIGHT_BINDING)) ? TIGHT_BINDING_LEN :
                                                     LOOSE_BINDING_LEN ;
    // find the first delimiter
    int index = resource.indexOf(delimiter);

    if (index != -1)
    {
      // split into head and tail
      head = resource.substring(0, index);
      tail = resource.substring(index+delLen);
    }
    else
    {
      // there is no tail, only a head
      head = resource;
      tail = null;
    }

    retval[0] = head;
    retval[1] = tail;

    return retval;
  }

  /**
   * Takes a list of values delimited by the DELIMITER character, and returns
   * these values as a list.
   * @param values the multiple values
   * @return these values in a list
   */
  protected final List getValuesAsList(String values)
  {
    List list = new LinkedList();
    StringTokenizer tok = new StringTokenizer(values, DELIMITER);

    while(tok.hasMoreTokens())
    {
      // extract each value, trimming whitespace
      list.add(tok.nextToken().trim());
    }

    // return the list
    return list;
  }

  /********************** Methods for writing resources **********************/

  /**
   * Writes all internal resources to the given output stream.
   * @param stream where to write the resources to
   */
  public synchronized void writeResources(OutputStream stream)
  {
    writeResources(stream, null);
  }

  /**
   * Writes all internal resources to the given output stream.
   * @param stream where to write the resources to
   * @param header Header to prepend to the stream
   */
  public synchronized void writeResources(OutputStream stream, String header)
  {
    PrintStream ps = new PrintStream(stream);

    // dump the header
    if (header != null)
    {
      // here we do some sanity checking to ensure that the header
      // has is prepended with # to be recognised as a comment
      StringTokenizer tok = new StringTokenizer(header, "\n");
      String line;

      while(tok.hasMoreTokens())
      {
        line = tok.nextToken();
        if (line.startsWith("#"))
          ps.println(line);
        else
          ps.println("# "+line);
      }
    }

    // dump all the resources
    ps.print(root);
  }

  /**
   * Dumps the entire resource tree.
   * Will print to stdout.  This is used for debugging.
   */
  public void dump()
  {
    dump(null);
  }

  /**
   * Dumps the entire resource tree from the given section of the tree.
   * Will print to stdout.  This is used for debugging.
   * @param prefix where to start dumping, null for the entire tree
   */
  public void dump(String prefix)
  {
    Node node = null;

    if ((prefix != null) && (!prefix.equals("")))
      node = getNode(prefix);

    if (node == null)
      node = root;

    System.out.print(node);
  }

  /****** Private methods for dealing with storing object name mappings ******/

  //
  // Note that the object is stored in the maps using weak references.
  // This is needed incase the object is not unregistered, and is otherwise
  // eligible for garbage collection.
  //

  /**
   * Simple container class containing the name and prefix of an object.
   */
  private class ObjectName
  {
    String name;
    String prefix;
  }

  /**
   * Mapping of object to name details<br>
   * key: Object<br>
   * value: ObjectName
   */
  private WeakHashMap objectNames;

  /**
   * Mapping of absolute name to object<br>
   * key: Absolute String name of the object - prefix+TIGHT_BINDING+name<br>
   * value: WeakReference to the object
   */
  private HashMap nameObjects;

  /**
   * Creates the maps.
   */
  private void createNameObjectMaps()
  {
    objectNames = new WeakHashMap();
    nameObjects = new HashMap();
  }

  /**
   * Clears the maps.
   */
  private void clearNameObjectMaps()
  {
    objectNames.clear();
    nameObjects.clear();
  }

  /**
   * Stores the given object under the specified name in the maps.
   * <strong>Note:</strong> If multiple objects attempt to register under
   * the same prefix, then only the first object to register is stored, in
   * the mapping name -> object.
   * @param name the absolute name (including prefix) of the object
   * @param obj the object
   */
  private void storeObject(String name, Object obj)
  {
    objectNames.put(obj, name);

    if (!nameObjects.containsKey(name))
      nameObjects.put(name, new WeakReference(obj));
  }

  /**
   * Removes the given object from the maps.
   * @param obj the object
   */
  private void removeObjectFromMaps(Object obj)
  {
    String name = (String) objectNames.remove(obj);

    // if multiple objects have registered under the same prefix, ensure
    // that only this object is removed from that mapping
    try
    {
      // retrieve the object that the weak reference refers to, if any
      WeakReference ref = (WeakReference) nameObjects.get(name);
      if (System.identityHashCode(obj) == System.identityHashCode(ref.get()))
        nameObjects.remove(name);
    }
    catch(NullPointerException e0)
    {
      // ignore
    }
  }

  /**
   * Retrieves the objects that is associated with the given name.
   * @param name the absolute name of the object
   * @return the object associated with that name, or null if none
   */
  private Object retrieveObject(String name)
  {
    Object retVal = null;

    // retrieve the object that the weak reference refers to, if any
    WeakReference ref = (WeakReference) nameObjects.get(name);
    if (ref != null)
    {
      retVal = ref.get();
    }
    
    return retVal;
  }

  /**
   * Retrieves the objects that is associated with the given name.
   * This is simply a short cut for<br>
   * <pre><code>
   *  retrieveObject(prefix+TIGHT_BINDING+name);
   * </code></pre>
   * @see #retrieveObject(java.lang.String)
   * @param prefix the prefix of this name
   * @param name the relative name of the object
   * @return the object associated with that name, or null if none
   */
  private Object retrieveObject(String prefix, String name)
  {
    String newName;
    if ((prefix == null) || (prefix.equals("")))
      newName = name;
    else
      newName = prefix+TIGHT_BINDING+name;

    return retrieveObject(newName);
  }

  /**
   * Retrieves the absolute name of the given object.
   * @param obj the object
   * @return the name of that object, or null if none
   */
  private String retrieveAbsoluteName(Object obj)
  {
    return (String) objectNames.get(obj);
  }
}

