package com.elasticpath.cortex.dce.navigations

import cucumber.api.DataTable
import org.json.JSONArray

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks
import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I follow the root navigations link$') { ->
	client.GET("/")
			.navigations()
			.stopIfFailure()
}

And(~'^the expected navigation list exactly matches the following$') { DataTable navigationTable ->
	def navigationList = navigationTable.asList(String)
	def actualCategoryList = []

//	findAll loops through everything inside the links. "it" is the first item return. Then it looks for "element"
// 	and retrieve the uri and get the "name" value to store in the list.
	client.body.links.findAll {
		if (it.rel == "element") {
			client.GET(it.uri)
			actualCategoryList.add(client["name"])
		}
	}

// Verifies if the list is exactly the same and in the same order.
	assertThat(actualCategoryList)
			.containsExactlyElementsOf(navigationList)
}

And(~'the navigation node (.+) should contain exactly the following child nodes$') { String nodeName, DataTable childNodesTable ->
	def childNodeList = childNodesTable.asList(String)
	def actualChildNodeList = []

	client.findCategory(nodeName)
			.stopIfFailure()
	client.body.links.findAll {
		if (it.rel == "child") {
			client.GET(it.uri)
			actualChildNodeList.add(client["name"])
		}
	}
	assertThat(actualChildNodeList)
			.containsExactlyElementsOf(childNodeList)
}

And(~'the child node (.+) should contain the following sub child nodes$') { String childNode, DataTable subChildNodesTable ->
	def subChildNodeList = subChildNodesTable.asList(String)
	def actualSubChildNodeList = []

//	Navigate back to parent node to retrieve all child nodes since the previous step stops at the last child node.
	client.parent()

	client.body.links.findAll {
		if (it.rel == "child") {
			client.GET(it.uri)
			if (client["name"] == childNode) {
				client.body.links.findAll {
					if (it.rel == "child") {
						client.GET(it.uri)
						actualSubChildNodeList.add(client["name"])
					}
				}
			}
		}
	}

	assertThat(actualSubChildNodeList)
			.containsExactlyElementsOf(subChildNodeList)
}

Then(~'the items are listed in the follow order') { DataTable dataTable ->
	ArrayList<String> orderedDisplayName = dataTable.asList(String)
	JSONArray jsonArray = (JSONArray) client["links"];

	//This assert prevents an exception if the expected table has more items than the number of links
	//TODO: find a way to deal with the "next" link in the jsonArray
	assertThat(orderedDisplayName)
			.as("The number of items in list was less than the number of links")
			.size()
			.isLessThanOrEqualTo(jsonArray.length())

	def listUri = client.body.self.uri
	int i = 0

	for (def displayName : orderedDisplayName) {
		client.GET(jsonArray.get(i).getAt("uri"))
				.definition()
		assertThat(client["display-name"])
				.as("Element $i was not as expected")
				.isEqualTo(displayName)
		i++
	}
	client.GET(listUri)

}

