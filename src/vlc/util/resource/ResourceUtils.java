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
import java.util.List;

// Application specific imports
// none

/**
 * A collection of utility functions for extracting resources and turning those
 * into meaningful values.
 * <p>
 *
 * The resource manager produces all properties as strings. Unfortunately a
 * lot of the time we really want numbers or colour values. These are 
 * quite frequently encountered and this class provides a single point to
 * place all the common conversion routines.
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @author  Justin Couch
 * @version 1.0 (5 June 2000)
 */
public class ResourceUtils
{ 
  /** The resource manager that we are getting stuff from */
  private static ResourceManager m_resources;

  static
  {
    m_resources = ResourceManager.getResourceManager();
  }

  /**
   * A private constructor to prevent instantiation of this class.
   */
  private ResourceUtils()
  {
  }

  /**
   * Fetch an int value that the given property name describes. If the
   * value does not exist or is a bad format then use the default value.
   *
   * @param prop The fully qualified property name to fetch
   * @param def The default value in case something breaks
   */
  public static int fetchInt(String prop, int def)
  {
    int ret_val = def;

    Object val = m_resources.getResource(prop);

    if(val instanceof String)
    {
      try
      {
        ret_val = Integer.parseInt((String)val);
      }
      catch(NumberFormatException nfe)
      {
        // ignored
      }
    }

    return ret_val;
  }
    
  /**
   * Fetch a float value that the given property name describes. If the
   * value does not exist or is a bad format then use the default value.
   *
   * @param prop The fully qualified property name to fetch
   * @param def The default value in case something breaks
   */
  public static float fetchFloat(String prop, float def)
  {
    float ret_val = def;

    Object val = m_resources.getResource(prop);

    if(val instanceof String)
    {
      try
      {
        ret_val = Float.parseFloat((String)val);
      }
      catch(NumberFormatException nfe)
      {
        // ignored
      }
    }

    return ret_val;
  }

  /**
   * Fetch a boolean value that the given property name describes. If the
   * value does not exist or is a bad format then use the default value.
   *
   * @param prop The fully qualified property name to fetch
   * @param def The default value in case something breaks
   */
  public static boolean fetchBoolean(String prop, boolean def)
  {
    boolean ret_val = def;

    Object val = m_resources.getResource(prop);

    if(val instanceof String)
    {
      Boolean bool = Boolean.valueOf((String)val);
      ret_val = bool.booleanValue();
    }

    return ret_val;
  }

  /**
   * Convenience method to take a list of items and turn them into a
   * delimited list suitable for storage.
   * @param list a list of Strings
   * @return a string containing the elements of the given list delimited
   * by the delimiter character
   */
  public static String listToDelimitedString(List list)
  {
    return ManagerUtils.listToDelimitedString(list);
  }

}
