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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

// Application specific imports
// none

/**
 * This is a simple HashMap that only stores one value.  The only methods
 * overridden are 'get', 'put' and 'values', so accessing the other
 * methods will likely cause errors
 * <p>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 *  @author  Justin Couch
 *  @version 1.0 (5 June 2000)
 */
class SingleMapHashMap extends HashMap
{
  private Object value = null;

  public Object put(Object key, Object value)
  {
    // save the value
    this.value = value;

    return null;
  }

  public Object get(Object key)
  {
    // return the value
    return value;
  }

  public Collection values()
  {
    Collection ar = new ArrayList(1);
    ar.add(value);
    return ar;
  }
}

