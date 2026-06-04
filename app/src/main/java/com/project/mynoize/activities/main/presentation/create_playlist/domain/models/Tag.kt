package com.project.mynoize.activities.main.presentation.create_playlist.domain.models

enum class Tag(val displayName: String) {

    // Mood
    HAPPY("Happy"),
    SAD("Sad"),
    MELANCHOLIC("Melancholic"),
    ENERGETIC("Energetic"),
    CALM("Calm"),
    ROMANTIC("Romantic"),
    AGGRESSIVE("Aggressive"),
    NOSTALGIC("Nostalgic"),
    DREAMY("Dreamy"),
    DARK("Dark"),
    UPLIFTING("Uplifting"),
    MYSTERIOUS("Mysterious"),
    PLAYFUL("Playful"),
    TENSE("Tense"),
    HOPEFUL("Hopeful"),
    ANGRY("Angry"),
    EUPHORIC("Euphoric"),
    MELANCHOLY("Melancholy"),
    CHILL("Chill"),
    BITTERSWEET("Bittersweet"),

    // Activity
    WORKOUT("Workout"),
    RUNNING("Running"),
    YOGA("Yoga"),
    MEDITATION("Meditation"),
    STUDYING("Studying"),
    WORKING("Working"),
    DRIVING("Driving"),
    COOKING("Cooking"),
    SLEEPING("Sleeping"),
    PARTY("Party"),
    ROAD_TRIP("Road Trip"),
    GAMING("Gaming"),
    READING("Reading"),

    // Time of Day
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening"),
    NIGHT("Night"),
    LATE_NIGHT("Late Night"),
    SUNRISE("Sunrise"),
    SUNSET("Sunset"),

    // Season / Weather
    SPRING("Spring"),
    SUMMER("Summer"),
    AUTUMN("Autumn"),
    WINTER("Winter"),
    RAINY("Rainy"),
    SUNNY("Sunny"),
    SNOWY("Snowy"),
    STORMY("Stormy"),

    // Setting / Vibe
    BEACH("Beach"),
    CITY("City"),
    NATURE("Nature"),
    FOREST("Forest"),
    COFFEE_SHOP("Coffee Shop"),
    UNDERGROUND("Underground"),
    ROOFTOP("Rooftop"),
    COUNTRYSIDE("Countryside"),
    SPACE("Space"),
    VINTAGE("Vintage"),
    FUTURISTIC("Futuristic"),
    URBAN("Urban"),
    COZY("Cozy"),

    // Sound / Production
    ACOUSTIC("Acoustic"),
    INSTRUMENTAL("Instrumental"),
    LIVE("Live"),
    LO_FI("Lo-Fi"),
    HI_FI("Hi-Fi"),
    BASS_HEAVY("Bass Heavy"),
    VOCAL_FOCUSED("Vocal Focused"),
    AMBIENT("Ambient"),
    EXPERIMENTAL("Experimental"),
    MINIMALIST("Minimalist"),
    ORCHESTRAL("Orchestral"),
    A_CAPPELLA("A Cappella"),

    // Tempo
    SLOW("Slow"),
    MID_TEMPO("Mid Tempo"),
    FAST("Fast"),
    UPBEAT("Upbeat"),
    DOWNTEMPO("Downtempo"),

    // Audience / Social
    SOLO("Solo"),
    DATE_NIGHT("Date Night"),
    FAMILY("Family"),
    FRIENDS("Friends"),
    CROWD("Crowd"),
    KIDS("Kids"),

    // Cultural / Origin
    AFROBEAT("Afrobeat"),
    LATIN_VIBES("Latin Vibes"),
    ASIAN_INSPIRED("Asian Inspired"),
    MIDDLE_EASTERN("Middle Eastern"),
    CELTIC("Celtic"),
    NORDIC("Nordic"),

    // Decade Feel
    RETRO_60S("Retro 60s"),
    RETRO_70S("Retro 70s"),
    RETRO_80S("Retro 80s"),
    RETRO_90S("Retro 90s"),
    EARLY_2000S("Early 2000s"),
    MODERN("Modern"),

    // Thematic
    LOVE("Love"),
    HEARTBREAK("Heartbreak"),
    EMPOWERMENT("Empowerment"),
    REBELLION("Rebellion"),
    SPIRITUAL("Spiritual"),
    POLITICAL("Political"),
    STORYTELLING("Storytelling"),
    FEEL_GOOD("Feel Good"),
    HYPE("Hype"),
    SOULFUL("Soulful");

    companion object {
        fun fromDisplayName(name: String): Tag? =
            entries.find { it.displayName == name }
    }
}