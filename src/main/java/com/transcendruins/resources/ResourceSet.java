/* Copyright 2025 Evan Troxell
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.transcendruins.assets.AssetType;
import com.transcendruins.resources.languages.Language;
import com.transcendruins.resources.languages.LanguageSet;
import com.transcendruins.resources.sounds.Sound;
import com.transcendruins.resources.sounds.SoundSet;
import com.transcendruins.resources.textures.Texture;
import com.transcendruins.resources.textures.TextureSet;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.immutable.ImmutableMap;

public class ResourceSet {

    private final LanguageSet languages;

    private final SoundSet sounds;

    private final TextureSet textures;

    public ResourceSet(TracedPath path) {

        TracedPath languagePath = path.extend("languages");
        languages = new LanguageSet(languagePath);

        TracedPath soundPath = path.extend("sounds");
        sounds = new SoundSet(soundPath);

        TracedPath texturePath = path.extend("textures");
        textures = new TextureSet(texturePath);
    }

    public static ImmutableMap<String, ImmutableMap<String, String>> compileLanguages(
            List<ResourceSet> resources) {

        HashMap<String, ArrayList<Language>> stack = new HashMap<>();

        for (ResourceSet set : resources) {

            for (Map.Entry<String, Language> languageEntry : set.languages.getLanguages().entrySet()) {

                ArrayList<Language> languages = stack.computeIfAbsent(languageEntry.getKey(), _ -> new ArrayList<>());
                languages.add(languageEntry.getValue());
            }
        }

        HashMap<String, ImmutableMap<String, String>> compiled = new HashMap<>();
        for (Map.Entry<String, ArrayList<Language>> languageStack : stack.entrySet()) {

            HashMap<String, String> mapped = new HashMap<>();
            for (Language map : languageStack.getValue()) {

                mapped.putAll(map.getMappings());
            }
            compiled.put(languageStack.getKey(), new ImmutableMap<>(mapped));
        }

        return new ImmutableMap<>(compiled);
    }

    public static ImmutableMap<AssetType, ImmutableMap<String, Texture>> compileTextures(
            List<ResourceSet> resources) {

        return new ImmutableMap<>(
                AssetType
                        .createAssetMap(type -> new ImmutableMap<>(resources.stream().map(resource -> resource.textures)
                                .flatMap(textureSet -> textureSet.getTextures().get(type).entrySet().stream())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (_, replacement) -> replacement,
                                        HashMap::new)))));
    }

    public static ImmutableMap<AssetType, ImmutableMap<String, Sound>> compileSounds(List<ResourceSet> resources) {

        return new ImmutableMap<>(
                AssetType.createAssetMap(type -> new ImmutableMap<>(resources.stream().map(resource -> resource.sounds)
                        .flatMap(soundSet -> soundSet.getSounds().get(type).entrySet().stream())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (_, replacement) -> replacement,
                                HashMap::new)))));
    }
}
