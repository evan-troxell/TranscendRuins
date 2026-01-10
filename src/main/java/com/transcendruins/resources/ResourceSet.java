/* Copyright 2026 Evan Troxell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.transcendruins.resources;

import com.transcendruins.resources.languages.LanguageSet;
import com.transcendruins.resources.sounds.SoundSet;
import com.transcendruins.resources.styles.StyleSet;
import com.transcendruins.resources.textures.TextureSet;
import com.transcendruins.utilities.files.TracedPath;

public final class ResourceSet {

    private final LanguageSet languages;

    public final LanguageSet getLanguages() {

        return languages;
    }

    private final SoundSet sounds;

    public final SoundSet getSounds() {

        return sounds;
    }

    private final TextureSet textures;

    public final TextureSet getTextures() {

        return textures;
    }

    private final StyleSet style;

    public final StyleSet getStyle() {

        return style;
    }

    public ResourceSet(TracedPath path) {

        TracedPath languagePath = path.extend("language");
        languages = new LanguageSet(languagePath);

        TracedPath soundPath = path.extend("sound");
        sounds = new SoundSet(soundPath);

        TracedPath texturePath = path.extend("texture");
        textures = new TextureSet(texturePath);

        TracedPath stylesPath = path.extend("style.json");
        style = new StyleSet(stylesPath);
    }
}
