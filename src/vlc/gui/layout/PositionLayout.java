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
import java.awt.*;

import java.util.Hashtable;

// Application Specific imports
// none

/**
 * A constraint based layout manager for laying out components in a fixed X,Y
 * position and size.
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @author  Justin Couch
 * @version 1.0 (5 June 2000)
 *
 * @see vlc.gui.layout.PositionConstraints
 * @see java.awt.LayoutManager
 * @see java.awt.Container
 */
public class PositionLayout
  implements LayoutManager3
{
    /** The constraint should use the prefered size */
    private static final int PREFERRED = 0;

    /** The constraint should use the minimum size set */
    private static final int MINIMUM = 1;

    /** A set of default constraints to be shared around internally */
    private static final PositionConstraints DEFAULT_CONSTRAINTS =
        new PositionConstraints();

    /**
     * The width the layout manager is to use. Setting a value of -1 says
     * to fill to the preferred size of the contained components.
     */
    public int width;

    /**
     * The height the layout manager is to use. Setting a value of -1 says
     * to fill to the preferred size of the contained components.
     */
    public int height;

    /** The mapping of components (key) to their constraints (value) */
    private transient Hashtable m_constraints_map;

    /**
     * Construct a default layout manager that uses the preferred size of the
     * components.
     */
    public PositionLayout()
    {
        this(-1, -1);
    }

    /**
     * Construct a layout manager that has its size fixed to the given values.
     * If a parameter is passed as -1 then the prefered size of the contained
     * components are used.
     *
     * @param w The width to be used in pixels
     * @param h The height to be used in pixels
     */
    public PositionLayout(int w, int h)
    {
        width = w;
        height = h;

        m_constraints_map = new Hashtable();
    }
   
    /**
     * Add a component to the layout - Not used. The user should just call
     * setConstraints() and then use the generic add(Component) method. This is
     * similar to the GridBagLayout style functionality.
     *
     * @param name A name label for the component
     * @param comp The component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
    }
   
    /**
     * Get the constraints for a given component. If the component is not a part
     * of this layout manager then the default values for a PositionLayout are
     * returned. Always returns an instance of 
     * {@link vlc.gui.layout.PositionConstraints}. 
     * 
     * @param The component to get the constraints for
     * @return an instance of PositionLayout describing the constraints
     * @see vlc.gui.layout.PositionConstraints
     */
    public Object getConstraints(Component comp)
    {
        return lookupConstraints(comp).clone();
    }
  
    /**
     * Request that this manager lay out the components for the parent
     * container. Works through all of the container's child components and puts
     * them into the position as defined by these rules.
     *
     * @param parent The container needing to be laid out.
     */ 
    public void layoutContainer(Container parent)
    {
        Component comp;
        Component[] all_comps; 
        PositionConstraints constraints;
        Dimension d, parent_d;
        int x, y;

        all_comps = parent.getComponents();
        Insets insets = parent.getInsets();
        parent_d = parent.getSize();

        for(int i = 0; i < all_comps.length; i++)
        {
            comp = all_comps[i];
            constraints = lookupConstraints(comp);

            x = constraints.x + constraints.insets.left + insets.left;
            y = constraints.y + constraints.insets.top + insets.top;

            d = comp.getPreferredSize();

            // Setup the basic width and heights
            if(constraints.width != -1)
                d.width = constraints.width;
            
            if(constraints.height != -1)
                d.height = constraints.height;
           
            if((constraints.fill == PositionConstraints.BOTH) ||
               (constraints.fill == PositionConstraints.HORIZONTAL))
            {
                x =  insets.left + constraints.insets.left;
                d.width = parent_d .width - constraints.insets.left - 
                          constraints.insets.right - insets.left -
                          insets.right;
            }

            if((constraints.fill == PositionConstraints.BOTH) ||
               (constraints.fill == PositionConstraints.VERTICAL))
            {
                y = insets.top + constraints.insets.top;
                d.height = parent_d.height - constraints.insets.top -
                           constraints.insets.bottom - insets.top -
                           insets.bottom;
            }

            // Now process how we are going to anchor these things on screen.
            switch(constraints.anchor)
            {
                case PositionConstraints.NORTH:
                    x = (parent_d.width - d.width) / 2;
                    y = constraints.insets.top + insets.top;
                    break;

                case PositionConstraints.NORTHEAST:
                    x = parent_d.width - d.width - constraints.insets.right -
                        insets.right;
                    y = constraints.insets.top + insets.top;
                    break;

                case PositionConstraints.EAST:
                    x = parent_d.width - d.width - constraints.insets.right -
                        insets.right;
                    y = (parent_d.height - d.height) / 2;
                    break;

                case PositionConstraints.SOUTHEAST:
                    x = parent_d.width - d.width - constraints.insets.right -
                        insets.right;
                    y = parent_d.height - d.height -
                        constraints.insets.bottom - insets.bottom;
                    break;

                case PositionConstraints.SOUTH:
                    x = (parent_d.width - d.width) / 2;
                    y = parent_d.height - d.height -
                        constraints.insets.bottom - insets.bottom;
                    break;

                case PositionConstraints.SOUTHWEST:
                    x = constraints.insets.left + insets.left;
                    y = parent_d.height - d.height -
                        constraints.insets.bottom - insets.bottom;
                    break;
                    
                case PositionConstraints.WEST:
                    x = constraints.insets.left + insets.left;
                    y = (parent_d.height - d.height) / 2;
                    break;
                   
                case PositionConstraints.NORTHWEST:
                    x = constraints.insets.left + insets.left;
                    y = constraints.insets.top + insets.top;
                    break;

                default:   // default action is to do none 
                    break; 
            }

            comp.setBounds(x, y, d.width, d.height);
        }
    }
   
    /**
     * Get the miminum layout size that this manager reckons we need.
     * 
     * @param parent The parent container to calculate values for
     * @return Dimensions describing the required size
     */
    public Dimension minimumLayoutSize(Container parent)
    {
        return layoutSize(parent, MINIMUM);
    }
    
    /**
     * Get the preferred size that this layout manager would like be.
     *
     * @param parent The parent container to calculate values for
     * @return Dimensions describing the required size
     */
    public Dimension preferredLayoutSize(Container parent)
    {
        return layoutSize(parent, PREFERRED);
    }
     
    /**
     * Remove a component from this layout manager. If the component doesn't
     * exist here it silently fails.
     *
     * @param comp The component to be removed
     */
    public void removeLayoutComponent(Component comp)
    {
        m_constraints_map.remove(comp);
    }
   
    /**
     * Set the constraint for a component to be managed. If it is not a
     * {@link vlc.gui.layout.PositionConstraints} instance then 
     * the manager will not use the component. If no constraints are passed
     * then the default position layout values are used.
     *
     * @param comp The component to be managed
     * @param constraint The constraint to be applied. Must be a
     *   {@link vlc.gui.layout.PositionConstraints} instance
     */
    public void setConstraints(Component comp, Object constraints)
    {
        // quick sanity check
        if(!(constraints instanceof PositionConstraints) &&
           (constraints != null))
            return;
        
        PositionConstraints cons;

        if(constraints == null)
            cons = DEFAULT_CONSTRAINTS;
        else
        {
            cons = (PositionConstraints)constraints;
            cons = (PositionConstraints)cons.clone();
        }

        m_constraints_map.put(comp, cons);

        // :IMPLEMENTATION
        // Deal nicely with nested layout managers, just in case. Note that
        // the original code used the JavaBeans framework to determine whether
        // the parent container was a Container instance. That code was ugly and
        // really messy. As I doubt we'll be using _any_ beans in this system,
        // the much simpler code below is used. See the @see tags in the class
        // documentation if you want to see the original source
        if(comp instanceof Container)
        {
            LayoutManager mgr = ((Container)comp).getLayout();
            
            if(mgr instanceof PositionLayout)
            {
                
              PositionLayout pl = (PositionLayout)mgr;

              pl.width = cons.width;
              pl.height = cons.height;
            }        
        }
    }
  
    /**
     * Clear all of the components constrainted by this layout manager. The
     * internal storage list is cleared of all values, effectively making this
     * a fresh instance.
     */
    public void clear()
    {
        m_constraints_map.clear();
    }
    
    /**
     * Determine the size of the layout that the parent wants to use. Can be
     * used to determine either the miminum layout or the prefered size by
     * changing the second parameter.
     *
     * @param parent The container that we are managing
     * @param type One of PREFERRED or MINIMUM
     * @return The dimensions of this layout
     */
    private Dimension layoutSize(Container parent, int type)
    {
        int new_w = 0, new_h = 0;

        if((width == -1) || (height == -1))
        {
            Component comp;
            Component[] all_comps = parent.getComponents();
            PositionConstraints constraints;
            Dimension d;
            int x, y;
           
            // Loop through all the components and determine the max
            // width, height and the x,y position.
            for(int i = 0; i < all_comps.length; i++)
            {
                comp = all_comps[i];
                constraints = lookupConstraints(comp);

                if(type == PREFERRED)
                    d = comp.getPreferredSize();
                else
                    d = comp.getMinimumSize();

                if(constraints.width != -1)
                    d.width = constraints.width;
                
                if(constraints.height != -1)
                    d.height = constraints.height;

                if(constraints.anchor == PositionConstraints.NONE)
                {
                    x = constraints.x;
                    y = constraints.y;
                }
                else
                {
                    x = constraints.insets.left;
                    y = constraints.insets.top;
                }

                if((constraints.fill != PositionConstraints.BOTH) &&
                   (constraints.fill != PositionConstraints.HORIZONTAL))
                    new_w = Math.max(new_w, d.width);
                else
                    new_w = Math.max(new_w,
                                    d.width + constraints.insets.left +
                                        constraints.insets.right);

                if((constraints.fill != PositionConstraints.BOTH) &&
                   (constraints.fill != PositionConstraints.VERTICAL))
                    new_h = Math.max(new_w, d.height);
                else
                    new_h = Math.max(new_h,
                                    d.height + constraints.insets.top +
                                        constraints.insets.bottom);
            } 

            if(width != -1)
                new_w = width;

            if(height != -1)
                new_h = height;
        }
        else
        {
            // both width and height are set, so just take those values.
            new_w = width;
            new_h = height;
        }

        Insets insets = parent.getInsets();

        Dimension ret_val = new Dimension(new_w + insets.left + insets.right,
                                          new_h + insets.top + insets.bottom);

        return ret_val;
    }

    /**
     * Look up the constraints for a particular component.
     *
     * @param comp The component to find constraints for
     * @return The costraint objects. If none found then use the defaults
     */
    private PositionConstraints lookupConstraints(Component comp)
    {
        PositionConstraints p =
           (PositionConstraints)m_constraints_map.get(comp);

        // If we couldn't find any then set the default constraints for the
        // next time that we do a lookup
        if(p == null)
        {
            setConstraints(comp, DEFAULT_CONSTRAINTS);
            p = DEFAULT_CONSTRAINTS;
        }

        return p;
    }
    
    /**
     * Make a clone of this object. Just returns an empty instance with the
     * width and height set, but none of the components. If the clone failed
     * then <code>null</code> is returned.
     */
    public Object clone()
    {
        PositionLayout ret_val = null;
                
        try
        {
            ret_val = (PositionLayout)super.clone();
            ret_val.m_constraints_map = new Hashtable();
        }
        catch(CloneNotSupportedException cnse)
        {
            // ignored
        }

        return ret_val;
    }
}

