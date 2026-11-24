plugins {
    alias(libs.plugins.android.library)
}

// Dominio veterinario: clientes (dueños), mascotas, productos, pedidos y consultas
android {
    namespace = "com.example.veterinariaapp.datos"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // java.time (LocalDate) existe desde API 26; el desugaring lo habilita en API 23-25
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(project(":utilidades"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit)
}
