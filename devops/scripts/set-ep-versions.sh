#! /bin/bash

set -e
readonly PROGNAME=$(basename -- $0)
readonly ARGS="$@"
readonly WORKSPACE=$(pwd)

#### HELPER FUNCTIONS ####

# Wraps sed to work on Windows because "sed -i" destroys file permissions.  
exec_sed_update() {
	local script=$1
	local inputfile=$2
	local tempfile="${inputfile}.bak"

	sed "${script}" "${inputfile}" > "${tempfile}"
	mv -f "${tempfile}" "${inputfile}"
}

set_property_version() {
	local property=$1
	local value=$2
	local file=$3

	exec_sed_update "s|\(.*<${property}>\).*\(</${property}>.*\)|\1${value}\2|" ${file}
}

update_parent_version() {
	local version=$1
	local pom_file=$2

	exec_sed_update "\#<parent>#,\#</parent># s|<version>.*</version>|<version>${version}</version>|" ${pom_file}
}

#### VERSION UPDATE FUNCTIONS ####

set_ce_versions() {
	local version=$1

	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}" -f "${ce_directory}"/pom.xml
}

build_ce() {
	mvn $MAVEN_SETTINGS -f "${ce_directory}"/pom.xml clean install -DskipAllTests
}

# Installs the parent poms which maven would otherwise complain about not having
maven_dependency_trick() {
	mvn $MAVEN_SETTINGS -f "${ce_directory}"/pom.xml clean install -N
	mvn $MAVEN_SETTINGS -f "${extensions_dir}"/pom.xml clean install -N
}

set_extensions_dependencies() {
	local version=$1

	# Set commerce-engine version
	update_parent_version "${version}" "${extensions_dir}"/pom.xml

	set_property_version "platform.version" "${version}" "${extensions_dir}"/pom.xml
	set_property_version "dce.version"      "${version}" "${extensions_dir}"/cortex/*-commerce-engine-wrapper/pom.xml
	set_property_version "dce.version"      "${version}" "${extensions_dir}"/cortex/*-cortex-webapp/pom.xml
	set_property_version "dce.version"      "${version}" "${extensions_dir}"/cortex/system-tests/cucumber/pom.xml

	# Set commerce-manager version
	set_property_version "cmclient.platform.feature.version" "${version}" "${extensions_dir}"/cm/pom.xml
	set_property_version "cmclient.platform.feature.version" "${version}" "${extensions_dir}"/cm/ext-cm-libs/pom.xml
}

build_extensions() {
	mvn $MAVEN_SETTINGS -f "${extensions_dir}"/pom.xml clean install
}

set_extensions_versions() {
	local version=$1

	# Set extension version
	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}" -f "${extensions_dir}"/pom.xml

	# Set extension Cortex version (separated due to POM hierarchy)
	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}" -f "${extensions_dir}"/cortex/pom.xml
	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}" -f "${extensions_dir}"/cortex/*-commerce-engine-wrapper/pom.xml 
	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}" -f "${extensions_dir}"/cortex/*-cortex-webapp/pom.xml
	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}" -f "${extensions_dir}"/cortex/system-tests/cucumber/pom.xml

	set_property_version "ep-commerce-engine-wrapper-version" "${version}" "${extensions_dir}"/cortex/*-cortex-webapp/pom.xml

	# Set extension CM version (separated due to Tycho reactor)
	mvn $MAVEN_SETTINGS org.eclipse.tycho:tycho-versions-plugin:0.17.0:set-version -Dtycho.mode=maven -DnewVersion="${version}" -f "${extensions_dir}"/cm/pom.xml

	update_parent_version "${version}" "${extensions_dir}"/cm/system-tests/selenium/pom.xml
	update_parent_version "${version}" "${extensions_dir}"/cm/*-cm-webapp-runner/pom.xml
	update_parent_version "${version}" "${extensions_dir}"/cm/ext-cm-invoker/pom.xml
	update_parent_version "${version}" "${extensions_dir}"/cm/pom.xml
}

set_devops_versions() {
	local version=$1

	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}" -f "${devops_directory}"/pom.xml

	update_parent_version "${version}" "${devops_directory}"/pom.xml
}

set_cmc_versions() {
	local version=$1

	mvn $MAVEN_SETTINGS org.codehaus.mojo:versions-maven-plugin:2.1:set org.codehaus.mojo:versions-maven-plugin:2.1:commit -DnewVersion="${version}"  -f "${cmc_directory}"/cm-libs/pom.xml

	mvn $MAVEN_SETTINGS org.eclipse.tycho:ep-tycho-versions-plugin:set-version \
		-f "${cmc_directory}"/pom.xml \
		-Dtycho.mode=maven \
		-DnewVersion="${version}" \
		-Dartifacts=com.elasticpath:ep-settings,com.elasticpath:ep-persistence-openjpa,com.elasticpath:ep-persistence-api,com.elasticpath:ep-base,com.elasticpath:ep-cache,com.elasticpath:ep-core,com.elasticpath.cmclient:com.elasticpath.cmclient.libs,com.elasticpath.cmclient:com.elasticpath.cmclient.testlibs

	mvn $MAVEN_SETTINGS org.eclipse.tycho:tycho-versions-plugin:0.17.0:set-version \
		-f "${cmc_directory}"/pom.xml \
		-Dtycho.mode=maven \
		-DnewVersion="${version}"

	update_parent_version ${version} "${cmc_directory}"/cm-invoker/pom.xml
	update_parent_version ${version} "${cmc_directory}"/pom.xml
	update_parent_version ${version} "${cmc_directory}"/cm-libs/pom.xml
}

build_cmc() {
	mvn $MAVEN_SETTINGS -f "${cmc_directory}"/pom.xml clean package
}

# The reason we are creating the script this way is so that we have a way to simply use a single "settings.xml" file for all projects when we get to that point. Ideally, we should also be supplying all these projects in the same zip or side by side; the script accommodates that.
usage() {
	cat << EOF

	Usage: ./${PROGNAME} [-h] [-b] [-s <settings-file-location>] <ce_version> <extensions_version> <commerce-engine-directory> <extensions-directory> <commerce-manager-directory> <devops-directory>

	Sets the project versions and dependencies in Commerce Engine, Extensions, Commerce Manager Client, and DevOps.

	Options:
		-h, --help
			Displays this help page
		-b, --build
			Builds the projects upon completing the version setting. By building the projects after setting their versions, you verify the new version builds correctly.
		-s, --maven-settings <settings-file-location>
			Use a Maven settings.xml file that is not your default from your .m2 directory.

	Examples:

		$ ./${PROGNAME} 612.0.0-SNAPSHOT 0-SNAPSHOT commerce-engine extensions commerce-manager devops
			This will be the most common usage (relative paths and your settings file in your .m2 directory).

		$ ./${PROGNAME} -s extensions/maven/settings.xml 612.0.0-SNAPSHOT 0-SNAPSHOT commerce-engine extensions commerce-manager devops
			This approach lets you use a different settings.xml than the default maven settings specified in your .m2 directory.

		$ ./${PROGNAME} -s /home/ep-user/code/extensions/maven/ep-settings.xml 612.0.0-SNAPSHOT 0-SNAPSHOT /home/ep-user/code/commerce-engine /home/ep-user/code/extensions /home/ep-user/code/commerce-manager /home/ep-user/code/devops
			Both the settings.xml file and the project directories can be specified with absolute paths.
			This is the syntax you would use for a Linux user.

		$ ./${PROGNAME} -s c:/Users/ep-user/code/extensions/maven/ep-settings.xml 612.0.0-SNAPSHOT 0-SNAPSHOT c:/Users/ep-user/code/commerce-engine c:/Users/ep-user/code/extensions c:/Users/ep-user/code/commerce-manager c:/Users/ep-user/code/devops
			This is the syntax you would use for a Windows user when using absolute paths.
			You can mix and match absolute paths and relative paths if that's what you're into.

		$ ./${PROGNAME} -b 612.0.0-SNAPSHOT 0-SNAPSHOT commerce-engine extensions commerce-manager devops
			Specifying the -b option will build the projects after setting their versions to confirm the version set doesn't break the projects.
EOF

	exit 1
}

main() {
	# This magic number is derived from the 6 mandatory operands: ce_version, extensions_version, commerce_engine, extensions, commerce_manager_client, devops
	if [[ $# -ne 6 ]]; then
		usage
	fi

	local ce_version=$1
	local extensions_version=$2
	local ce_directory=$3
	local extensions_dir=$4
	local cmc_directory=$5
	local devops_directory=$6

	maven_dependency_trick
	set_ce_versions "${ce_version}"

	set_cmc_versions "${ce_version}"

	set_extensions_versions "${extensions_version}"
	set_extensions_dependencies "${ce_version}"

	set_devops_versions "${extensions_version}"

	if [[ $BUILD_FLAG == true ]]; then
		build_ce "${settings_file}"
		build_extensions "${settings_file}"
		build_cmc "${settings_file}"
	fi
}

cmdline() {
	for arg
	do
		local delim=""
		case "$arg" in
			#translate --gnu-long-options to -g (short options)
			--build)          args="${args}-b ";;
			--help)           args="${args}-h ";;
			--maven-settings) args="${args}-s ";;
			*) [[ "${arg:0:1}" == "-" ]] || delim="\""
				args="${args}${delim}${arg}${delim} ";;
		esac
	done

	#Reset the positional parameters to the short options
	eval set -- $args

	local MAVEN_SETTINGS=""
	local BUILD_FLAG=false

	while getopts "be:hs:" OPTION
	do
		case $OPTION in
			b)
				BUILD_FLAG=true
				;;
			h)
				usage
				;;
			s)
				MAVEN_SETTINGS="-s $OPTARG"
				;;
		esac
	done

	# shifts all options parsed previously by getopts to get operands not parsed by getopts
	local i=1
	while [[ $# -gt 0 && $i -lt $OPTIND ]]; do
		let i=$i+1
		shift
	done

	main $@
}
cmdline $ARGS
