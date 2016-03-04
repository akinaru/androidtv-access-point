/*
 * Wifi Access Point configuration for Android TV
 *
 * Copyright (C) 2016 Bertrand Martel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
