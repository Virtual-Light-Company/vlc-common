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
import java.awt.Insets;

// Application Specific imports
// none

/**
 * Constraint information for components laid out using the
 * {@link vlc.gui.layout.PositionLayout} layout manager.
 * <p>
 *
 * The constraints for each component in a position layout manager are
 * specified with instances of this class. If the width or height is set to -1
 * (the default) then the component will automatically size to its preferred
 * size in that dimension. 
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @author  Justin Couch
 * @version 1.0 (5 June 2000)
 */
public class PositionConstraints
  implements Cloneable
{
    // Constants defining positional attributes 

    /** No constraint for this area */
    public static final int NONE = 0;

    /** Set the constraint to apply to both vertical and horizontal fields */
    public static final int BOTH = 1;

    /** The constraint value only applies to the horizontal field */
    public static final int HORIZONTAL = 2;

    /** The constraint value only applies to the vertical field */
    public static final int VERTICAL = 3;

    /** Anchor the component to the north of the parent component */
    public static final int NORTH = 1;

    /** Anchor the component to the north east of the parent component */
    public static final int NORTHEAST = 2;

    /** Anchor the component to the east of the parent component */
    public static final int EAST = 3;

    /** Anchor the component to the south east of the parent component */
    public static final int SOUTHEAST = 4;

    /** Anchor the component to the south of the parent component */
    public static final int SOUTH = 5;

    /** Anchor the component to the south west of the parent component */
    public static final int SOUTHWEST = 6;

    /** Anchor the component to the west of the parent component */
    public static final int WEST = 7;

    /** Anchor the component to the north west of the parent component */
    public static final int NORTHWEST = 8;

    // Normal variables

    /** The x position to locate this component */
    public int x;

    /** The y position to location this component */
    public int y;

    /** 
     * The width in pixels to size this component to. If -1 will use the 
     * preferred size of the component.
     */
    public int width;

    /** 
     * The height in pixels to size this component to. If -1 will use the 
     * preferred size of the component.
     */
    public int height;

    /** Where to anchor this component relative to the parent component */
    public int anchor;

    /** How to make this component fill the alloted area withing the parent */
    public int fill;

    /** Inset information for this component */
    public Insets insets;

    /**
     * Construct a default instance of this class with the fields set to:<br>
     * x: 0<br>
     * y: 0<br>
     * width: -1<br>
     * height: -1<br>
     * anchor: NONE<br>
     * fill: NONE<br>
     * insets: 0,0,0,0<br>
     */
    public PositionConstraints()
    {
        x = 0;
        y = 0;
        width = -1;
        height = -1;
        anchor = NONE;
        fill = NONE;
        insets = new Insets(0, 0, 0, 0);
    }

    /**
     * Make a clone of this object. Performs a deep copy of all the information
     * so that it may act independently of the original.
     *
     * @return A complete copy of this object
     */
    public Object clone()
    {
        PositionConstraints p = null;
        
        try
        {        
            p = (PositionConstraints)super.clone();
            p.insets = (Insets)insets.clone();
        }
        catch(CloneNotSupportedException cnse)
        {
        }

        return p;
    }
}

