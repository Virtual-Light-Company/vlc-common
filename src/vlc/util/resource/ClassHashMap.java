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

// Application specific imports
// none

/**
 * This class will provide facility to retrieve a class, or its nearest
 * superclass that exists in this hash.  e.g If the query is for
 * java/awt/Panel, and that doesn't exist, then java/awt/Container will
 * be tried - continuing up the hierarchical class tree.
 * <p>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 *  @author  Justin Couch
 *  @version 1.0 (5 June 2000)
 */
class ClassHashMap extends HashMap
{
  /**
   * This method is identical to 'get()' except it will return the lowest
   * object in the class hierarchy, if the specified object does not exist
   */
  public Object find(Object key)
  {
    Class[] interfaces;
    int i;

    Class c = null;
    Object obj = null;

    try
    {
      // get a class instance of the class represented by the key
      c = Class.forName( ((String)key).replace('/','.') );

      while(c != null)
      {
        // search for this class, and return if found
        obj = super.get( c.getName().replace('.','/'));
        if (obj != null)
          return obj;

        // class not found, so try interfaces
        interfaces = c.getInterfaces();
        for(i=0; i<interfaces.length; i++)
        {
          // search for this interface, and return if found
          obj = super.get( interfaces[i].getName().replace('.','/'));
          if (obj != null)
            return obj;
        }

        // not found so try higher up the tree
        c = c.getSuperclass();
      }
    }
    catch(ClassNotFoundException e)
    {
    }

    // didn't find anything
    return null;
  }
}

