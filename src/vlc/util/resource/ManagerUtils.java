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
import java.util.Iterator;
import java.util.List;

// Application specific imports
// none

/**
 * A collection of internal utilities that are needed by the resource
 * manager and associated classes.
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 *  @author  Justin Couch
 *  @version 1.0 (5 June 2000)
 */
final class ManagerUtils
  implements ResourceConstants
{
  /**
   * Returns the type of node for the given name.
   * @param name the name of a single node
   * @return the type of this node, either BY_SINGLE_MATCH, BY_NAME, BY_CLASS
   */
  static int getNodeType(String name)
  {
    // if the node starts with an uppercase character or contains a '/' then
    // it is a class name, else if it is '?' it is a single match, otherwise
    // it is an ordinary name.
    int nodeType = (name.equals(SINGLE_MATCH)) ? BY_SINGLE_MATCH :
                    (Character.isUpperCase(name.charAt(0)) ||
                    (name.indexOf('/') != -1)) ?        BY_CLASS :
                                                        BY_NAME;

    return nodeType;
  }

  /**
   * Takes a list as argument, and converts it to a delimited string.
   * This is used in writing resources to disk.
   * @param list a list of Strings
   * @return a string containing the elements of the given list delimited
   * by the delimiter character
   */
  static String listToDelimitedString(List list)
  {
    StringBuffer buf = new StringBuffer();
    String element;
    String delim = " " + DELIMITER + " ";

    // get all elements in the list
    Iterator iterator = list.iterator();

    if (iterator.hasNext())
    {
      // extract the first element
      element = (String) iterator.next();
      buf.append(element);

      // now extract all the rest of the elements
      while(iterator.hasNext())
      {
        element = (String) iterator.next();
        buf.append(delim);
        buf.append(element);
      }
    }

    return buf.toString();
  }
}
