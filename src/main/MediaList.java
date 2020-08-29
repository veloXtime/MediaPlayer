package main;

import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// class for the list of media files in a directory
public class MediaList {

    private ArrayList<File> mediaList;
    private int curnInd = 0;
    private String songName;
    private String songPath;

    // if the directory is null
    public MediaList(File selectedDirec) {
        try {
            mediaList = new ArrayList<File>();
            File [] files = selectedDirec.listFiles();
            for (File f : files) {
                String filePath = f.getAbsolutePath();
                if (filePath.endsWith(".mp3") || filePath.endsWith(".mp4") || filePath.endsWith(".wav")) {
                    mediaList.add(f);
                }
            }

            if (mediaList.size() == 0) {
                AlertWindow aw = new AlertWindow();
                aw.display("Warning", "No valid media file found, reselect directory.");
            }
            else {
                if (mediaList.get(curnInd)!= null) {
                    songName = mediaList.get(curnInd).getName();
                    songPath = mediaList.get(curnInd).getAbsolutePath();
                }
            }
        }
        catch (Exception e) {
            System.out.println("ERROR: failed to read in file");
        }
    }

    public String getSongName() {
        if (testValidity()) {
            return mediaList.get(curnInd).getName();
        }
        return "";
    }

    public String getSongPath() {
        if (testValidity()) {
            System.out.println(curnInd);
            System.out.println(mediaList.size());
            return mediaList.get(curnInd).toURI().toString();
        }
        return "";
    }

    public void changeCurnInd(int indChange) {
        curnInd += indChange;
    }

    public boolean testValidity() {
        if (curnInd < mediaList.size() && curnInd >= 0) {
            return true;
        }
        return false;
    }
}
