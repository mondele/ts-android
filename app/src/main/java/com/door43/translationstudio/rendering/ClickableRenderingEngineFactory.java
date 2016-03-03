package com.door43.translationstudio.rendering;

import com.door43.translationstudio.core.TranslationFormat;
import com.door43.translationstudio.spannables.Span;

/**
 * Created by blm on 3/3/16.
 */
public class ClickableRenderingEngineFactory {

    /**
     * create appropriate rendering engine for format and add click listeners
     * @param format
     * @param defaultFormat
     * @param verseClickListener
     * @param noteClickListener
     * @return
     */
    public static ClickableRenderingEngine create(TranslationFormat format, TranslationFormat defaultFormat, Span.OnClickListener verseClickListener, Span.OnClickListener noteClickListener) {

        ClickableRenderingEngine renderer = null;

        if( (format != TranslationFormat.USFM) && (format != TranslationFormat.USX) ) {
            format = defaultFormat;
        }

        if(format == TranslationFormat.USFM) {
            renderer = new USFMRenderer(verseClickListener, noteClickListener);
        } if(format == TranslationFormat.USX)  {
            renderer = new USXRenderer(verseClickListener, noteClickListener);
        }

        return renderer;
    }

    /**
     * test if this is a clickable format
     * @param format
     * @return
     */
    public static boolean isClickableFormat(TranslationFormat format) {
        return (format == TranslationFormat.USX) || (format == TranslationFormat.USFM);
    }
}
