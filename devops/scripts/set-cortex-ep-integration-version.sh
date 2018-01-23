#!/bin/bash

set -e
readonly PROGNAME=$(basename -- $0)
readonly ARGS="$@"
readonly WORKSPACE=$(pwd)

# The reason we are creating the script this way is so that we have a way to simply
# use a single "settings.xml" file for all projects when we get to that point.
# Ideally, we should also be supplying all these projects in the same zip or side
# by side; the script accommodates that.
usage() {
  cat << EOF

Usage: ./${PROGNAME} [-h] [-b] [-s <settings-file-location>] <commerce-engine_version> <cortex-ep-integration_version> <cortex-ep-integration_directory> <extensions_directory>

Sets the cortex-ep-integration project version and dependencies in Extensions.

Options:
	-h, --help
		Displays this help page
	-b, --build
		Builds the projects upon completing the version setting. By building the projects after setting their versions, you verify the new version builds correctly.
	-s, --maven-settings <settings-file-location>
		Use a Maven settings.xml file that is not your default from your .m2 directory.

Examples:

	$ ./${PROGNAME} 615.0.0-SNAPSHOT 0-SNAPSHOT cortex-ep-integration extensions
		This will be the most common usage (relative paths and your settings file in your .m2 directory).
		$ ./${PROGNAME} -s extensions/maven/settings.xml 615.0.0-SNAPSHOT 0-SNAPSHOT cortex-ep-integration extensions
		This approach lets you use a different settings.xml than the default maven settings specified in your .m2 directory.

	$ ./${PROGNAME} -s /home/ep-user/code/extensions/maven/ep-settings.xml 615.0.0-SNAPSHOT 0-SNAPSHOT /home/ep-user/code/cortex-ep-integration /home/ep-user/code/extensions
		Both the settings.xml file and the project directories can be specified with absolute paths.
		This is the syntax you would use for a Linux user.

	$ ./${PROGNAME} -s c:/Users/ep-user/code/extensions/maven/ep-settings.xml 615.0.0-SNAPSHOT 0-SNAPSHOT c:/Users/ep-user/code/cortex-ep-integration c:/Users/ep-user/code/extensions
		This is the syntax you would use for a Windows user when using absolute paths.
		You can mix and match absolute paths and relative paths if that's what you're into.

	$ ./${PROGNAME} -b 615.0.0-SNAPSHOT 0-SNAPSHOT cortex-ep-integration extensions
		Specifying the -b option will build the projects after setting their versions to confirm the version set doesn't break the projects.
EOF

  exit 1
}

# Wraps sed to work on Windows because "sed -i" destroys file permissions.
exec_sed_update() {
  local script=$1
  local input_file=$2
  local temp_file="${input_file}.bak"

  sed "${script}" "${input_file}" > "${temp_file}"
  mv -f "${temp_file}" "${input_file}"
}

set_cortex_ep_integration_dependencies() {
  local project_dir=$1
  local commerce_engine_version=$2

  exec_sed_update "s,\(<dce.version>\).*\(</dce.version>\),\1${commerce_engine_version}\2,g" "${project_dir}"/pom.xml
}

set_cortex_ep_integration_versions() {
  local project_dir=$1
  local version=$2

  mvn $MAVEN_SETTINGS \
    -f "${project_dir}"/pom.xml \
    org.codehaus.mojo:versions-maven-plugin:2.1:set \
    org.codehaus.mojo:versions-maven-plugin:2.1:commit \
    -DnewVersion="${version}"
}

build_cortex_ep_integration() {
  local project_dir=$1

  mvn $MAVEN_SETTINGS \
    -f "${project_dir}"/pom.xml \
    clean install \
    -DskipAllTests
}

set_extensions_dependencies() {
  local extensions_dir=$1
  local cortex_ep_integration_version=$2

  exec_sed_update "s,\(<cortex.ep.integration.version>\).*\(</cortex.ep.integration.version>\),\1${cortex_ep_integration_version}\2,g" "${extensions_dir}"/cortex/pom.xml
}

build_extensions() {
  local project_dir=$1

  mvn $MAVEN_SETTINGS \
    -f "${project_dir}"/pom.xml \
    clean install \
    -DskipAllTests
}

main() {
  # This magic number is derived from the 4 mandatory operands:
  # commerce-engine_version, cortex-ep-integration_version, cortex-ep-integration_directory, extensions_directory
  if [[ $# -ne 4 ]]; then
    usage
  fi

  local commerce_engine_version=$1
  local cortex_ep_integration_version=$2
  local cortex_ep_integration_dir=$3
  local extensions_dir=$4

  set_cortex_ep_integration_dependencies "${cortex_ep_integration_dir}" $commerce_engine_version
  set_cortex_ep_integration_versions "${cortex_ep_integration_dir}" $cortex_ep_integration_version
  set_extensions_dependencies "${extensions_dir}" $cortex_ep_integration_version

  if [[ $BUILD_FLAG == true ]]; then
    build_cortex_ep_integration "${cortex_ep_integration_dir}"
    build_extensions "${extensions_dir}"
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
