package com.door43.translationstudio.core;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a page of questions in the new language questionnaire
 */
public class NewLanguagePage {
    private Map<Long, NewLanguageQuestion> questions = new LinkedHashMap<>();

    /**
     * Adds a question to this page
     * @param question
     */
    public void addQuestion(NewLanguageQuestion question) {
        this.questions.put(question.id, question);
    }

    /**
     * Checks if this page contains the question
     * @param id
     * @return
     */
    public boolean containsQuestion(long id) {
        return this.questions.containsKey(id);
    }

    /**
     * Returns the questions on this page
     * @return
     */
    public List<NewLanguageQuestion> getQuestions() {
        return new ArrayList<>(this.questions.values());
    }

    /**
     * Returns the number of questions on this page
     * @return
     */
    public int getNumQuestions() {
        return this.questions.size();
    }

    /**
     * Returns the question on this page by position
     * @param position
     * @return
     */
    @Nullable
    public NewLanguageQuestion getQuestion(int position) {
        if(position >= 0 && position < getNumQuestions()) {
            return getQuestions().get(position);
        }
        return null;
    }

    /**
     * Returns the question on this page by id
     * @param id
     * @return
     */
    @Nullable
    public NewLanguageQuestion getQuestionById(long id) {
        return this.questions.get(id);
    }

    /**
     * Returns the index of the question
     * @param questionId
     * @return
     */
    public int indexOf(long questionId) {
        return new ArrayList<>(questions.keySet()).indexOf(questionId);
    }

    /**
     * Removes a question from the page
     * @param questionId
     */
    public void removeQuestion(long questionId) {
        questions.remove(questionId);
    }
}
