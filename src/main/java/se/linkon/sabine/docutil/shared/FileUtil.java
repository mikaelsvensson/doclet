package se.linkon.sabine.docutil.shared;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class FileUtil {

    public static List<File> findFiles(File folder, Pattern filePattern) {
        System.out.println("Finding files in " + folder.getAbsolutePath() + " using " + filePattern.pattern());
        List<File> files = new ArrayList<File>();
        findFiles(folder, filePattern, files);
        return files;
    }

    private static void findFiles(File folder, Pattern filePattern, List<File> resultList) {
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                findFiles(f, filePattern, resultList);
            } else if (f.isFile()) {
                if (filePattern.matcher(f.getName()).matches()) {
                    resultList.add(f);
                }
            }
        }
    }

    public static interface FileGrouper {
        String getGroupId(File file);
    }
    public static String getFileContent(File file) throws IOException {
        FileReader reader = new FileReader(file);
        char[] content = new char[(int) file.length()];
        reader.read(content, 0, content.length);
        return new String(content);
    }

    public static File getFileWithOtherExtension(File file, String newExtension) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot < 0) {
            lastDot = name.length();
        }
        return new File(file.getParent(), name.substring(0, lastDot) + newExtension);
    }

    public static File getFileWithoutExtension(File file) {
        return getFileWithOtherExtension(file, "");
    }

    public static boolean generateThumbnail(File imageFile, int maxWidth, int maxHeight, File output) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        double ratioWidth = 1.0 * maxWidth / width;
        double ratioHeight = 1.0 * maxHeight / height;

        double resizeFactor = Math.min(ratioWidth, ratioHeight);

        if (resizeFactor < 1) {
            int newHeight = (int) (resizeFactor * height);
            int newWidth = (int) (resizeFactor * width);

            BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

            img.createGraphics().drawImage(bufferedImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            ImageIO.write(img, "png", output);
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, List<File>> getFileGroups(File folder, FileFilter fileFilter, FileGrouper grouper) {
        Map<String, List<File>> groups = new TreeMap<String, List<File>>();
        for (File file : folder.listFiles(fileFilter)) {
            String groupId = grouper.getGroupId(file);
            if (!groups.containsKey(groupId)) {
                groups.put(groupId, new LinkedList<File>());
            }
            groups.get(groupId).add(file);
        }
        return groups;
    }
}
