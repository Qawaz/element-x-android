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

package io.element.android.libraries.textcomposer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.theme.bgSubtleTertiary
import io.element.android.libraries.theme.ElementTheme
import io.element.android.wysiwyg.compose.RichTextEditorDefaults
import io.element.android.wysiwyg.compose.RichTextEditorStyle

internal object ElementRichTextEditorStyle {
    @Composable
    fun create(
        hasFocus: Boolean,
    ) : RichTextEditorStyle {
        val colors = ElementTheme.colors
        val m3colors = MaterialTheme.colorScheme
        val codeCornerRadius = 4.dp
        val codeBorderWidth = 1.dp
        return RichTextEditorDefaults.style(
            text = RichTextEditorDefaults.textStyle(
                color = if (hasFocus) {
                    m3colors.primary
                } else {
                    m3colors.secondary
                }
            ),
            cursor = RichTextEditorDefaults.cursorStyle(
                color = colors.iconAccentTertiary,
            ),
            link = RichTextEditorDefaults.linkStyle(
                color = colors.textLinkExternal,
            ),
            codeBlock = RichTextEditorDefaults.codeBlockStyle(
                leadingMargin = 8.dp,
                background = RichTextEditorDefaults.codeBlockBackgroundStyle(
                    color = colors.bgSubtleTertiary,
                    borderColor = colors.borderInteractiveSecondary,
                    cornerRadius = codeCornerRadius,
                    borderWidth = codeBorderWidth,
                )
            ),
            inlineCode = RichTextEditorDefaults.inlineCodeStyle(
                verticalPadding = 0.dp,
                background = RichTextEditorDefaults.inlineCodeBackgroundStyle(
                    color = colors.bgSubtleTertiary,
                    borderColor = colors.borderInteractiveSecondary,
                    cornerRadius = codeCornerRadius,
                    borderWidth = codeBorderWidth,
                )
            ),
        )
    }
}
