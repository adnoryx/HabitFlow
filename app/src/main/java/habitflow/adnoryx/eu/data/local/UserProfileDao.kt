package habitflow.adnoryx.eu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import habitflow.adnoryx.eu.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateProfile(profile: UserProfile)

    @Update
    fun updateProfile(profile: UserProfile): Int

    @Query("DELETE FROM user_profile")
    fun deleteProfile(): Int
}
