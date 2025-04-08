package hbv601g.learningsquare.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE userName = :userName")
    fun getByUserName(userName: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("UPDATE users SET recoveryEmail = :valueToSet WHERE userName = :userName")
    fun updateRecoveryEmail(valueToSet: String, userName: String)

    @Query("UPDATE users SET password = :valueToSet WHERE userName = :userName")
    fun updatePassword(valueToSet: String, userName: String)

    @Delete
    fun delete(user: User)
}