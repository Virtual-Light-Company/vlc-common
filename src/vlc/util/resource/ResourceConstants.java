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
 * A collection of constants used internally by the resource management
 * system.
 * <P>
 *
 * This interface is not intended to be used outside of this package.
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 *  @author  Justin Couch
 *  @version 1.0 (5 June 2000)
 */
interface ResourceConstants
{
  /** delimiter for tightly bound names */
  static final String TIGHT_BINDING = ".";
  static final int TIGHT_BINDING_LEN = TIGHT_BINDING.length();

  /** delimiter for loosely bound names */
  static final String LOOSE_BINDING = "*";
  static final int LOOSE_BINDING_LEN = LOOSE_BINDING.length();

  /** delimiter for a single name match */
  static final String SINGLE_MATCH = "?";
  static final int SINGLE_MATCH_LEN = SINGLE_MATCH.length();

  /** delimiter for multiple values */
  static final String DELIMITER = "|";

  // constants for the children type for node

  /** children bound to parent by name */
  static final int BY_NAME = 0;

  /** children bound to parent by class */
  static final int BY_CLASS = 1;

  /** children bound to parent but not by specific name */
  static final int BY_SINGLE_MATCH = 2;

  /** number of different categories of children */
  static final int NUM_CATEGORIES = 3;

  /** a hopefully unique name used for temporarily registering an object */
  static final String UNIQUE_NAME = "_32lD$SF832GDk%ll123(01SDFl";
}
