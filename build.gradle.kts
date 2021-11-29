import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.0"
	kotlin("plugin.spring") version "1.6.0"
	kotlin("plugin.lombok") version "1.6.0"

}

group = "com.theta"
version = "0.4.2"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("mysql:mysql-connector-java:8.0.25")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-validation:2.3.0.RELEASE")
	implementation("io.jsonwebtoken:jjwt-api:0.10.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.10.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.10.2")
	implementation("org.springframework.boot:spring-boot-starter-security:2.2.0.RELEASE")
	compileOnly("org.projectlombok:lombok:1.18.22")
	annotationProcessor("org.projectlombok:lombok:1.18.22")

	testCompileOnly("org.projectlombok:lombok:1.18.22")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
	// testing
	testImplementation("io.cucumber:cucumber-junit:4.2.0")
	implementation("io.cucumber:cucumber-java8:4.2.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	implementation("io.cucumber:cucumber-spring:4.2.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.rest-assured:rest-assured:3.0.0")


}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
