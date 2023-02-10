/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.x.root

import androidx.compose.runtime.Stable
import io.element.android.features.rageshake.crash.ui.CrashDetectionState
import io.element.android.features.rageshake.crash.ui.aCrashDetectionState
import io.element.android.features.rageshake.detection.RageshakeDetectionState
import io.element.android.features.rageshake.detection.aRageshakeDetectionState

@Stable
data class RootState(
    val isShowkaseButtonVisible: Boolean,
    val rageshakeDetectionState: RageshakeDetectionState,
    val crashDetectionState: CrashDetectionState,
    val eventSink: (RootEvents) -> Unit
)

fun aRootState() = RootState(
    isShowkaseButtonVisible = false,
    rageshakeDetectionState = aRageshakeDetectionState(),
    crashDetectionState = aCrashDetectionState(),
    eventSink = {}
)
