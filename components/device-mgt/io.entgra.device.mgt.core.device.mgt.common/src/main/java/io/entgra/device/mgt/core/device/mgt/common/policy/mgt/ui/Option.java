/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.device.mgt.common.policy.mgt.ui;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Option")
public class Option {

    private String definedValue;
    private String displayingValue;

    @XmlAttribute(name = "value", required = true)
    public String getDefinedValue() {
        return definedValue;
    }

    public void setDefinedValue(String definedValue) {
        this.definedValue = definedValue;
    }

    @XmlAttribute(name = "display", required = true)
    public String getDisplayingValue() {
        return displayingValue;
    }

    public void setDisplayingValue(String displayingValue) {
        this.displayingValue = displayingValue;
    }
}
