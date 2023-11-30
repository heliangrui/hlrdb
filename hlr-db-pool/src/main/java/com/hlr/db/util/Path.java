package com.hlr.db.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * Path
 * Description:
 * date: 2023/11/29 13:47
 *
 * @author hlr
 */
public class Path {

    public static String getPathFromClass(Class cls) throws IOException {
        String path = null;
        if (cls == null) {
            throw new NullPointerException();
        } else {
            URL url = getClassLocationURL(cls);
            if (url != null) {
                path = url.getPath();
                if ("jar".equalsIgnoreCase(url.getProtocol())) {
                    try {
                        path = (new URL(path)).getPath();
                    } catch (MalformedURLException var4) {
                    }

                    int location = path.indexOf("!/");
                    if (location != -1) {
                        path = path.substring(0, location);
                    }
                }

                File file = new File(path);
                path = file.getCanonicalPath();
            }

            return path;
        }
    }

    public static List getClasses(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        } else if (!file.isDirectory()) {
            return null;
        } else {
            String[] tempList = file.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if ((new File(dir, name)).isDirectory()) {
                        return false;
                    } else {
                        return name.indexOf(".class") != -1;
                    }
                }
            });
            List<String> list = new ArrayList();

            for (int i = 0; i < tempList.length; ++i) {
                list.add(tempList[i].substring(0, tempList[i].lastIndexOf(46)));
            }

            return list;
        }
    }

    public static String getFullPathRelateClass(String relatedPath, Class cls) throws IOException {
        String path = null;
        if (relatedPath == null) {
            throw new NullPointerException();
        } else {
            String clsPath = getPathFromClass(cls);
            File clsFile = new File(clsPath);
            String tempPath = clsFile.getParent() + File.separator + relatedPath;
            File file = new File(tempPath);
            path = file.getCanonicalPath();
            return path;
        }
    }

    private static URL getClassLocationURL(Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null input: cls");
        } else {
            URL result = null;
            String clsAsResource = cls.getName().replace('.', '/').concat(".class");
            ProtectionDomain pd = cls.getProtectionDomain();
            if (pd != null) {
                CodeSource cs = pd.getCodeSource();
                if (cs != null) {
                    result = cs.getLocation();
                }

                if (result != null && "file".equals(result.getProtocol())) {
                    try {
                        if (!result.toExternalForm().endsWith(".jar") && !result.toExternalForm().endsWith(".zip")) {
                            if ((new File(result.getFile())).isDirectory()) {
                                result = new URL(result, clsAsResource);
                            }
                        } else {
                            result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
                        }
                    } catch (MalformedURLException var6) {
                    }
                }
            }

            if (result == null) {
                ClassLoader clsLoader = cls.getClassLoader();
                result = clsLoader != null ? clsLoader.getResource(clsAsResource) : ClassLoader.getSystemResource(clsAsResource);
            }

            return result;
        }
    }
}
