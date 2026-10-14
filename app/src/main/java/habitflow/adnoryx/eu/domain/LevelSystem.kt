package habitflow.adnoryx.eu.domain

data class LevelInfo(
    val level: Int,
    val currentLevelXp: Int,
    val requiredXpForNextLevel: Int,
    val progressFraction: Float,
    val totalXp: Int
)

object LevelSystem {
    fun getXpRequiredForLevel(level: Int): Int {
        if (level <= 1) return 100
        return 100 + (level - 1) * 50
    }

    fun calculateLevelInfo(totalXp: Int): LevelInfo {
        var remainingXp = maxOf(0, totalXp)
        var level = 1

        while (true) {
            val req = getXpRequiredForLevel(level)
            if (remainingXp < req) {
                val fraction = if (req > 0) remainingXp.toFloat() / req.toFloat() else 0f
                return LevelInfo(
                    level = level,
                    currentLevelXp = remainingXp,
                    requiredXpForNextLevel = req,
                    progressFraction = fraction.coerceIn(0f, 1f),
                    totalXp = totalXp
                )
            }
            remainingXp -= req
            level++
        }
    }
}
