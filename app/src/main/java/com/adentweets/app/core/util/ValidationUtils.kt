package com.adentweets.app.core.util

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidUsername(username: String): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9_]{4,15}$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^\\+?[0-9]{10,15}$"))
    }

    fun isValidPostContent(content: String): Boolean {
        return content.isNotBlank() && content.length <= Constants.MAX_POST_LENGTH
    }

    fun isValidDisplayName(name: String): Boolean {
        return name.isNotBlank() && name.length in 2..50
    }

    fun isValidBio(bio: String): Boolean {
        return bio.length <= 160
    }

    fun isValidWebsite(url: String): Boolean {
        if (url.isBlank()) return true
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }

    fun isNotEmpty(value: String): Boolean = value.isNotBlank()
}