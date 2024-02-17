package server

object LeaguesToScrape {
    val dsvLeagueIds = mapOf(
        299 to mapOf(
            "C" to listOf("")
        ),
        297 to mapOf(
            "V" to listOf("A", "B")
        ),
        301 to mapOf(
            "V" to listOf("A", "B", "C"),
            "Z" to listOf("D", "E", "F")
        ),
        188 to mapOf(
            "L" to listOf("")
        ),

        30 to mapOf(
            "L" to listOf("")
        ),
        61 to mapOf(
            "C" to listOf("")
        ),

        70 to mapOf(
            "L" to listOf("")
        ),
        244 to mapOf(
            "L" to listOf("")
        ),
        71 to mapOf(
            "C" to listOf("")
        ),

        17 to mapOf(
            "L" to listOf(""),
        ),
        36 to mapOf(
            "C" to listOf("")
        ),

        77 to mapOf(
            "L" to listOf("")
        ),

        39 to mapOf(
            "L" to listOf("")
        ),
        38 to mapOf(
            "C" to listOf("")
        ),
        216 to mapOf(
            "L" to listOf("")
        ),

        90 to mapOf(
            "C" to listOf("")
        ),
        45 to mapOf(
            "L" to listOf("")
        ),

        158 to mapOf(
            "L" to listOf("")
        ),

        307 to mapOf(
            "V" to listOf("A", "B"),
        ),

        171 to mapOf(
            "P" to listOf(""),
            "V" to listOf("A", "B")
        ),
        172 to mapOf(
            "V" to listOf("")
        ),
        122 to mapOf(
            "L" to listOf("")
        ),

        46 to mapOf(
            "L" to listOf("")
        ),

        196 to mapOf(
            "L" to listOf("")
        ),
        132 to mapOf(
            "C" to listOf("")
        ),
        198 to mapOf(
            "V" to listOf("A", "B")
        ),
        240 to mapOf(
            "L" to listOf("")
        ),

        274 to mapOf(
            "L" to listOf("A", "B")
        ),

        303 to mapOf(
            "L" to listOf("")
        ),
        163 to mapOf(
            "L" to listOf("")
        ),

        321 to mapOf(
            "L" to listOf("")
        )
    )

    val leagueNames = mapOf(
        299 to "DSV Pokal",
        297 to "Bundesliga",
        301 to "U18 Bundesliga",
        188 to "DSV Supercup",

        30 to "2. Liga Nord",
        61 to "NSV Pokal",

        70 to "2. Liga Ost",
        244 to "LGO U16 OMW",
        71 to "OSV Pokal",

        17 to "2. Liga Süd",
        36 to "SSV Pokal",

        77 to "2. Liga West",

        39 to "Oberliga Baden-Württemberg",
        38 to "Baden-Würrtemberg Pokal",
        216 to "U18 Baden-Württemberg",

        90 to "Bayern Pokal",
        45 to "Oberliga Bayern",

        158 to "Berlin Meisterschaft",

        307 to "Hamburg Liga",

        171 to "HSV Pokal",
        172 to "Oberliga Hessen",
        122 to "U16 Hessen",

        46 to "Oberliga Niedersachsen",

        196 to "Oberliga NRW",
        132 to "NRW Pokal",
        198 to "U16 NRW",
        240 to "U18 NRW",

        274 to "Regionalliga Südwest",

        303 to "Oberliga Sachsen",
        163 to "U16 Sachsen",

        321 to "Landesliga Thüringen"
    )

    val leagueRegions = mapOf(
        299 to "National",
        297 to "National",
        301 to "National",
        188 to "National",

        30 to "Nord",
        61 to "Nord",

        70 to "Ost",
        244 to "Ost",
        71 to "Ost",

        17 to "Süd",
        36 to "Süd",

        77 to "West",

        39 to "Baden-Württemberg",
        38 to "Baden-Württemberg",
        216 to "Baden-Württemberg",

        90 to "Bayern",
        45 to "Bayern",

        158 to "Berlin",

        307 to "Hamburg",

        171 to "Hessen",
        172 to "Hessen",
        122 to "Hessen",

        46 to "Niedersachsen",

        196 to "NRW",
        132 to "NRW",
        198 to "NRW",
        240 to "NRW",

        274 to "Rehinland-Pfalz/Saarland",

        303 to "Sachsen",
        163 to "Sachsen",

        321 to "Thüringen"
    )
}