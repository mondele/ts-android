package com.door43.translationstudio.core;

import com.door43.translationstudio.App;
import com.door43.util.FileUtilities;

import org.unfoldingword.tools.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.unfoldingword.resourcecontainer.Project;

// TODO: 9/6/16  separate UI components from this class and add listener

/**
 * Created by blm on 7/23/16.
 */
public class ExportUsfm {

    public static final String TAG = ExportUsfm.class.getName();


    /**
     * output target translation to USFM file, returns file name to check for success
     * @param targetTranslation
     * @param destinationFolder
     * @param fileName
     * * @return target zipFileName or null if error
     */
    static public File saveToUSFM(TargetTranslation targetTranslation, File destinationFolder, String fileName) {
        if(destinationFolder == null) {
            destinationFolder = App.getPublicDownloadsDirectory();
        }

        File exportFile = null;
        try {
            exportFile = exportAsUSFM(targetTranslation, destinationFolder, fileName);
        } catch (Exception e) {
            Logger.e(TAG, "Failed to export the target translation " + targetTranslation.getId(), e);
        }
        if( (exportFile == null) || !exportFile.exists()) {
            return null;
        }

        return exportFile;
    }

    /**
     * Exports a target translation as a USFM file
     * @param targetTranslation
     * @param destinationFolder
     * @param fileName
     * @return output file
     */
    static private File exportAsUSFM(TargetTranslation targetTranslation, File destinationFolder, String fileName) throws IOException {
        File tempDir = new File(App.context().getCacheDir(), System.currentTimeMillis() + "");
        tempDir.mkdirs();
        ChapterTranslation[] chapters = targetTranslation.getChapterTranslations();
        PrintStream ps = null;
        String outputFileName = null;
        File chapterFile = null;
        for(ChapterTranslation chapter:chapters) {
            // TRICKY: the translation format doesn't matter for exporting
            FrameTranslation[] frames = targetTranslation.getFrameTranslations(chapter.getId(), TranslationFormat.DEFAULT);
            if(frames.length == 0) continue;

            boolean needNewFile = (ps == null);
            if(needNewFile) {
                BookData bookData = BookData.generate(targetTranslation);
                String bookCode = bookData.getBookCode();
                String bookTitle = bookData.getBookTitle();
                String bookName = bookData.getBookName();
                String languageId = bookData.getLanguageId();
                String languageName = bookData.getLanguageName();

                if((fileName != null) && (!fileName.isEmpty())) {
                    outputFileName = fileName;
                } else {
                    outputFileName = bookData.getDefaultUsfmFileName();
                }

                chapterFile = new File(tempDir, outputFileName);
                chapterFile.createNewFile();

                if(ps != null) {
                    ps.close();
                }
                ps = new PrintStream(chapterFile);

                String id = "\\id " + bookCode + " " + bookTitle + ", " + bookName + ", " + (languageId + ", " + languageName);
                ps.println(id);
                String bookID = "\\toc1 " + bookTitle;
                ps.println(bookID);
                String bookNameID = "\\toc2 " + bookName;
                ps.println(bookNameID);
                String shortBookID = "\\toc3 " + bookCode;
                ps.println(shortBookID);
            }

            ArrayList<FrameTranslation> frameList = sortFrameTranslations(frames);
            int startChunk = 0;
            if(frameList.size() > 0) {
                FrameTranslation frame = frameList.get(0);
                int verseID = Util.strToInt(frame.getId(),0);
                if((verseID == 0)) {
                    String text = frame.body;
                    ps.print(text);
                    startChunk++;
                }
           }

            int chapterInt = Util.strToInt(chapter.getId(),0);
            if(chapterInt != 0) {
                String chapterNumber = "\\c " + chapter.getId();
                ps.println(chapterNumber);
            }

            if((chapter.title != null) && (!chapter.title.isEmpty())) {
                String chapterTitle = "\\cl " + chapter.title;
                ps.println(chapterTitle);
            }

            if( (chapter.reference != null) && (!chapter.reference.isEmpty())) {
                String chapterRef = "\\cd " + chapter.reference;
                ps.println(chapterRef);
            }

            for (int i = startChunk; i < frameList.size(); i++) {
                FrameTranslation frame = frameList.get(i);
                String text = frame.body;

                // text
                ps.println("\\s5"); // section marker
                ps.print(text);
            }
        }

        ps.close();

        File destFile = null;
        if( (chapterFile != null) && (outputFileName != null) ) {
            File outputFile = new File(destinationFolder, outputFileName);
            boolean success = FileUtilities.moveOrCopyQuietly(chapterFile, outputFile);
            if(success) {
                destFile = outputFile;
            }
        }
        FileUtilities.deleteQuietly(tempDir);
        return destFile;
    }

    /**
     * sort the frames
     * @param frames
     * @return
     */
    public static ArrayList<FrameTranslation> sortFrameTranslations(FrameTranslation[] frames) {
        // sort frames
        ArrayList<FrameTranslation> frameList = new ArrayList<FrameTranslation>(Arrays.asList(frames));
        Collections.sort(frameList, new Comparator<FrameTranslation>() { // do numeric sort
            @Override
            public int compare(FrameTranslation lhs, FrameTranslation rhs) {
                Integer lhInt = getChunkOrder(lhs.getId());
                Integer rhInt = getChunkOrder(rhs.getId());
                return lhInt.compareTo(rhInt);
            }
        });
        return frameList;
    }

    /**
     *
     * @param chunkID
     * @return
     */
    public static Integer getChunkOrder(String chunkID) {
        if("00".equals(chunkID)) { // special treatment for chunk 00 to move to end of list
            return 99999;
        }
        if("back".equalsIgnoreCase(chunkID)){
            return 9999999; // back is moved to very end
        }
        return Util.strToInt(chunkID, -1); // if not numeric, then will move to top of list and leave order unchanged
    }

    /**
     * class to extract book data as well as default USFM output file name
     */
    public static class BookData {
        private String defaultUsfmFileName;
        private String bookCode;
        private String languageId;
        private String languageName;
        private String bookName;
        private String bookTitle;

        public BookData(TargetTranslation targetTranslation) {

            bookCode = targetTranslation.getProjectId().toUpperCase();
            languageId = targetTranslation.getTargetLanguageId();
            languageName = targetTranslation.getTargetLanguageName();
            ProjectTranslation projectTranslation = targetTranslation.getProjectTranslation();
            Project project = App.getLibrary().index.getProject(languageId, targetTranslation.getProjectId(), true);

            bookName = bookCode;
            if( (project != null) && (project.name != null)) {
                bookName = project.name;
            }

            bookTitle = "";
            if(projectTranslation != null) {
                String title = projectTranslation.getTitle();
                if( title != null ) {
                    bookTitle = title.trim();
                }
            }
            if(bookTitle.isEmpty()) {
                bookTitle = bookCode;
            }

            // generate file name
            defaultUsfmFileName = System.currentTimeMillis() + "_" + languageId + "_" + bookCode + "_" + bookName + ".usfm";
        }

        public String getDefaultUsfmFileName() {
            return defaultUsfmFileName;
        }

        public String getBookCode() {
            return bookCode;
        }

        public String getLanguageId() {
            return languageId;
        }

        public String getLanguageName() {
            return languageName;
        }

        public String getBookName() {
            return bookName;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public static BookData generate(TargetTranslation targetTranslation) {
           return new BookData( targetTranslation);
        }
    }
}
