package com.mywork.finance.utils.plasticine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Max
 * Date: 23.04.13
 * Time: 10:43
 */
public class ClasspathResourceManager {

    private static Logger logger;
    private String baseResourcePath = null;

    public static ClasspathResourceManager getResourceManager(String baseResourceDirectory) {
        return new ClasspathResourceManager(baseResourceDirectory);
    }

    public static ClasspathResourceManager getResourceManager() {
        return new ClasspathResourceManager();
    }
    
    /**
     * Returns URL for classpath contains {@code classpathName}. If such not found than throws exception.
     * @param classpathName
     * @return first URL (if exists) which contains classpathName. 
     * @throws Exception
     */
    public static URL getClasspathURL(String classpathName) throws Exception {
    	ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls){
        	if (url.getPath().endsWith(classpathName) || url.getPath().endsWith(classpathName + "/")) {
        		LoggerUtil.getLogger(ClasspathResourceManager.class).debug("First URL that contans %s was found: %s", classpathName, url);
        		return url;
        	}
        }
        throw new Exception(String.format("Directory %s wasn't found", classpathName));
    }

    static {
        logger = Logger.getLogger(ClasspathResourceManager.class.getName());
        logger.setLevel(Level.FINEST);
    }

    /**
     * Default constructor.
     *
     * @param baseDirectory - relative path of the resource directory into classpath. Prefered to be at least on one branch below
     *                      the classpath directory.  If root file in classpath need to be loaded, then path must be stated without slash "/".
     */
    public ClasspathResourceManager(String baseDirectory) {
        if (!baseDirectory.equals("") && !baseDirectory.endsWith("/")) {
            baseDirectory = baseDirectory.concat("/");
        }
        this.baseResourcePath = baseDirectory;
    }

    /**
     * Constructs default resources manager which searches resource into all classpath directories.
     */
    public ClasspathResourceManager() {
        this.baseResourcePath = "";
    }

    public URL getFileUrl(String fileName) {
        URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(baseResourcePath + fileName);
        logger.fine(String.format("Loading file from URL: %s", fileUrl));
        return fileUrl;
    }

    /**
     * Returns file object from current resource folder.
     * E.g. you want get file TEXT.txt from classpath "resources/main/resources/TEXT.txt" you may construct {@code ClasspathResourceManager}
     * with {@code baseResourceDirectory} "main/resources" and pass "TEXT.txt" as an argument.
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public File getFile(String filePath) throws IOException, URISyntaxException {
        URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(baseResourcePath + filePath);
        if (fileUrl == null) {
            throw new NullPointerException("Couldn't create URL for the file path: " + baseResourcePath + filePath);
        }
        logger.fine(String.format("Loading file from URL: %s", fileUrl));
        File resourceFile = new File(fileUrl.toURI());

        return resourceFile;
    }

    /**
     * The same as an {@code getFile} but returns {@code InputStream}.
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public InputStream getInputStream(String filePath) throws IOException {
        URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(baseResourcePath + filePath);
        if (fileUrl == null) {
            throw new NullPointerException("Couldn't create URL for the file path: " + baseResourcePath + filePath);
        }
        InputStream is = new FileInputStream(URLDecoder.decode(fileUrl.getPath(), "UTF-8"));
        return is;
    }
    
   
}
