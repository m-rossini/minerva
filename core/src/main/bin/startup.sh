echo
echo \#\#
echo \#\# Copyright 2004-2005 Auster Solutions do Brasil
echo \#\#
echo \#\#  --- Minerva Report Manager v1.0.0 ---
echo \#\#
echo

function checkAnt {
	if [[ -z "${ANT_HOME}" || ! ( -r ${ANT_HOME}/lib/ant.jar ) ]]; then
		echo ANT_HOME is set incorrectly or Ant could not be located. Please set ANT_HOME.
		exit 1
	fi
}

function checkJava {
	if [[ -z "${JAVA_HOME}" || ! ( -r ${JAVA_HOME}/bin/java ) ]]; then
		echo JAVA_HOME is set incorrectly or java could not be located. Please set JAVA_HOME.
		exit 1
	fi
}

function printUsage {
   	echo
   	echo Usage: startup.sh <configuration-file>
   	echo
    exit 1
}

function printHelp {
  ${JAVA_HOME}/bin/java -cp ${ANT_HOME}/lib/ant-launcher.jar -Dant.home=$ANT_HOME -Dbasedir=$MINERVA_HOME org.apache.tools.ant.launch.Launcher -file ${MINERVA_HOME}/bin/minerva-run.xml server.help
	exit 1
}

checkAnt
checkJava

ORIGINAL_PATH=${PWD}
MINERVA_HOME=$( dirname $0 )
cd $MINERVA_HOME
MINERVA_HOME=${PWD%%"/bin"}
cd ${ORIGINAL_PATH}

if [[ -z "$1" ]]; then
	printUsage
fi

if [[ "$1" = "-help" || "$1" = "--help" || "$1" = "-h" ]]; then 
    printHelp
fi
CONFIGURATION_FILE=$1



# Starting Minerva
echo
echo Starting Minerva Server
echo

${JAVA_HOME}/bin/java -cp ${ANT_HOME}/lib/ant-launcher.jar -Dant.home=$ANT_HOME -Dbasedir=$MINERVA_HOME -Dconfiguration.file=$CONFIGURATION_FILE org.apache.tools.ant.launch.Launcher -file ${BILLCHECKOUT_HOME}/bin/minerva-run.xml

echo
echo [ Finished ]
echo
