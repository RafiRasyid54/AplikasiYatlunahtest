package com.yatlunah.app.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {
    const val PROJECT_URL = "https://opmmcdsffhesnlolkcfa.supabase.co"
    const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9wbW1jZHNmZmhlc25sb2xrY2ZhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU3NTk0NDAsImV4cCI6MjA5MTMzNTQ0MH0.P-7FgZazo0ht4ONGIGhDJ62G7rVF9McFugFHXVjVxj0"

    val bucketName = "setoran_audio"
    val client = createSupabaseClient(
        supabaseUrl = PROJECT_URL,
        supabaseKey = ANON_KEY
    ) {
        // ✅ Cukup tulis Storage seperti ini, jangan pakai SessionSource
        install(Storage)
    }
}