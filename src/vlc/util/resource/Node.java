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
import java.util.HashMap;
import java.util.Iterator;

// Application specific imports
// none

/**
 * This is the node that exists in the registry tree.  Each node can have
 * many children, but the children fall into 6 categories.<br>
 * <ol>
 *  <li> byName - This child is tightly bound to its parent and it given its
 *       own name (not a class name).  e.g. frame.dialog1
 *  <li> byClass - This child is tightly bound to its parent and it given a
 *       class name. e.g. frame.java/awt/Dialog
 *  <li> bySingleMatch - This child is tightly bound to its parent and is
 *       not given a specific name. e.g. frame.?
 *  <li> byLooseName - This child is loosely bound to its parent by name.
 *       e.g. frame*dialog1
 *  <li> byLooseClass - This child is loosely bound to its parent by class.
 *       e.g. frame*java/awt/Dialog
 *  <li> byLooseSingleMatch  - This child is loosely bound to its parent and
 *       is not given a specific name. e.g. frame*?
 * </ol>
 * These 6 categories are used to make storing data into the tree faster,
 * and priority searching is made faster also.
 * <p>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 *  @author  Justin Couch
 *  @version 1.0 (5 June 2000)
 */
class Node
  implements ResourceConstants
{
  /**
   * Mapping of absolute resource names to the nodes that represent them.
   * Start the map at a pretty decent size because it will probably get
   * really large anyway so we want to avoid re-hashing as much as possible.
   */
  private static HashMap nodes = new HashMap(100);

  /** All the tightly bound children nodes.  Sorted by category */
  HashMap[] tightlyBoundChildren;

  /** All the loosely bound children nodes.  Sorted by category */
  HashMap[] looselyBoundChildren;

  /** the name of this node */
  private final String name;

  /** the absolute name of this node */
  private final String absName;

  /** contents of this node */
  private String data;

  /**
   * Constructs a new node with the given name.
   * @param name the name of this node e.g. <code>button</code>
   * @param absoluteName the absolute name of this node e.g.
   * <code>application*button</code>.  If this is null, then it designates
   * the top level node
   */
  Node(String name, String absoluteName)
  {
    // save name
    this.name = name;
    this.absName = absoluteName;

    // there are 6 categories of children
    tightlyBoundChildren = new HashMap[NUM_CATEGORIES];
    looselyBoundChildren = new HashMap[NUM_CATEGORIES];

    // store this node in the nodes map
    if (absoluteName != null)
      nodes.put(absoluteName, this);
  }

  /**
   * Creates a bound child node.  The type of this node is
   * based on the name.  If a child node with the given name already
   * exists, then it is not created.
   * <table border>
   *  <th>
   *   <tr>
   *    <td>Node Type</td>
   *    <td>Name Details</td>
   *   </tr>
   *  <tr>
   *   <td>byName</td>
   *   <td>name is all lowercase</td>
   *  </tr>
   *  <tr>
   *   <td>byClass</td>
   *   <td>name starts with uppercase character or contains '/'</td>
   *  </tr>
   *  <tr>
   *   <td>bySingleMatch</td>
   *   <td>name equals '?'</td>
   *  </tr>
   * </table>
   * 
   * @param name the name of the node to create
   * @param binding either TIGHT_BINDING or LOOSE_BINDING to represent
   * whether to create a tightly or loosely bound node
   * @return the reference to the newly created node
   */
  final Node createChild(String name, String binding)
  {
    HashMap[] children = null;
    String newAbsoluteName = null;

    if (binding.equals(TIGHT_BINDING))
    {
      children = tightlyBoundChildren;
      newAbsoluteName = (absName == null) ? name :
                                            absName + TIGHT_BINDING + name;
    }
    else if (binding.equals(LOOSE_BINDING))
    {
      children = looselyBoundChildren;
      newAbsoluteName = (absName == null) ? LOOSE_BINDING + name :
                                            absName + LOOSE_BINDING + name;
    }

    // get the type of the node
    int nodeType = ManagerUtils.getNodeType(name);

    // get the hashmap containing this type of node
    HashMap hashmap = children[nodeType];
    if (hashmap == null)
    {
      // need to create a new hash map to store the new node in
      hashmap = (nodeType == BY_SINGLE_MATCH) ? new SingleMapHashMap() :
                (nodeType == BY_CLASS) ? new ClassHashMap() :
                new HashMap();
      children[nodeType] = hashmap;
    }

    // search the map for a match
    Node newNode = (Node) hashmap.get(name);
    if (newNode == null)
    {
      // node such node exists so create one and add it to the hash map
      newNode = new Node(name, newAbsoluteName);
      hashmap.put(name, newNode);
    }

    return newNode;
  }

  /**
   * Searches for the specified child node.  If the given node is starts with
   * an uppercase letter, or contains '/', then it is assumed to be
   * classname, otherwise it is assumed to be a node name.
   * @param name the name of the node to retrieve
   * @param binding either TIGHT_BINDING or LOOSE_BINDING to represent
   * whether to retrieve a tightly or loosely bound node
   * @return the reference to the node if it exists, or null
   */
  final Node getExactChild(String name, String binding)
  {
    // determine whether to use the tight, or loosely bound children
    HashMap[] children = null;
    if (binding.equals(TIGHT_BINDING))
      children = tightlyBoundChildren;
    else if (binding.equals(LOOSE_BINDING))
      children = looselyBoundChildren;

    // if the node starts with an uppercase character or contains a '/' then
    // it is a class name, else it is an ordinary name.
    int nodeType = ManagerUtils.getNodeType(name);

    Node child = null;

    // now search the map for the child
    HashMap hashmap = children[nodeType];
    if (hashmap != null)
      child = (Node) hashmap.get(name);

    return child;
  }

  /**
   * Searches for the specified child node.  The name is assumed to be
   * the name of a class.
   * an uppercase letter, or contains '/', then it is assumed to be
   * classname, otherwise it is assumed to be a node name.
   * @param name the name of the node to retrieve
   * @param binding either TIGHT_BINDING or LOOSE_BINDING to represent
   * whether to retrieve a tightly or loosely bound node
   * @return the reference to the node if it exists, or null
   */
  final Node getClosestChild(String name, String binding)
  {
    // determine whether to use the tight, or loosely bound children
    HashMap[] children = null;
    if (binding.equals(TIGHT_BINDING))
      children = tightlyBoundChildren;
    else if (binding.equals(LOOSE_BINDING))
      children = looselyBoundChildren;

    // this is a two step process, first attempt to find a matching class,
    // and if that fails, try to find a single match

    Node child = null;

    // firstly attempt to find a compatible class
    ClassHashMap chm = (ClassHashMap) children[BY_CLASS];
    if (chm != null)
      child = (Node) chm.find(name);

    if (child == null)
    {
      // now search for a single match
      HashMap hashmap = children[BY_SINGLE_MATCH];
      if (hashmap != null)
        child = (Node) hashmap.get(name);
    }

    // return the result
    return child;
  }

  /**
   * Sets this Node's data.
   * @param value the value for this node
   */
  final void setData(String value)
  {
    data = value;
  }

  /**
   * Returns this Node's data.
   * @return the data
   */
  final String getData()
  {
    return data;
  }

  /**
   * Clear the current collection of nodes from the hashmap.
   */
  static void clear()
  {
    nodes.clear();
  }

  /**
   * Returns the node that matches the given resource exactly.  If there is
   * no exact match, then null is returned.
   * @param resource the absolute resource to search
   * @return the Node representing the resource, or null if node
   */
  static Node getNode(String resource)
  {
    return (Node)nodes.get(resource);
  }

  /**
   * Returns a string representation of this node and all the children
   * under it.
   * @param string representation
   */
  public String toString()
  {
    StringBuffer buf = new StringBuffer("");
    if (data != null)
    {
      // output in format that can be read back in
      buf.append(absName).append(": ").append(data).append("\n");
    }

    int i;
    Node child;
    HashMap hashmap;
    Iterator iterator;

    // add tightly bound children
    for(i=0; i<NUM_CATEGORIES; i++)
    {
      hashmap = tightlyBoundChildren[i];
      if (hashmap != null)
      {
        iterator = hashmap.values().iterator();
        while(iterator.hasNext())
        {
          child = (Node) iterator.next();
          buf.append(child.toString());
        }
      }
    }

    // add loosely bound children
    for(i=0; i<NUM_CATEGORIES; i++)
    {
      hashmap = looselyBoundChildren[i];
      if (hashmap != null)
      {
        iterator = hashmap.values().iterator();
        while(iterator.hasNext())
        {
          child = (Node) iterator.next();
          buf.append(child.toString());
        }
      }
    }

    return buf.toString();
  }
}

