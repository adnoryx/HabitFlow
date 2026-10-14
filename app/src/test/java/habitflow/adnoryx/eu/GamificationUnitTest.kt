package habitflow.adnoryx.eu

import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.domain.LevelSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationUnitTest {

    @Test
    fun testLevelSystemCalculation() {
        val infoLvl1 = LevelSystem.calculateLevelInfo(0)
        assertEquals(1, infoLvl1.level)
        assertEquals(0, infoLvl1.currentLevelXp)
        assertEquals(100, infoLvl1.requiredXpForNextLevel)

        val infoLvl2 = LevelSystem.calculateLevelInfo(100)
        assertEquals(2, infoLvl2.level)
        assertEquals(0, infoLvl2.currentLevelXp)
        assertEquals(150, infoLvl2.requiredXpForNextLevel)

        val infoLvl3 = LevelSystem.calculateLevelInfo(250)
        assertEquals(3, infoLvl3.level)
        assertEquals(0, infoLvl3.currentLevelXp)

        var accumulatedXp = 0
        for (lvl in 1..100) {
            accumulatedXp += LevelSystem.getXpRequiredForLevel(lvl)
        }
        val infoLvl101 = LevelSystem.calculateLevelInfo(accumulatedXp)
        assertEquals(101, infoLvl101.level)
    }

    @Test
    fun testDayFreezeRules() {
        var profile = UserProfile(
            totalFreezes = 3,
            freezesUsedToday = 0
        )

        fun canUseFreeze(p: UserProfile): Boolean {
            return p.totalFreezes > 0 && p.freezesUsedToday < 1
        }

        assertTrue(canUseFreeze(profile))

        profile = profile.copy(
            totalFreezes = profile.totalFreezes - 1,
            freezesUsedToday = 1
        )

        assertFalse(canUseFreeze(profile))
    }
}
