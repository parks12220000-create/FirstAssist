#!/bin/sh
DIRNAME=$(dirname "$0")
CLASSPATH=$DIRNAME/gradle/wrapper/gradle-wrapper.jar
exec java $GRADLE_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
