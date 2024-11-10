plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Projekat"
include("spec")
include("testApp")
include("csvImpl")
include("excelImpl")
include("txtImpl")
include("pdfImpl")


include("storageSpec")
include("storageImpl")
