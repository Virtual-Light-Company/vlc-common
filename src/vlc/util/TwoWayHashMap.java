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

package vlc.util;

import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A version of HashMap that allows the user to look up in both directions
 * for key and value.
 * <P>
 *
 * In many cases you want to be able to look up something by the key and
 * some by the value to get the key because both are important. For example,
 * The file extension to MIME type.
 * <P>
 *
 * The implementation can get nasty if you have two keys that have the same
 * value. To deal with this, there is an explicit method for looking up the
 * reverse value. This method may return a ValueSet object, which is all of the
 * matching keys for this value. If there is only one, then the actual value
 * is returned.
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @author  Justin Couch
 * @version 1.0 (5 June 2000)
 */
public class TwoWayHashMap extends HashMap
{
  /** Hashmap to hold all of the reverse mapping */
  private HashMap reverse_map;

  /**
   * A simple inner class that derives from HashSet. This is used purely for
   * type safety, just in case someone decides that the "value" of the
   * forward mapping will be an ordinary Set. We need to know the difference.
   */
  class ValueSet extends HashSet
  {
  }

  /**
   * Create a new hash map with the default capacity and load factor.
   */
  public TwoWayHashMap()
  {
    reverse_map = new HashMap();
  }

  /**
   * Create a new hash map with the given initial capacity.
   *
   * @param initialCapacity The number of items to have in the map
   */
  public TwoWayHashMap(int initialCapacity)
  {
    super(initialCapacity);

    reverse_map = new HashMap(initialCapacity);
  }

  /**
   * Create a new hash map with the given initial capacity and a load
   * factor for rehashing.
   *
   * @param initialCapacity The number of items to have in the map
   * @param loadFactor Percentage [0 - 1] of the capacity when rehashing
   *   takes place
   */
  public TwoWayHashMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);

    reverse_map = new HashMap(initialCapacity, loadFactor);
  }

  /**
   * Put a new value and key into the hash table.
   */
  public Object put(Object key, Object value)
  {
    Object c_value = super.get(key);

    // Now check the reverse map and remove the old key
    if(c_value != null)
    {
      Object cr_value = reverse_map.get(c_value);

      if(cr_value instanceof ValueSet)
      {
        Set cr_set = (Set)cr_value;
        cr_set.remove(key);

        // If there's only one item in the set then change the set to
        // just the instance.
        if(cr_set.size() == 1)
        {
          Iterator itr = cr_set.iterator();
          reverse_map.put(value, itr.next());
        }
      }
      else
      {
        reverse_map.remove(c_value);
      }
    }

    // now insert the new key/value into the reverse map
    Object r_value = reverse_map.get(value);

    if(r_value == null)
      reverse_map.put(value, key);
    else if(r_value instanceof ValueSet)
    {
      Set r_set = (Set)r_value;
      r_set.add(key);
    }
    else
    {
      // There is one entry so we need to replace that with a dd
      ValueSet r_set = new ValueSet();
      r_set.add(key);
      r_set.add(r_value);
      reverse_map.put(value, r_set);
    }

    // Finally put the normal version into our map
    return super.put(key, value);
  }

  /**
   * Remove a value from the map.
   */
  public Object remove(Object key)
  {
    Object value = super.remove(key);

    Object r_value = reverse_map.get(value);

    if(r_value instanceof ValueSet)
    {
      Set r_set = (Set)r_value;
      r_set.remove(key);

      // If there's only one item in the set then change the set to
      // just the instance.
      if(r_set.size() == 1)
      {
        Iterator itr = r_set.iterator();
        reverse_map.put(value, itr.next());
      }
    }
    else
    {
      // there's only a single object, so remove it from the reverse map too.
      reverse_map.remove(value);
    }

    return value;
  }

  /**
   * Do a reverse lookup of the mapping. If the mapping has two keys that
   * reference the same value then you will get a ValueSet back that contains
   * both of the keys back.
   */
  public Object reverseGet(Object value)
  {
    return reverse_map.get(value);
  }

 /***********
  public static void main(String[] argv)
  {
    TwoWayHashMap map = new TwoWayHashMap();

    map.put("key1", "value1");
    map.put("key2", "value2");

    System.out.println("Test value 1: " + map.get("key1"));
    System.out.println("Test value 2: " + map.get("key2"));
    System.out.println("Test key 1  : " + map.reverseGet("value1"));
    System.out.println("Test key 2  : " + map.reverseGet("value2"));

    map.put("key3", "value1");

    System.out.println("\nNow with multiple values");
    System.out.println("Test value 1: " + map.get("key1"));
    System.out.println("Test value 2: " + map.get("key2"));
    System.out.println("Test value 3: " + map.get("key3"));
    System.out.println("Test key 1  : " + map.reverseGet("value1"));
    System.out.println("Test key 2  : " + map.reverseGet("value2"));

    map.remove("key2");
    System.out.println("\nRemoving a key");
    System.out.println("Test value 1: " + map.get("key1"));
    System.out.println("Test value 2: " + map.get("key2"));
    System.out.println("Test key 1  : " + map.reverseGet("value1"));
    System.out.println("Test key 2  : " + map.reverseGet("value2"));

    map.put("key2", "value2");
    map.remove("key1");
    System.out.println("\nRemoving multiple key");
    System.out.println("Test value 1: " + map.get("key1"));
    System.out.println("Test value 2: " + map.get("key2"));
    System.out.println("Test value 3: " + map.get("key3"));
    System.out.println("Test key 1  : " + map.reverseGet("value1"));
    System.out.println("Test key 2  : " + map.reverseGet("value2"));
  }
****************/

}
