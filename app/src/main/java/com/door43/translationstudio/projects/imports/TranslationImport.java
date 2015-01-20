package com.door43.translationstudio.projects.imports;

import com.door43.translationstudio.projects.Language;
import com.door43.translationstudio.util.ListMap;
import com.door43.translationstudio.util.MainContext;

import java.io.File;

/**
 * Created by joel on 1/19/2015.
 */
public class TranslationImport extends ImportRequest {
    public final File sourceDir;
    public final String languageId;

    public TranslationImport(String languageId, File sourceDir) {
        this.languageId = languageId;
        this.sourceDir = sourceDir;
    }

    /**
     * Adds a chapter import request this this translation
     * @param request
     */
    public void addChapterImport(ChapterImport request) {
        super.addChildImportRequest(request);
    }

    @Override
    public String getId() {
        return languageId;
    }

    @Override
    public String getTitle() {
        Language l = MainContext.getContext().getSharedProjectManager().getLanguage(languageId);
        if(l != null) {
            return l.getName();
        } else {
            return languageId;
        }
    }
}
