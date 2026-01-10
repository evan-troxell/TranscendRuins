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

package com.transcendruins.assets;

import java.util.function.Consumer;
import java.util.function.Function;

import com.transcendruins.utilities.PropertyHolder;

/**
 * <code>Instance</code>: A class representing any structure which should be
 * updated through the attribute stack system.
 */
public abstract class Instance extends PropertyHolder {

    /**
     * Generated an updated attribute, optionally returning a default value if the
     * input attribute is <code>null</code>.
     * 
     * @param <K>        The input value type.
     * @param <T>        The output value type.
     * @param value      <code>K</code>: The input value to apply.
     * @param generator  <code>Function&lt;K, T&gt;</code>: The standard generation
     *                   method to use. This will only be used if the
     *                   <code>value</code> parameter is not <code>null</code>.
     * @param ifNull     <code>T</code>: The value to return if the
     *                   <code>value</code> parameter is null. Note that this value
     *                   is not passed into the <code>generator</code> parameter.
     * @param attributes <code>Attributes</code>: The attributes from which the
     *                   attribute originated.
     * @param ifBase     <code>T</code>: The value to return if the
     *                   <code>value</code> parameter is null and the base attribute
     *                   set is attempting to apply the attribute. Note that this
     *                   value is not passed into the <code>generator</code>
     *                   parameter.
     * @return <code>T</code>: The resulting value.
     */
    public final <K, T> T calculateAttribute(K value, Function<K, T> generator, T ifNull, Attributes attributes,
            T ifBase) {

        if (value != null) {

            return generator.apply(value);
        }

        return (attributes.isBase()) ? ifBase : ifNull;
    }

    /**
     * Generates an updated attribute, optionally returning a default value if the
     * input attribute is <code>null</code>.
     * 
     * @param <K>       The input value type.
     * @param <T>       The output value type.
     * @param value     <code>K</code>: The input value to apply.
     * @param generator <code>Function&lt;K, T&gt;</code>: The standard generation
     *                  method to use. This will only be used if the
     *                  <code>value</code> parameter is not <code>null</code>.
     * @param ifNull    <code>T</code>: The value to return if the
     *                  <code>value</code> parameter is null. Note that this value
     *                  is not passed into the <code>generator</code> parameter.
     * @return <code>T</code>: The resulting value.
     */
    public final <K, T> T calculateAttribute(K value, Function<K, T> generator, T ifNull) {

        if (value != null) {

            return generator.apply(value);
        }

        return ifNull;
    }

    /**
     * Generates an updated attribute, optionally returning a default value if the
     * input attribute is <code>null</code>.
     * 
     * @param <K>        The value type.
     * @param value      <code>K</code>: The input value to apply.
     * @param ifNull     <code>K</code>: The value to return if the
     *                   <code>value</code> parameter is null.
     * @param attributes <code>Attributes</code>: The attributes from which the
     *                   attribute originated.
     * @param ifBase     <code>K</code>: The value to return if the
     *                   <code>value</code> parameter is null and the base attribute
     *                   set is attempting to apply the attribute.
     * @return <code>K</code>: The resulting value.
     */
    public final <K> K calculateAttribute(K value, K ifNull, Attributes attributes, K ifBase) {

        if (value != null) {

            return value;
        }

        return (attributes.isBase()) ? ifBase : ifNull;
    }

    /**
     * Generates an updated attribute, optionally returning a default value if the
     * input attribute is <code>null</code>.
     * 
     * @param <K>    The value type.
     * @param value  <code>K</code>: The input value to apply.
     * @param ifNull <code>K</code>: The value to return if the <code>value</code>
     *               parameter is null.
     * @return <code>K</code>: The resulting value.
     */
    public final <K> K calculateAttribute(K value, K ifNull) {

        if (value != null) {

            return value;
        }

        return ifNull;
    }

    public final <K> void computeAttribute(K value, Consumer<K> operation, K ifNull, Attributes attributes, K ifBase) {

        if (value != null) {

            operation.accept(value);
        } else {

            operation.accept(attributes.isBase() ? ifBase : ifNull);
        }
    }

    public final <K> void computeAttribute(K value, Consumer<K> operation, K ifNull) {

        if (value != null) {

            operation.accept(value);
        } else {

            operation.accept(ifNull);
        }
    }

    /**
     * Computes an operation using an input attribute, optionally using a default
     * parameter if the input attribute is <code>null</code>.
     * 
     * @param <K>        The value type.
     * @param value      <code>K</code>: The input attribute to the operation. This
     *                   will only be used if it is not <code>null</code>.
     * @param operation  <code>Consumer&lt;K&gt;</code>: The operation to run. This
     *                   takes a single argument for the attribute, and will either
     *                   be the <code>value</code> parameter or the
     *                   <code>ifBase</code> parameter.
     * @param attributes <code>Attributes</code>: The attributes from which the
     *                   attribute originated.
     * @param ifBase     <code>K</code>: The value to use in the operation if the
     *                   <code>value</code> parameter is <code>null</code> and the
     *                   base attribute set is attempting to apply the attribute.
     */
    public final <K> void computeAttribute(K value, Consumer<K> operation, Attributes attributes, K ifBase) {

        if (value != null) {

            operation.accept(value);
        } else if (attributes.isBase()) {

            operation.accept(ifBase);
        }
    }

    /**
     * Computes an operation using an input attribute if the input attribute is not
     * <code>null</code>.
     * 
     * @param <K>       The value type.
     * @param value     <code>K</code>: The input attribute to the operation. This
     *                  will only be used if it is not <code>null</code>.
     * @param operation <code>Consumer&lt;K&gt;</code>: The operation to run. This
     *                  takes a single argument for the attribute, and will only run
     *                  if the <code>value</code> parameter is not
     *                  <code>null</code>.
     */
    public final <K> void computeAttribute(K value, Consumer<K> operation) {

        if (value != null) {

            operation.accept(value);
        }
    }

    /**
     * Applies an attribute set to this <code>Instance</code> instance.
     * 
     * @param attributeSet <code>Attributes</code>: The attributes to apply.
     */
    public abstract void applyAttributes(Attributes attributeSet);
}
