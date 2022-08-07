package dev.riccardo.productsstore

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml

import java.nio.file.Paths

class VersionParserTask extends DefaultTask {
    static final DEFAULT_VERSION = "1.0.0-SNAPSHOT"

    @TaskAction
    String parse() {
        Map<String, Object> applicationProperties = new Yaml().load(
            project.file(
                Paths.get("src", "main", "resources", "application.yaml")
            ).text
        )
        applicationProperties.microservice?.version ?: DEFAULT_VERSION
    }
}
