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

import java.util.LinkedList;

/**
 * Simple 'First In First Out' (FIFO) queue. Backend is implemented
 * with a Collections LinkedList.
 * <P>
 *
 * This simple queue does not block if you request an item. If nothing is
 * in the queue then it just returns you a null value.
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @see java.util.LinkedList
 * @author  Justin Couch
 * @version 1.0 (5 June 2000)
 */
public class Queue
{
    /** linked list queue */
    private LinkedList m_queue;

    /**
     * Constructor. Create a simple queue.
     */
    public Queue()
    {
        m_queue = new LinkedList();
    }

    /**
     * Add an element to the end of the queue.
     *
     * @param o Element to add.
     */
    public void add(Object o)
    {
        m_queue.addLast(o);
    }

    /**
     * Return the next element from the front and remove it from the queue.
     *
     * @return element at the from of the queue, or null if empty.
     */
    public Object getNext()
    {
        if (hasNext())
        {
            return m_queue.removeFirst();
        }
        return null;
    }

    /**
     * Return the next element from the front of the queue.
     *
     * @return element at the from of the queue, or null if empty.
     */
    public Object peekNext()
    {
        if (hasNext())
        {
            return m_queue.getFirst();
        }
        return null;
    }

    /**
     * Check if queue has more elements.
     *
     * @return true if queue has more elements.
     */
    public boolean hasNext()
    {
        return (m_queue.size() > 0);
    }

    /**
     * Return the number of elements in the queue.
     *
     * @return size of queue.
     */
    public int size()
    {
        return m_queue.size();
    }

    /**
     * Remove all elements in the queue.
     *
     */
    public void clear()
    {
        m_queue.clear();
    }
}
