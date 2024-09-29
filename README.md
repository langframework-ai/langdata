# Lang Data

Lang Data is a comprehensive Java library designed to simplify data handling and connectivity across a wide range of connectors. Whether working with files, integrating external models, accessing SaaS platforms, or managing vector data, Lang Data provides all the tools you need to handle these tasks efficiently.

## Table of Contents

1. [Features](#features)
2. [Getting Started](#getting-started)
3. [Build and Format](#build-and-format)

## Features

- **Versatile Connectors**: Connect to files, external models, SaaS platforms, and vector data sources.
- **Easy Integration**: Simplifies data handling and integration tasks in Java applications.
- **Extensible and Scalable**: Designed to be extensible and handle various data formats and sources.

## Getting Started

### Prerequisites

- Java 17+
- Gradle

### Installation

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/langframework-ai/langdata.git
   cd langdata
   ```

2. **Add Dependency**:

   Add the following to your `build.gradle` file to include Lang Data in your project:

   ```groovy
   dependencies {
       implementation 'ai.langframework.langdata:lang-data:0.0.1'
   }
   ```

3. **Build the Project**:

   To build the project, run:

   ```bash
   ./gradlew build
   ```

## Usage

Lang Data provides various connectors for different use cases. Below is a basic example demonstrating how to use a file connector:

```java
import ai.langframework.langdata.connectors.FileDataConnector;

public class Main {
    public static void main(String[] args) {
        FileDataConnector connector = new FileDataConnector("path/to/your/file");
        System.out.println(connector.fetchData());
    }
}
```

## Build and Format

- **Apply Code Formatting**:

  Use Spotless to apply the project's code style:

   ```bash
   ./gradlew spotlessApply
   ```

- **Build the Project**:

   ```bash
   ./gradlew build
   ```