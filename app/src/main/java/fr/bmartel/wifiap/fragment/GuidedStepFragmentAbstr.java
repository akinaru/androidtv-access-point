/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.wifiap.fragment;

import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.List;

/**
 * Abstract for all GuidedStep fragments
 *
 * @author Bertrand Martel
 */
public abstract class GuidedStepFragmentAbstr extends GuidedStepFragment {

    private static final int CHECK_SET_ID = 1;

    /**
     * Add a a Guided Action initially checked
     *
     * @param actions list of GuidedActions
     * @param title   GuidedAction title
     * @param checked define if GuidedAction is checked
     */
    protected void addCheckedAction(List<GuidedAction> actions, String title, boolean checked) {
        GuidedAction guidedAction = new GuidedAction.Builder()
                .title(title)
                .checkSetId(CHECK_SET_ID)
                .build();
        guidedAction.setChecked(checked);
        actions.add(guidedAction);
    }

    /**
     * Add a GuidedAction
     *
     * @param actions list of GuidedActions
     * @param id      GuidedAction id
     * @param title   GuidedAction title
     * @param desc    GuidedAction description
     */
    protected GuidedAction addAction(List<GuidedAction> actions, long id, String title, String desc) {

        GuidedAction action = new GuidedAction.Builder()
                .id(id)
                .title(title)
                .description(desc)
                .build();
        actions.add(action);
        return action;
    }

    /**
     * add an EditableGuidedAction
     *
     * @param actions list of GuidedActions
     * @param id      GuidedAction id
     * @param title   GuidedAction title
     * @param desc    GuidedAction description
     */
    protected void addEditableAction(List<GuidedAction> actions, long id, String title, String desc) {
        actions.add(new GuidedAction.Builder()
                .id(id)
                .title(title)
                .editable(true)
                .editTitle(desc)
                .description(desc)
                .build());
    }

}
