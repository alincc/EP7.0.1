package com.elasticpath.cortex.dce

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks
import org.json.JSONArray

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)
/**
 * Shared steps.
 */

String savedUri
String savedRel

def RESPONSE_MAP = ['OK'                    : 200, 'OK, created': 201, 'no content': 204, 'bad request': 400, 'unauthorized': 401,
					'forbidden'             : 403, 'not found': 404, 'method not allowed': 405, 'conflict': 409,
					'unsupported media type': 415, 'server failure': 500]

Then(~'the HTTP status is (.+)$') { String response ->
	assertThat(client.response.status)
			.as("The response status is not as expected")
			.isEqualTo(RESPONSE_MAP[response])
}

Then(~'the HTTP status code is (.+)') { int code ->
	assertThat(client.response.status)
			.as("The response status is not as expected")
			.isEqualTo(code)
}

Then(~'I follow links (.+)') { String resources ->
	/* this step will follow a list of links separated by '->'
	ie. When I follow links cart -> order -> paymentmethodinfo
	 */
	navigateResources(resources)
}

Then(~'I navigate links (.+)') { String resources ->
	/* this step will follow a list of links separated by '->'
	ie. When I follow links cart -> order -> paymentmethodinfo
	 */
	client.GET("/")
	navigateResources(resources)
}

def navigateResources(def resources) {
	List<String> path = new ArrayList<String>()
	path.addAll(resources.split("->"))

	for (String rel : path) {
		def links = client.body.links.findAll { link ->
			link.rel == rel.trim()
		}
		assertThat(links)
				.size()
				.as("rel $rel not found")
				.isGreaterThan(0)

		client.GET(links.get(0).uri)
				.stopIfFailure()
	}
}

Then(~'open the (element|child) with field (.+) of (.+)') { def linkrel, def field, def value ->
	elementExists = false
	client.body.links.find {
		if (it.rel == linkrel) {
			client.GET(it.uri)
			if (client[field] == value) {
				elementResponse = client.save()
				elementExists = true
			}
		}
	}

	assertThat(elementExists)
			.as("$linkrel with $field = $value not found")
			.isTrue()

	client.resume(elementResponse)
}

Then(~'open the (element|child) with field (.+) containing (.+)') { def linkrel, def field, def value ->
	elementExists = false
	client.body.links.find {
		if (it.rel == linkrel) {
			client.GET(it.uri)
			if (client[field].toString().contains(value)) {
				elementResponse = client.save()
				elementExists = true
			}
		}
	}
	assertThat(elementExists)
			.as("$linkrel with $field = $value not found")
			.isTrue()
	client.resume(elementResponse)
}

Then(~'there is an (element|child) with field (.+) of (.+)') { def linkrel, def field, def value ->
	elementExists = false
	def uri = client.body.self.uri
	client.body.links.find {
		if (it.rel == linkrel) {
			client.GET(it.uri)
			if (client[field] == value) {
				elementExists = true
			}
		}
	}

	assertThat(elementExists)
			.as("element with $field = $value not found")
			.isTrue()

	client.GET(uri)
}

Then(~'there is an element with field (.+) containing (.+)') { def field, def value ->
	elementExists = false
	def uri = client.body.self.uri
	client.body.links.find {
		if (it.rel == "element") {
			client.GET(it.uri)
			if (client[field].toString().contains(value)) {
				elementExists = true
			}
		}
	}

	assertThat(elementExists)
			.as("element with $field = $value not found")
			.isTrue()

	client.GET(uri)
}

Then(~'there is no element with field (.+) of (.+)') { def field, def value ->
	elementExists = false
	client.body.links.find {
		if (it.rel == "element") {
			client.GET(it.uri)
			if (client[field] == value) {
				elementResponse = client.save()
				elementExists = true
			}
		}
	}

	assertThat(elementExists)
			.as("element with $field = $value found")
			.isFalse()
}

Then(~'^the field (.+) matches (.+)$') { String field, String regex ->
	assertThat(client[field].toString())
			.as("The field $field is not as expected")
			.matches(regex)
}

Then(~'^the field (.+) contains value (.+)$') { String field, String regex ->
	assertThat(client[field].toString())
			.as("The field $field is not as expected")
			.contains(regex)
}

Then(~'^the field (.+) does not exist') { def field ->
	assertThat(client[field])
			.as("The field $field is not as expected")
			.isEqualTo(null)
}

Then(~'there are no (.+) links') { def rel ->
	def expectedLinks = client.body.links.findAll {
		link ->
			link.rel == rel
	}
	assertThat(expectedLinks)
			.size()
			.as("$rel links found but should not exist: $client.body.links")
			.isEqualTo(0)
}

Then(~'there are (.+) links of rel (.+)') { Integer count, def rel ->
	assertThat(client.body.links.toString().count("rel:$rel"))
			.as("The number of links is not as expected")
			.isEqualTo(count)
}

Then(~'I GET (.+)') { def uri ->
	client.GET(uri)
			.stopIfFailure()
}

Then(~'save the (?:.*) uri') { ->
	savedUri = client.body.self.uri
}

Then(~'attempt to access the (?:.*)') { ->
	client.GET(savedUri)
			.stopIfFailure()
}

Then(~'attempt to delete the (?:.*)') { ->
	client.DELETE(savedUri)
}

Then(~'return to the saved (?:.*)') { ->
	client.GET(savedUri)
			.stopIfFailure()
}

Then(~'save link rel (?:.*) uri') { ->
	savedRel = client.body.links[0].uri
}

Then(~'post to the saved (?:.*)') { ->
	client.POST(savedRel, [:])
}

Then(~'the uri of (.+) matches the uri of saved (.+) uri') { def currentResource, def savedResource ->
	assertThat(client.body.self.uri)
			.as("uri is not as expected")
			.isEqualTo(savedUri)
}

Then(~'try to access (.+) from scope (.+) on scope (.+)') { def uriType, String originalScope, String newScope ->
	client.GET(savedUri.toString().replace(originalScope, newScope))
			.stopIfFailure()
}

Then(~'the saved (.+) uri has rel (.+)') { def saveduritype, def newRel ->
	navigateResources(newRel)
	assertThat(client.body.self.uri)
			.as("uri is not as expected")
			.isEqualTo(savedUri)
}

Then(~'attempt to select the original users address') { ->
	def selectactionUri = client.body.self.uri
	client.GET(selectactionUri + savedUri)
}

Then(~'^the field (.+) does not contain value (.+)$') { String field, String regex ->
	assertThat(client[field].toString())
			.as("Field $field is not as expected")
			.doesNotContain(regex)
}

Then(~'^the array field (.+) is empty$') { String field ->
	assertThat(client[field].toString())
			.as("Field $field is not empty")
			.isEqualTo("[]")
}

Then(~/^the fields have the following values$/) { DataTable dataTable ->
	def keyValueList = dataTable.asList(KeyValue)

	for (KeyValue keyValue : keyValueList) {
		def map = keyValue.getFormMap()
		map.each {
			assertThat(client[it.key])
					.as("Field " + it.key + " is not as expected")
					.isEqualTo(it.value)
		}
	}
}

Then(~/^the fields contain the following values$/) { DataTable dataTable ->
	def keyValueList = dataTable.asList(KeyValue)

	for (KeyValue keyValue : keyValueList) {
		def map = keyValue.getFormMap()
		map.each {
			assertThat(client[it.key].toString())
					.as("Field " + it.key + " is not as expected")
					.contains(it.value)
		}
	}
}

public class KeyValue {
	String key
	String value

	Map<String, String> formMap

	def getFormMap() {
		formMap = new HashMap<String, String>()
		formMap.put(key, value)
		return formMap;
	}
}


Then(~'there is an item with display-name (.+)') { def displayName ->
	client.findItemByDisplayName(displayName)
			.definition()
			.stopIfFailure()
	assertThat(client["display-name"])
			.as("Item display name is not as expected")
			.isEqualTo(displayName)
}

Then(~'there is not an item with display-name (.+)') { def displayName ->
	Boolean found = false
	client.findElement {
		item ->
			def definition = item.definition()
			if (definition["display-name"] == displayName)
				found = true

	}

	assertThat(found)
			.as("Item $displayName should not exist in element list")
			.isFalse()
}


Then(~/I POST to selectaction and follow$/) { ->
	client.selectaction()
	client.follow()
			.stopIffailure()
}

Then(~/^the (.+) array field (.+) contains$/) { String description, String arrayField, DataTable dataTable ->
	def nameValueList = dataTable.asList(NameValue)

	JSONArray jsonArray = (JSONArray) client[arrayField];

	for (NameValue NameValue : nameValueList) {
		def map = NameValue.nameValueMap()
		map.each {
			boolean nameValueFound = false
			if (jsonArray != null) {
				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					if (jsonArray.get(i).getAt("name") == it.key && jsonArray.get(i).getAt("value").toString().equals(it.value)) {
						nameValueFound = true;
					}
				}
			}
			assertThat(nameValueFound)
					.as("name: " + it.key + " value: " + it.value + " not found")
					.isTrue()
		}
	}
}

public class NameValue {
	String name
	String value

	Map<String, String> nameValueMap

	def nameValueMap() {
		nameValueMap = new HashMap<String, String>()
		nameValueMap.put(name, value)
		return nameValueMap;
	}
}

Then(~'attempting a DELETE on the (.+)') { def description ->
	client.DELETE(client.body.self.uri)
}

Then(~'follow the response') { ->
	client.follow()
}

Then(~'I POST to (.+) with request body (.+)') { def uri, def body ->
	client.POST(uri, body)
			.stopIfFailure()
}
Then(~'the response is empty') { ->
	assertThat(client.body)
			.as("The response is not empty")
			.isEqualTo(null)
}
