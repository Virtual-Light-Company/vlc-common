/*****************************************************************************
 *                The Virtual Light Company Copyright (c) 1999
 *                               Java Source
 *
 * This code is licensed under the GNU Library GPL. Please read
 * license.txt
 * for the full details. A copy of the LGPL may be found at
 *
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * Project:    VLC Common core code library
 *
 * Version History
 * Date        TR/IWOR  Version  Programmer
 * ----------  -------  -------  ------------------------------------------
 *
 ****************************************************************************/

package vlc.util;

/**
 * Blocking 'First In First Out' (FIFO) queue.
 * <P>
 *
 * Based on the simple Queue but can be used concurrently by seperate
 * threads. If there are not elements in the queue, getNext() will block until
 * it is not empty.
 *
 * @see vlc.util.Queue
 * @version 1.0
 *
 * @author $Author: justin $
 */
public class BlockingQueue extends Queue
{
    /** A flag to indicate the class is currently undergoing a purge */
    private boolean m_purging = false;

    /**
     * Constructor. Create a simple queue.
     */
    public BlockingQueue()
    {
    }

    /**
     * Add an object to the end of the queue.
     *
     * @param o Object to add.
     */
    public synchronized void add(Object o)
    {
        super.add(o);
        notifyAll();
    }

    /**
     * Return the next element from the front of the queue, and remove it
     * from the queue. Under normal circumstances this method will always
     * return an object, blocking if it has to until something is available.
     * However, sometimes the queue needs to close so we unblock the queue
     * are return null instead.
     *
     * @return element at the front of the queue, will block until
     * queue is not empty.
     */
    public Object getNext()
    {
        while(!m_purging && !hasNext())
        {
            try
            {
                synchronized(this)
                {
                    wait();
                }
            }
            catch(InterruptedException e)
            {
            }
        }

        Object o = super.getNext();
        return o;
    }

    /**
     * Get the next element from the front of the queue.
     *
     * @return element at the front of the queue, null if empty.
     */
    public synchronized Object peekNext()
    {
        return super.peekNext();
    }

    /**
     * Check if queue has more objects.
     *
     * @return true if queue has more objects.
     */
    public synchronized boolean hasNext()
    {
        return super.hasNext();
    }

    /**
     * Return the size of the queue.
     *
     * @return size of queue.
     */
    public synchronized int size()
    {
        return super.size();
    }

    /**
     * Remove all elements from queue. Also unblock those who are waiting for
     * items in the queue. They leave the getNext() method with null.
     */
    public synchronized void purge()
    {
        super.clear();
        m_purging = true;
        notifyAll();
        m_purging = false;

    }

    /**
     * Clear the queue of items. If there are users of the class that are
     * blocked while waiting for elements in the queue, they remain so.
     */
    public synchronized void clear()
    {
        super.clear();
    }
}
