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

package vlc.gui.layout;

// Standard imports 
import java.awt.Component;
import java.awt.LayoutManager;

// Application Specific imports
// none

/**
 * Basic extended LayoutManager interface for building constraint based
 * layouts.
 * <p>
 *
 * A simple alternative to {@link java.awt.LayoutManager2} that provides for a
 * consistent way of getting and setting constraints for the components in a
 * container.
 * <p>
 * 
 * This code has been sourced from
 * <A HREF="http://www.ibm.com/java/education/position-layout-manager.html">
 *  IBM's Java Education Center
 * </A>
 *  and tweaked to fit the local coding conventions.
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @author  Justin Couch
 * @version 1.0 (5 June 2000)
 */
public interface LayoutManager3 extends LayoutManager, Cloneable
{
  /**
   * Get the constraints for the named object
   *
   * @param comp The target object for the constraints
   * @return The constraints for this object
   */
  public Object getConstraints(Component comp);

  /**
   * Set the constraints for this given component.
   * 
   * @param comp The component to have the constraints set for
   * @param constraints The constraints to be used
   */
  public void setConstraints(Component comp, Object constraints);
}

