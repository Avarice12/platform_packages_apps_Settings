/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.homepage;

import android.content.Context;
import android.util.Log;

import androidx.collection.ArraySet;

import com.android.settings.homepage.conditional.ConditionContextualCardController;
import com.android.settings.homepage.conditional.ConditionContextualCardRenderer;

import java.util.Set;

/**
 * This is a fragment scoped singleton holding a set of {@link ContextualCardController} and
 * {@link ContextualCardRenderer}.
 */
public class ControllerRendererPool {

    private static final String TAG = "ControllerRendererPool";

    private final Set<ContextualCardController> mControllers;
    private final Set<ContextualCardRenderer> mRenderers;

    public ControllerRendererPool() {
        mControllers = new ArraySet<>();
        mRenderers = new ArraySet<>();
    }

    public <T extends ContextualCardController> T getController(Context context,
            @ContextualCard.CardType int cardType) {
        final Class<? extends ContextualCardController> clz =
                ContextualCardLookupTable.getCardControllerClass(cardType);
        for (ContextualCardController controller : mControllers) {
            if (controller.getClass() == clz) {
                Log.d(TAG, "Controller is already there.");
                return (T) controller;
            }
        }

        final ContextualCardController controller = createCardController(context, clz);
        if (controller != null) {
            mControllers.add(controller);
        }
        return (T) controller;
    }

    public Set<ContextualCardController> getControllers() {
        return mControllers;
    }

    public ContextualCardRenderer getRenderer(Context context, @ContextualCard.CardType int cardType) {
        final Class<? extends ContextualCardRenderer> clz =
                ContextualCardLookupTable.getCardRendererClasses(cardType);
        for (ContextualCardRenderer renderer : mRenderers) {
            if (renderer.getClass() == clz) {
                Log.d(TAG, "Renderer is already there.");
                return renderer;
            }
        }

        final ContextualCardRenderer renderer = createCardRenderer(context, clz);
        if (renderer != null) {
            mRenderers.add(renderer);
        }
        return renderer;
    }

    private ContextualCardController createCardController(Context context,
            Class<? extends ContextualCardController> clz) {
        if (ConditionContextualCardController.class == clz) {
            return new ConditionContextualCardController(context);
        }
        return null;
    }

    private ContextualCardRenderer createCardRenderer(Context context, Class<?> clz) {
        if (ConditionContextualCardRenderer.class == clz) {
            return new ConditionContextualCardRenderer(context, this /*controllerRendererPool*/);
        }
        return null;
    }

}
