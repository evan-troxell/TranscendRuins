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

package com.transcendruins.rendering;

/**
 * <code>RenderInstance</code>: An interface representing a model to be
 * rendered, paired with its instance context.
 */
@FunctionalInterface
public interface RenderInstance {

    /**
     * Retrieves the polygons of this <code>RenderInstance</code> instance.
     * 
     * @return <code>RenderBuffer</code>: The retrieved polygons.
     */
    public RenderBuffer getPolygons();
}
