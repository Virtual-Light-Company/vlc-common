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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A utility class that boostraps the {@link vlc.util.resource.ResourceManager}
 * with a collection of resource files. 
 * <p>
 *
 * The boostrap mechanism takes the first config file to be loaded and the name
 * of the property that defines further files to be loaded. This information
 * is then used to locate and load the initial config file and then chain load
 * the others that are needed by the system.
 * <P>
 *
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @author  Justin Couch
 * @version 1.0 (5 June 2000)
 */
public class ConfigLoader
{
  /** The class loader that we use to locate the files */
  private static ClassLoader class_loader;
    
  /** The resource manager that everyone is using */
  private static ResourceManager resources;

  /**
   * Static initialiser to setup the various static variables.
   */
  static
  {
    class_loader = ClassLoader.getSystemClassLoader();
    resources = ResourceManager.getResourceManager();
  }

  /**
   * A private constructor to prevent instantiation of this class.
   */
  private ConfigLoader()
  {
  }

  /**
   * Load the resource manager with the given initial file and load any
   * further files defined by the named property. If the property does
   * not exist or the reference passed is <CODE>null</CODE> it will not
   * attempt to load anything. If the initial file cannot be located an
   * exception is thrown.
   *
   * @param initialFile The name of the first file to load
   * @param fileProp The name of the property that defines further files
   *   that should be loaded
   * @param verbose A flag indicating whether the loading process should
   *   make noise (System.err) when config files can't be found
   * @throws FileNotFoundException The initial file could not be found
   * @throws NullPointerException The initial file is <CODE>null</CODE>
   */
  public static void loadConfigFiles(String initialFile,
                                     String fileProp,
                                     boolean verbose)
    throws FileNotFoundException
  {
    if(initialFile == null)
      throw new NullPointerException("The initial file is null");

    loadSingleFile(initialFile);

    // Now fetch the resource and look for the other files.
    if(fileProp == null)
      return;
    
    Object others = resources.getResource(fileProp);

    if(others == null)
      return;

    List file_list;
    
    if(others instanceof String)
    {
      file_list = new ArrayList(1);
      file_list.add(others);
    }
    else
      file_list = (List)others;

    Iterator file_itr = file_list.iterator();
    while(file_itr.hasNext())
    {
      try
      {
        loadSingleFile((String)file_itr.next());
      }
      catch(FileNotFoundException fnfe)
      {
        // just ignore it and head onto the next one.
        if(verbose)
          System.err.println(fnfe.getMessage());
      }
    }
  }

  /**
   * Convenience method that loads just a single property file into
   * the resource manager.
   *
   * @param file The name of the file to load
   * @throws FileNotFoundException The initial file could not be found
   * @throws NullPointerException The initial file is <CODE>null</CODE>
   */
  public static void loadConfigFile(String name)
    throws FileNotFoundException      
  {
    if(name == null)
      throw new NullPointerException("The file name supplied is null");

    loadSingleFile(name);
  }
          
  /**
   * Load a single named file into the resource manager.
   *
   * @param file The name of the file to open
   * @throws FileNotFoundException The initial file could not be found
   */
  private static void loadSingleFile(String name)
    throws FileNotFoundException
  {
    InputStream is = class_loader.getResourceAsStream(name);

    if(is == null)
      throw new FileNotFoundException("The resource file " + name + 
                                      " could not be located");

    resources.readResources(is);
  }
}
