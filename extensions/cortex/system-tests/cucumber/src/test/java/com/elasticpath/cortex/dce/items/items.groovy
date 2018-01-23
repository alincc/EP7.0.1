package com.elasticpath.cortex.dce.items

import cucumber.api.DataTable
import org.json.JSONArray

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

Then(~/^the (SKU|navigation node) attributes contain$/) { def description, DataTable dataTable ->
    def nameValueList = dataTable.asList(NameValue)

    JSONArray jsonArray = (JSONArray) client["details"];

    for (NameValue NameValue : nameValueList) {
        Map<String, List<String>> map = NameValue.getAttributeMap()
        map.each {
            boolean attributeFound = false
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    if (jsonArray.get(i).getAt("name") == it.key) {
                        ArrayList<String> values = it.value
                        def attribute = jsonArray.get(i)
                        if (attribute.getAt("value").toString().contains(values.get(0))
                                && attribute.getAt("display-name").toString().contains(values.get(1))
                                && attribute.getAt("display-value").toString().contains(values.get(2))) {
                            attributeFound = true;
                        }
                    }
                }
            }
            assertThat(attributeFound)
            .as("name: " + it.key + " value: " + it.value + " not found")
            .isTrue()
        }
    }
}

public class NameValue {
    String name
    String value
    String displayName
    String displayValue

    Map<String, List<String>> attributeMap
    List<String> valuesList

    def getAttributeMap() {
        attributeMap = new HashMap<String, List<String>>()
        valuesList = new ArrayList<String>()
        valuesList.add(value)
        valuesList.add(displayName)
        valuesList.add(displayValue)
        attributeMap.put(name, valuesList)
        return attributeMap;
    }
}