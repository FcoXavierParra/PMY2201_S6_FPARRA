plugins {
    alias(libs.plugins.android.library)
}

// Funciones generales reutilizables: validaciones y formato. No depende de otros módulos.
android {
    namespace = "com.example.veterinariaapp.utilidades"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit)
}
