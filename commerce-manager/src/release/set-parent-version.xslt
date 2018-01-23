<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan" xmlns:m="http://maven.apache.org/POM/4.0.0" xmlns="http://maven.apache.org/POM/4.0.0" exclude-result-prefixes="xalan xsl m">
	<!-- Even though cmclient is a separate artifact in a separate reactor, we sneakily bump the parent version it relies on to the current release version and then to the next SNAPSHOT version. We can't use Maven to do this since neither the previous SNAPSHOT version nor the upcoming next SNAPSHOT version are available to be resolved. -->
	<!-- See https://github.elasticpath.net/DevOps/releases-build-jobs/blob/master/Hive/610.x.0-CMClient-Release.xml for usage. -->

	<xsl:output method="xml" encoding="UTF-8" indent="yes" xalan:indent-amount="4" />

	<xsl:preserve-space elements="*" />

	<xsl:param name="parent.version" />

	<!-- Update version of parent to parent.version. -->
	<xsl:template match="m:parent/m:version">
		<xsl:copy>
			<xsl:value-of select="$parent.version" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>

