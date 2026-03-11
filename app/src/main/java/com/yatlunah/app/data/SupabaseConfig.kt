package com.yatlunah.app.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {
    const val PROJECT_URL = "https://ecueyemrcmaelxwqhbbo.supabase.co"
    const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVjdWV5ZW1yY21hZWx4d3FoYmJvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzMxNzMyNDMsImV4cCI6MjA4ODc0OTI0M30.9I1GbaCWGgLeUSuyyufyy85h1ASB4KYrpsVYDfhbEMs"

    val bucketName = "setoran_audio"
    val client = createSupabaseClient(
        supabaseUrl = PROJECT_URL,
        supabaseKey = ANON_KEY
    ) {
        // ✅ Cukup tulis Storage seperti ini, jangan pakai SessionSource
        install(Storage)
    }
}